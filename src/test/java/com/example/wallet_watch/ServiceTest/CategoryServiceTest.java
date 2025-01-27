package com.example.wallet_watch.ServiceTest;

import com.example.wallet_watch.Model.Category;
import com.example.wallet_watch.Repository.CategoryRepository;
import com.example.wallet_watch.Service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    // Test data
    private Category createTestCategory() {
        Category category = new Category();
        category.setName("Food");
        return category;
    }

    @Test
    public void testCreateCategory() {
        // Arrange
        Category category = createTestCategory();
        when(categoryRepository.existsByName("Food")).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);

        // Act
        Category savedCategory = categoryService.createCategory(category);

        // Assert
        assertNotNull(savedCategory);
        assertEquals("Food", savedCategory.getName());
        verify(categoryRepository, times(1)).existsByName("Food");
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    public void testCreateCategory_DuplicateName() {
        // Arrange
        Category category = createTestCategory();
        when(categoryRepository.existsByName("Food")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.createCategory(category);
        });

        assertEquals("Category with the given name already exists.", exception.getMessage());
        verify(categoryRepository, times(1)).existsByName("Food");
        verify(categoryRepository, never()).save(category);
    }

    @Test
    public void testGetAllCategories() {
        // Arrange
        Category category1 = createTestCategory();
        Category category2 = new Category();
        category2.setName("Transport");
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        // Act
        List<Category> categories = categoryService.getAllCategories();

        // Assert
        assertEquals(2, categories.size());
        assertEquals("Food", categories.get(0).getName());
        assertEquals("Transport", categories.get(1).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    public void testGetCategoryById() {
        // Arrange
        Category category = createTestCategory();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        Optional<Category> foundCategory = categoryService.getCategoryById(1L);

        // Assert
        assertTrue(foundCategory.isPresent());
        assertEquals("Food", foundCategory.get().getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetCategoryById_NotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Category> foundCategory = categoryService.getCategoryById(1L);

        // Assert
        assertFalse(foundCategory.isPresent());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetCategoryByName() {
        // Arrange
        Category category = createTestCategory();
        when(categoryRepository.findByName("Food")).thenReturn(Optional.of(category));

        // Act
        Optional<Category> foundCategory = categoryService.getCategoryByName("Food");

        // Assert
        assertTrue(foundCategory.isPresent());
        assertEquals("Food", foundCategory.get().getName());
        verify(categoryRepository, times(1)).findByName("Food");
    }

    @Test
    public void testGetCategoryByName_NotFound() {
        // Arrange
        when(categoryRepository.findByName("Unknown")).thenReturn(Optional.empty());

        // Act
        Optional<Category> foundCategory = categoryService.getCategoryByName("Unknown");

        // Assert
        assertFalse(foundCategory.isPresent());
        verify(categoryRepository, times(1)).findByName("Unknown");
    }
}