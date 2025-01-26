package com.example.wallet_watch.Service;

import com.example.wallet_watch.Model.User;
import com.example.wallet_watch.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Create a new user
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with the given email already exists.");
        }
        return userRepository.save(user);
    }

    // Retrieve a user by ID
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    // Retrieve a user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    // Update the expense limit for a user
    public User updateExpenseLimit(Long userId, Double newLimit) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setExpenseLimit(newLimit);
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }

    public Optional<User> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            return userOptional; // Return the user if email and password match
        }
        return Optional.empty(); // Return empty if login fails
    }
}
