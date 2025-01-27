package com.example.wallet_watch.RepositoryTest;

import com.example.wallet_watch.Model.Category;
import com.example.wallet_watch.Repository.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use the actual SQL database
@Transactional
@Rollback
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    // Test data
    private Category createTestCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    @BeforeEach
    public void setUp() {
        // Optional: Clear the database before each test
        categoryRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        // Optional: Clear the database after each test
        categoryRepository.deleteAll();
    }

    @Test
    public void testSaveCategory() {
        // Arrange
        Category category = createTestCategory("Food");

        // Act
        Category savedCategory = categoryRepository.save(category);

        // Assert
        assertNotNull(savedCategory.getCategoryId()); // Ensure ID is generated
        assertEquals("Food", savedCategory.getName());
    }

    @Test
    public void testFindCategoryById() {
        // Arrange
        Category category = createTestCategory("Food");
        Category savedCategory = categoryRepository.save(category);

        // Act
        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getCategoryId());

        // Assert
        assertTrue(foundCategory.isPresent());
        assertEquals("Food", foundCategory.get().getName());
    }

    @Test
    public void testFindCategoryById_NotFound() {
        // Arrange
        Long nonExistentId = 999L;

        // Act
        Optional<Category> foundCategory = categoryRepository.findById(nonExistentId);

        // Assert
        assertFalse(foundCategory.isPresent());
    }

    @Test
    public void testFindCategoryByName() {
        // Arrange
        Category category = createTestCategory("Food");
        categoryRepository.save(category);

        // Act
        Optional<Category> foundCategory = categoryRepository.findByName("Food");

        // Assert
        assertTrue(foundCategory.isPresent());
        assertEquals("Food", foundCategory.get().getName());
    }

    @Test
    public void testFindCategoryByName_NotFound() {
        // Arrange
        String nonExistentName = "Unknown";

        // Act
        Optional<Category> foundCategory = categoryRepository.findByName(nonExistentName);

        // Assert
        assertFalse(foundCategory.isPresent());
    }

    @Test
    public void testExistsByName() {
        // Arrange
        Category category = createTestCategory("Food");
        categoryRepository.save(category);

        // Act
        boolean exists = categoryRepository.existsByName("Food");

        // Assert
        assertTrue(exists);
    }

    @Test
    public void testExistsByName_NotFound() {
        // Arrange
        String nonExistentName = "Unknown";

        // Act
        boolean exists = categoryRepository.existsByName(nonExistentName);

        // Assert
        assertFalse(exists);
    }

    @Test
    public void testDeleteCategory() {
        // Arrange
        Category category = createTestCategory("Food");
        Category savedCategory = categoryRepository.save(category);

        // Act
        categoryRepository.delete(savedCategory);

        // Assert
        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getCategoryId());
        assertFalse(foundCategory.isPresent());
    }
}