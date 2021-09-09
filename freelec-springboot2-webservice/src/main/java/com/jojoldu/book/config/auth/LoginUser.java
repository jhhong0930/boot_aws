package com.jojoldu.book.config.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {

    /*
    @Target(ElementType.PARAMETER)
    - 이 어노테이션이 생성될 수 있는 위치를 지정
    - PARAMETER는 메소드의 파라미터로 선언된 객체에서만 사용할 수 있게 한다

    @interface
    - 이 파일을 어노테이션 클래스로 지정
    - LoginUser라는 이름을 가진 어노테이션이 생성되었다고 볼 수 있다
     */

}
