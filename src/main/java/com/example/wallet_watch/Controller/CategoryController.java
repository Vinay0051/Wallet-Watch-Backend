package com.example.wallet_watch.Controller;

import com.example.wallet_watch.Model.Category;
import com.example.wallet_watch.Service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Category Management", description = "APIs for managing categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @Operation(summary = "Create a new category", description = "Creates a new category with the provided details")
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(createdCategory);
    }


    @Operation(summary = "Get all categories", description = "Retrieves a list of all categories")
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }


//    @GetMapping("/{categoryId}")
//    public ResponseEntity<Category> getCategoryById(@PathVariable Long categoryId) {
//        Optional<Category> category = categoryService.getCategoryById(categoryId);
//        return category.map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }


//    @GetMapping("/name/{name}")
//    public ResponseEntity<Category> getCategoryByName(@PathVariable String name) {
//        Optional<Category> category = categoryService.getCategoryByName(name);
//        return category.map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
}
