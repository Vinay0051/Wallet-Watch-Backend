package com.example.wallet_watch.Repository;

import com.example.wallet_watch.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Check if a user exists by email
    boolean existsByEmail(String email);

    // Find a user by email
    Optional<User> findByEmail(String email);
}
