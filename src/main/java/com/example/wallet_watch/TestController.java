package com.example.wallet_watch;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;



@RestController
@CrossOrigin(origins = "http://localhost:3000") // Allow requests from React app
public class TestController {
    @GetMapping("/api/test")
    public ResponseEntity<String> testApi() {
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Connection successful!");
        return ResponseEntity.ok("");
    }
}
