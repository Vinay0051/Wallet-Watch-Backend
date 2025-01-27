package com.example.wallet_watch.RepositoryTest;

import com.example.wallet_watch.Model.Category;
import com.example.wallet_watch.Model.Expense;
import com.example.wallet_watch.Model.User;
import com.example.wallet_watch.Repository.ExpenseRepository;
import com.example.wallet_watch.Repository.UserRepository;
import com.example.wallet_watch.Repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Test data
    private User createTestUser() {
        return new User("Vinay", "vinay@example.com", "password", 1000.0);
    }

    private Category createTestCategory(String name) {
        return new Category(name); // Use a unique name for each test
    }

    private Expense createTestExpense(User user, Category category) {
        Expense expense = new Expense(); // Use the default constructor
        expense.setAmount(100.0); // Set amount
        expense.setTransactionDate(LocalDate.of(2023, 10, 15)); // Set transaction date
        expense.setUser(user); // Set user
        expense.setCategory(category); // Set category
        return expense;
    }

    @Test
    public void testSaveExpense() {
        // Arrange
        User user = createTestUser();
        userRepository.save(user);

        Category category = createTestCategory("New"); // Use a unique category name
        categoryRepository.save(category);

        Expense expense = createTestExpense(user, category);

        // Act
        Expense savedExpense = expenseRepository.save(expense);

        // Assert
        assertNotNull(savedExpense.getExpenseId());
        assertEquals(100.0, savedExpense.getAmount());
        assertEquals(LocalDate.of(2023, 10, 15), savedExpense.getTransactionDate());
        assertEquals(user.getUserId(), savedExpense.getUser().getUserId());
        assertEquals(category.getCategoryId(), savedExpense.getCategory().getCategoryId());
    }

    @Test
    public void testFindExpensesByUser() {
        // Arrange
        User user = createTestUser();
        userRepository.save(user);

        Category category = createTestCategory("old"); // Use a unique category name
        categoryRepository.save(category);

        Expense expense = createTestExpense(user, category);
        expenseRepository.save(expense);

        // Act
        List<Expense> expenses = expenseRepository.findByUser(user);

        // Assert
        assertEquals(1, expenses.size());
        assertEquals(100.0, expenses.get(0).getAmount());
    }

    @Test
    public void testFindExpensesByUserAndDateRange() {
        // Arrange
        User user = createTestUser();
        userRepository.save(user);

        Category category = createTestCategory("Entertainment"); // Use a unique category name
        categoryRepository.save(category);

        Expense expense = createTestExpense(user, category);
        expenseRepository.save(expense);

        LocalDate startDate = LocalDate.of(2023, 10, 1);
        LocalDate endDate = LocalDate.of(2023, 10, 31);

        // Act
        List<Expense> expenses = expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);

        // Assert
        assertEquals(1, expenses.size());
        assertEquals(100.0, expenses.get(0).getAmount());
    }

    @Test
    public void testFindExpensesByUserAndTransactionDate() {
        // Arrange
        User user = createTestUser();
        userRepository.save(user);

        Category category = createTestCategory("Groceries"); // Use a unique category name
        categoryRepository.save(category);

        Expense expense = createTestExpense(user, category);
        expenseRepository.save(expense);

        LocalDate transactionDate = LocalDate.of(2023, 10, 15);

        // Act
        List<Expense> expenses = expenseRepository.findByUserAndTransactionDate(user, transactionDate);

        // Assert
        assertEquals(1, expenses.size());
        assertEquals(100.0, expenses.get(0).getAmount());
    }
}