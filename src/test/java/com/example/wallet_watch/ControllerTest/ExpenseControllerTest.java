package com.example.wallet_watch.ControllerTest;

import com.example.wallet_watch.Controller.ExpenseController;
import com.example.wallet_watch.Model.Expense;
import com.example.wallet_watch.Service.ExpenseService;
import com.example.wallet_watch.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private ExpenseController expenseController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Initialize mocks and inject them into the controller
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController).build();
    }

    @Test
    void getExpensesForMonth_ShouldReturnExpenses_WhenValidToken() throws Exception {
        List<Expense> expenses = Collections.singletonList(new Expense(100.0, LocalDate.now()));
        String token = "Bearer some_valid_token";
        String email = "test@example.com";

        given(jwtUtil.extractEmail(anyString())).willReturn(email);
        given(expenseService.getExpensesForMonth(anyString(), anyInt(), anyInt())).willReturn(expenses);

        mockMvc.perform(get("/expenses/month")
                        .header("Authorization", token)
                        .param("year", "2023")
                        .param("month", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amount").value(100.0));
    }

}
