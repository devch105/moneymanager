package com.devch105.moneymanger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/health","/status"})
public class HomeController {

    @GetMapping
    public ResponseEntity<String> HealthCheck(){
        System.out.println("Health check is Running");
        return  ResponseEntity.ok("Application is Running");
    }
}
 