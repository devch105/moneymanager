package com.devch105.moneymanger.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/health","/status"})
public class HomeController {

    @GetMapping
    public String HealthCheck(){
        return "Application is Running";
    }
}
 