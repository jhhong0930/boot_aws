package com.jojoldu.book.web;

import com.jojoldu.book.web.dto.HelloResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {

        return "hello";
    }

    @GetMapping("/hello/dto")
    public HelloResponseDTO helloDto(@RequestParam("name") String name,
                                     @RequestParam("amount") int amount) {

        return new HelloResponseDTO(name, amount);
    }

}
