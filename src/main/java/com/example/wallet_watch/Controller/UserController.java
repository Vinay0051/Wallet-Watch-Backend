package com.example.wallet_watch.Controller;

import com.example.wallet_watch.Model.User;
import com.example.wallet_watch.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.wallet_watch.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;


    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details")
    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @Operation(summary = "Validate JWT token", description = "Validates the JWT token and returns user details")
    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");

            if (jwtUtil.isTokenExpired(jwtToken)) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Token expired");
                return ResponseEntity.status(401).body(response);
            }

            String email = jwtUtil.extractEmail(jwtToken);

            Optional<User> userOptional = userService.getUserByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                Map<String, Object> response = new HashMap<>();
                response.put("token", jwtToken);
                response.put("name", user.getName());
                response.put("email", user.getEmail());
                response.put("expenseLimit", user.getExpenseLimit());

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "User not found");
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid token");
            return ResponseEntity.status(401).body(response);
        }
    }

    @Operation(summary = "Google login", description = "Logs in a user using Google credentials")
    @PostMapping("/google-login")
    public ResponseEntity<Map<String, Object>> googleLogin(@RequestParam String email) {

        Optional<User> userOptional = userService.getUserByEmail(email);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();

        } else {
            user = new User();
            user.setEmail(email);
            user.setName(email);
            user.setPassword("password");
            user.setExpenseLimit(10000.0);

            user = userService.createUser(user);
        }

        String token = jwtUtil.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("name", user.getName());
        response.put("expenseLimit", user.getExpenseLimit());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "User login", description = "Logs in a user with email and password")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String email,
            @RequestParam String password) {

        Optional<User> userOptional = userService.login(email, password);

        if (userOptional.isPresent()) {

            String token = jwtUtil.generateToken(email);

            User user = userOptional.get();

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("name", user.getName());
            response.put("expenseLimit", user.getExpenseLimit());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    @Operation(summary = "Update user's expense limit", description = "Updates the expense limit for the authenticated user")
    @PutMapping("/update-expense-limit")
    public ResponseEntity<User> updateExpenseLimit(
            @RequestHeader("Authorization") String token,
            @RequestParam Double newLimit) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        User updatedUser = userService.updateExpenseLimit(email, newLimit);

        return ResponseEntity.ok(updatedUser);
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
//    @Operation(summary = "Get user by email", description = "Retrieves user details by email")
//    @PostMapping("/get-user-by-email")
//    public ResponseEntity<User> fetchUserByEmail(@RequestBody String email) {
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
    //
//    @PostMapping("/login")
//    public ResponseEntity<Map<String, String>> login(
//            @RequestParam String email,
//            @RequestParam String password) {
//        Optional<User> userOptional = userService.login(email, password);
//        if (userOptional.isPresent()) {
//            // Generate JWT token
//            String token = jwtUtil.generateToken(email);
//
//            // Return token in the response
//            Map<String, String> response = new HashMap<>();
//            response.put("token", token);
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.status(401).build(); // Unauthorized
//        }
//    }


