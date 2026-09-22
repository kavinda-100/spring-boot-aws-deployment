package com.kavinda.spring_aws_deploy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;

@RestController
public class BaseController {

    @GetMapping("/")
    public ResponseEntity<HashMap<String, String>> home() {

        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Hello from Spring Boot on AWS!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<HashMap<String, String>> health() {
        HashMap<String, String> response = new HashMap<>();

        response.put("status", "UP");
        response.put("message", "Service is running");
        response.put("timestamp", String.valueOf(Instant.now()));

        return ResponseEntity.ok(response);
    }
}
