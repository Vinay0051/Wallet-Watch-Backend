package com.example.wallet_watch.Repository;

import com.example.wallet_watch.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Find a category by name
    Optional<Category> findByName(String name);

    // Check if a category exists by name
    boolean existsByName(String name);
}
