### application-oauth 등록

- 스프링 부트에서는 application-xxx.properties로 만들면 xxx라는 이름의 profile이 생성되어 관리가 가능하다
- profile=xxx으로 호출하면 해당 properties의 설정들을 가져올 수 있다
- application.properties에 spring.profiles.include=xxx 으로 코드를 추가해주면 된다

```properties
# application.properties
spring.profiles.include=oauth

# application-oauth.properties
# google
spring.security.oauth2.client.registration.google.client-id=클라이언트 ID
spring.security.oauth2.client.registration.google.client-secret=클라이언트 보안 비밀
spring.security.oauth2.client.registration.google.scope=profile,email

# naver
spring.security.oauth2.client.registration.naver.client-id=클라이언트 ID
spring.security.oauth2.client.registration.naver.client-secret=클라이언트 보안 비밀
spring.security.oauth2.client.registration.naver.redirect-uri={baseUrl}/{action}/oauth2/code/{registrationId}
spring.security.oauth2.client.registration.naver.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.naver.scope=name,email,profile_image
spring.security.oauth2.client.registration.naver.client-name=Naver

spring.security.oauth2.client.provider.naver.authorization-uri=https://nid.naver.com/oauth2.0/authorize
spring.security.oauth2.client.provider.naver.token-uri=https://nid.naver.com/oauth2.0/token
spring.security.oauth2.client.provider.naver.user-info-uri=https://openapi.naver.com/v1/nid/me
spring.security.oauth2.client.provider.naver.user-name-attribute=response
```

- scope
  - 기본적으로 scope는 기본값이 openid, profile, email이다
  - openid라는 scope가 있으면 Open Id Provider로 인식하는데 이렇게되면 Open Id Provider인 서비스(구글) 과 그렇지 않은 서비스(네이버/카카오 등)로 나눠서 각각 OAuth2Service를 만들어야 한다
  - 하나의 OAuth2Service로 사용하기 위해 일부러 openid scope를 빼고 등록한다

---

### test 환경의 application.properties

- application.properties는 test에 설정이 없으면 main의 설정을 그대로 가져오기 때문에 테스트 코드를 수행할 때도 적용된다
- application-oauth.properties는 설정을 가져오지 못하기 때문에 테스트 환경을 위한 가짜 설정값을 등록한다

```properties
# src/test/resources/application.properties
spring.jpa.show_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
spring.h2.console.enabled=true
spring.session.store-type=jdbc

# Test OAuth
spring.security.oauth2.client.registration.google.client-id=test
spring.security.oauth2.client.registration.google.client-secret=test
spring.security.oauth2.client.registration.google.scope=profile,email
```

### 테스트에 시큐리티 적용

- API 요청 테스트시 스프링 시큐리티 설정이 인증되지 않은 사용자의 요청은 이동시키기 때문에 임의로 인증된 사용자를 추가하여 API만 테스트해 볼 수 있다.
- build.gradle에 spring-security-test 의존성을 주입한다
- @WithMockUser(roles = "권한")
  - 인증된 가짜 사용자를 만들어서 사용
  - roles에 권한을 추가할 수 있다

- No qualifying bean of type CustomOAuth2UserService
  - @WebMvcTest는 CustomOAuth2UserService를 스캔하지 않는다
  - @WebMvcTest는 @Repository, @Service, @Component를 스캔하지 않는다
  - 따라서 SecurityConfig를 생성하기 위해 필요한  CustomOAuth2UserService를 읽을 수 없으므로 스캔대상에서 SecurityConfig를 제거한다

- At least one JPA metamodel must be present!

  - @EnableJpaAuditing을 사용하기 위해서는 최소 하나 이상의 @Entity 클래스가 필요하다
  - @SpringBootApplication과 함께 있다보니 @WebMvcTest에서도 스캔이 되어버리므로 둘을 분리한다

  ```java
  // congih/JpaConfig
  @Configuration
  @EnableJpaAuditing
  public class JpaConfig{}
  ```

  

  