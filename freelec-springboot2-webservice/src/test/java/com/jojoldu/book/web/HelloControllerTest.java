package com.jojoldu.book.web;

import com.jojoldu.book.config.auth.SecurityConfig;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class) // 스프링 부트 테스트와 JUnit 사이의 연결자 역할
@WebMvcTest(controllers = HelloController.class,
        excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
        })
public class HelloControllerTest {

    @Autowired
    private MockMvc mvc;

    @WithMockUser(roles = "USER")
    @Test
    @DisplayName("hello가 리턴된다")
    public void hello() throws Exception {

        String hello = "hello";

        mvc.perform(get("/hello")) // /hello 주소로 HTTP GET 요청
                .andExpect(status().isOk()) // 200인지 검증
                .andExpect(content().string(hello)); // 반환값이 "hello"가 맞는지 검증

    }

    @WithMockUser(roles = "USER")
    @Test
    @DisplayName("helloDto가 리턴된다")
    public void helloDto() throws Exception {

        String name = "hello";
        int amount = 1000;

        mvc.perform(get("/hello/dto")
                        .param("name", name)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name))) // JSON 응답값을 필드별로 검증, $를 기준으로 명시
                .andExpect(jsonPath("$.amount", is(amount)));

    }
}