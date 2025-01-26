package com.example.wallet_watch.Controller;

import com.example.wallet_watch.Model.User;
import com.example.wallet_watch.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.wallet_watch.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired // Inject JwtUtil
    private JwtUtil jwtUtil;

    // Endpoint to create a new user
    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

//    @GetMapping("/login")
//    public ResponseEntity<User> login(@RequestParam Long user_id, @RequestParam String password) {
//        Optional<User> user1 = userService.getUserById(user_id);
//        if (user1.isPresent()) {
//            if (user1.get().getPassword().equals(password)) {
//                return ResponseEntity.ok(user1.get());
//            }
//            return ResponseEntity.status(401).build();
//        }
//        else{
//            return ResponseEntity.notFound().build();
//        }
//    }
//@GetMapping("/login")
//public ResponseEntity<User> login(
//        @RequestParam String email,
//        @RequestParam String password) {
//    Optional<User> userOptional = userService.login(email, password);
//    if (userOptional.isPresent()) {
//        return ResponseEntity.ok(userOptional.get()); // Return the user if login is successful
//    } else {
//        return ResponseEntity.status(401).build(); // Return 401 Unauthorized if login fails
//    }
//}

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestParam String email,
            @RequestParam String password) {
        Optional<User> userOptional = userService.login(email, password);
        if (userOptional.isPresent()) {
            // Generate JWT token
            String token = jwtUtil.generateToken(email);

            // Return token in the response
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
    }

//    // Endpoint to get a user by ID
//    @GetMapping("/{userId}")
//    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
//        Optional<User> user = userService.getUserById(userId);
//        return user.map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    // Endpoint to update a user's expense limit
//    @PutMapping("/{userId}/expense-limit")
//    public ResponseEntity<User> updateExpenseLimit(
//            @PathVariable Long userId,
//            @RequestParam Double newLimit) {
//        User updatedUser = userService.updateExpenseLimit(userId, newLimit);
//        return ResponseEntity.ok(updatedUser);
//    }
//
//    @GetMapping("/find-by-email/{email}")
//    public ResponseEntity<User> findUserByEmail(@PathVariable String email) {
//        Optional<User> user = userService.getUserByEmail(email);
//
//        // Debug log to check if user exists
//        if (user.isPresent()) {
//            return ResponseEntity.ok(user.get());
//        } else {
//            System.out.println("No user found with email: " + email); // Debug log
//            return ResponseEntity.notFound().build();
//        }
//    }

    // Endpoint to get user data by email using POST
    @PostMapping("/get-user-by-email")
    public ResponseEntity<User> fetchUserByEmail(@RequestBody String email) {
        Optional<User> user = userService.getUserByEmail(email);

        // Debug log to check if user exists
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            System.out.println("No user found with email: " + email); // Debug log
            return ResponseEntity.notFound().build();
        }
    }
}
