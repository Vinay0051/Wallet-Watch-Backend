package com.example.wallet_watch.ControllerTest;

import com.example.wallet_watch.Controller.CategoryController;
import com.example.wallet_watch.Model.Category;
import com.example.wallet_watch.Service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }


    @Test
    void getAllCategories_ShouldReturnListOfCategories() throws Exception {
        List<Category> categories = Arrays.asList(
                new Category("Category1"),
                new Category("Category2")
        );

        given(categoryService.getAllCategories()).willReturn(categories);

        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Category1"))
                .andExpect(jsonPath("$[1].name").value("Category2"));
    }

    @Test
    void getAllCategories_ShouldReturnEmptyList_WhenNoCategoriesExist() throws Exception {
        given(categoryService.getAllCategories()).willReturn(List.of());

        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenIdExists() throws Exception {
        Category category = new Category("TestCategory");

        given(categoryService.getCategoryById(1L)).willReturn(Optional.of(category));

        mockMvc.perform(get("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("TestCategory"));
    }

    @Test
    void getCategoryById_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        given(categoryService.getCategoryById(1L)).willReturn(Optional.empty());

        mockMvc.perform(get("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCategoryByName_ShouldReturnCategory_WhenNameExists() throws Exception {
        Category category = new Category("TestCategory");

        given(categoryService.getCategoryByName("TestCategory")).willReturn(Optional.of(category));

        mockMvc.perform(get("/categories/name/TestCategory")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("TestCategory"));
    }

    @Test
    void getCategoryByName_ShouldReturnNotFound_WhenNameDoesNotExist() throws Exception {
        given(categoryService.getCategoryByName("NonexistentCategory")).willReturn(Optional.empty());

        mockMvc.perform(get("/categories/name/NonexistentCategory")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
