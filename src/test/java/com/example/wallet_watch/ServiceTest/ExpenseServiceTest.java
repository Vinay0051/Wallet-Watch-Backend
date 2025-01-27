package com.example.wallet_watch.ServiceTest;

import com.example.wallet_watch.Model.Category;
import com.example.wallet_watch.Model.Expense;
import com.example.wallet_watch.Model.User;
import com.example.wallet_watch.Repository.CategoryRepository;
import com.example.wallet_watch.Repository.ExpenseRepository;
import com.example.wallet_watch.Repository.UserRepository;
import com.example.wallet_watch.Service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ExpenseService expenseService;

    // Test data
    private User createTestUser() {
        User user = new User();
        user.setEmail("vinay@example.com");
        return user;
    }

    private Category createTestCategory() {
        Category category = new Category();
        category.setName("Food");
        return category;
    }

    private Expense createTestExpense() {
        Expense expense = new Expense();
        expense.setAmount(100.0);
        expense.setTransactionDate(LocalDate.of(2023, 10, 15));
        return expense;
    }

    @Test
    public void testAddExpense_CategoryNotFound() {
        // Arrange
        User user = createTestUser();
        when(userRepository.findByEmail("vinay@example.com")).thenReturn(Optional.of(user));
        when(categoryRepository.findByName("Unknown")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            expenseService.addExpense("vinay@example.com", "Unknown", 100.0, LocalDate.of(2023, 10, 15));
        });

        assertEquals("Category not found with name: Unknown", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("vinay@example.com");
        verify(categoryRepository, times(1)).findByName("Unknown");
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    public void testGetTotalExpensesForCurrentMonth() {
        // Arrange
        User user = createTestUser();
        when(userRepository.findByEmail("vinay@example.com")).thenReturn(Optional.of(user));

        Expense expense1 = createTestExpense();
        expense1.setAmount(100.0);
        Expense expense2 = createTestExpense();
        expense2.setAmount(200.0);

        // Use the current month's date range
        YearMonth currentYearMonth = YearMonth.now();
        LocalDate startDate = currentYearMonth.atDay(1);
        LocalDate endDate = currentYearMonth.atEndOfMonth();

        when(expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate))
                .thenReturn(Arrays.asList(expense1, expense2));

        // Act
        Double totalExpenses = expenseService.getTotalExpensesForCurrentMonth("vinay@example.com");

        // Assert
        assertEquals(300.0, totalExpenses);
        verify(userRepository, times(1)).findByEmail("vinay@example.com");
        verify(expenseRepository, times(1)).findByUserAndTransactionDateBetween(user, startDate, endDate);
    }
}