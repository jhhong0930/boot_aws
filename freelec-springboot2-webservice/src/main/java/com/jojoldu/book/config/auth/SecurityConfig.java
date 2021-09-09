package com.jojoldu.book.config.auth;

import com.jojoldu.book.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity // Spring Security 설정 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                // h2-console 화면을 사용하기 위해 해당 옵션들 비활성화
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                // URL별 권한 관리를 설정하는 옵션의 시작점
                // authorizeRequests가 선언되어야만 andMatchers 옵션을 사용할 수 있다
                .authorizeRequests()
                // 권한 관리 대상을 지정하는 옵션
                // URL, HTTP 메소드별로 관리가 가능하다
                // "/" 등 지정된 URL은 permitAll() 옵션으로 전체 열람 권한 설정
                .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile").permitAll()
                // "/api/v1/**" 주소를 가진 API는 USER 권한을 가진 사람만 가능하도록 설정
                .antMatchers("/api/v1/**").hasRole(Role.USER.name())
                // 설정된 값들 이외 나머지 URL을 나타낸다
                // authenticated를 추가하면 나머지 URL은 모두 인증된 사용자에게만 허용하게 설정
                .anyRequest().authenticated()
                .and()
                // 로그아웃 기능에 대한 설정의 시작점
                .logout()
                // 로그하웃 성공시 / 주소로 이동
                .logoutSuccessUrl("/")
                .and()
                // OAuth 2 로그인 기능에 대한 설정의 시작점
                .oauth2Login()
                // OAuth 2 로그인 성공 이후 사용자 정보를 가져올 때의 설정들을 담당
                .userInfoEndpoint()
                // 소셜 로그인 성공 시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록
                // 리소스 서버에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시할 수 있다
                .userService(customOAuth2UserService);

    }
}
