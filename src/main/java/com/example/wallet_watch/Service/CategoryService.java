package com.example.wallet_watch.Service;

import com.example.wallet_watch.Model.Category;
import com.example.wallet_watch.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Create a new category
    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category with the given name already exists.");
        }
        return categoryRepository.save(category);
    }

    // Retrieve all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Retrieve a category by ID
    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    // Retrieve a category by name
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
}
