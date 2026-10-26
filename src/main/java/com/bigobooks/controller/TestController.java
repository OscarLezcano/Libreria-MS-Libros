package com.bigobooks.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('TEST')")
public class TestController {

    @GetMapping("/auxilio")
    public String auxilio() {
        System.out.println("auxilio");
        return "auxilio";
    }
}