### JPA 관련 어노테이션

- @Entity
  - 테이블과 링크될 클래스임을 나타낸다
  - 기본값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍으로 테이블 이름을 매칭한다
  - ex) SalseManager -> sales_manager
- @Id
  - 해당 테이블의 Primary Key 필드를 나타낸다
- @GeneretedValue
  - PK의 생성 규칙
  - 스프링 부트 2.0 에서는 GenerationType.IDENTITY 옵션을 추가해야 auto_increment가 된다
- @Column
  - 테이블의 칼럼을 나타내며 생략 가능
  - 기본값 외에 추가로 변경이 필요한 옵션이 있으면 사용한다

Entity 클래스에는 절대 Setter 메소드를 만들지 않는다
setter 메소드가 있으면 해당 클래스의 인스턴스 값들이 언제 어디서 변하는지 코드상으로 명확히 구분할 수 없다.

```java
// 잘못된 사용 예
public class Order {
    public void setStatus(boolean status) {
        this.status = status;
    }
}
public void 주문서비스의_취소이벤트() {
    order.setStatus(false);
}

// 올바른 사용 예
public class Order {
    public void cancelOrder() {
        this.status = false;
    }
}
public void 주문서비스의_취소이벤트() {
    order.cancelOrder();
}
```

이처럼 Setter가 없는 경우에는 생성자를 통해 최종값을 채운후 DB에 삽입하며, 값 변경이 필요한 경우 해당 이벤트에 맞는 public 메소드를 호출하여 변경한다
생성자 대신에 @Builder를 사용하면 지금 채워야 할 필드가 무엇인지 명확히 지정할 수 있다.

```java
Example.builder()
    .a(a)
    .b(b)
    .build();
```

- JpaRepository 생성
  - MyBatis에서 Dao라고 불리는 DB 계층 접근자
  - 인터페이스로 생성하고 JpaRepository<Entity 클래스, PK타입>을 상속한다
  - Entity 클래스와 기본 Entity Repository는 함께 위치해야 하므로 보통 도메인 패키지에서 함께 관리한다

### API 만들기

-  API를 만들기 위해 총 3개의 클래스가 필요하다
  - Request 데이터를 받을 Dto
  - API 요청을 받을 Controller
  - 트랜잭션, 도메인 기능 간의 순서를 보장하는 Service
- Service는 트랜잭션, 도메인 간 순서 보장의 역할만 할 뿐 비즈니스 로직을 처리하지 않는다

### Spring의 웹 계층

- Web Layer
  - 컨트롤러와 뷰 템플릿 영역
  - 필터, 인터셉터, 컨트롤러 어드바이스 등 외부 요청과 응답에 대한 전반적인 영역
- Service Layer
  - @Service에 사용되는 서비스 영역
  - 일반적으로 Controller와 Dao 중간 영역에서 사용된다
  - @Transactinal이 사용되어야 하는 영역이다
- Repository Layer
  - Database와 같이 데이터 저장소에 접근하는 영역
  - Dao(Data Access Object) 영역으로 이해하면 쉽다
- Dtos
  - Dto(Data Transfer Object)는 계층 간에 데이터 교환을 위한 객체이며 Dtos는 이들의 영역을 말한다
  - 뷰 템플릿 엔진에서 사용될 객체 또는 Repository Layer에서 결과로 넘겨준 객체 등이 해당된다
- Domain Model
  - 도메인이라 불리는 개발 대상을 모든 사람이 동일한 관점에서 이해할 수 있고 공유할 수 있도록 단순화시킨 것
  - @Entity가 사용된 영역 역시 도메인 모델이다
  - 무조건 데이터베이스의 테이블과 관계가 있어야만 하는 것은 아니다
  - VO처럼 값 객체들도 이 영역에 해당되기 때문이다
- Web, Service, Repository, Dto, Domain 중 비즈니스 처리를 담당해야 할 곳은 바로 Domain이다

```java
// 기존에 서비스로 처리하던 방식을 트랜잭션 스크립트라고 한다
// 모든 로직이 서비스 클래스 내부에서 처리되다보니 서비스 계층이 무의미하며
// 객체란 단순히 데이터 덩어리 역할만 하게 된다
@Transactonal
public Order cancelOrder(int orderId) {
    // 1) DB로부터 주문정보, 결제정보, 배송정보 조회
    OrdersDto order = ordersDao.selectOrders(orderId);
    BillingDto billing = bilingDao.selectBilling(orderId);
    DeliveryDto delivery = deliveryDao.selectDelivery(orderId);
    
    // 2) 배송 취소를 해야 하는지 확인
    String deliveryStatus = delivery.getStatus();
    
    // 3) 만약 배송 중이라면 배송 취소로 변경
    if ("IN_PROGRESS".equals(deliveryStatus)) {
        delivery.setStatus("CANCEL");
        deliveryDao.update(delivery);
    }
    
    // 4) 각 테이블에 취소 상태 Update
    order.setStatus("CANCEL");
    ordersDao.update(order);
    
    billing.setStatus("CANCEL");
    deliveryDao.update(billing);
    
    return order;
}

// order, billing, delivery가 각자 본인의 취소 이벤트 처리를 하며
// 서비스 메소드는 트랜잭션과 도메인 간의 순서만 보장해 준다
@Transactonal
public Order cancelOrder(int orderId) {
    // 1)
    OrdersDto order = ordersRepository.findById(orderId);
    BillingDto billing = billingRepository.findByOrderId(orderId);
    DeliveryDto delivery = deliveryRepository.findByOrderId(orderId);
    
    // 2~3)
    delivery.cancel();
    
    // 4)
    order.cancel();
    billing.cancel();
    
    return order;
}
```

### JPA Auditing으로 생성시간/수정시간 자동화

- JPA Auditing을 이용하여 거의 매 테이블마다 존재하는 생성시간과 수정시간을 따로 빼서 관리할 수 있다
- 기존에 사용하던 Date를 개선한 LocalDate를 사용한다
  - Date와 Calendar 클래스의 문제점:
    - 불변 객체가 아니기 떄문에 멀티스레드 환경에서 언제든 문제가 발생할 수 있다.
    - Calendar의 월 값은 0부터 시작한다 (Calendar.OCTOBER의 숫자 값은 9)

```java
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

}
```

- @MappedSuperclass
  - JPA Entity 클래스들이 BaseTimeEntity를 상속할 경우 필드들도 칼럼으로 인식하도록 한다
- @EntityListeners(AuditingEntityListener.class)
  - BaseTimeEntity 클래스에 Auditing 기능을 포함시킨다
- @CreatedDate
  - Entity가 생성되어 저잘될 떄 시간이 자동 저장된다
- @LastModifiedDate
  - 조회한 Entity의 값을 변경할 때 시간이 자동 저장된다

