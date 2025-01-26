package com.example.wallet_watch.Controller;

import com.example.wallet_watch.Model.Expense;
import com.example.wallet_watch.Service.ExpenseService;
import com.example.wallet_watch.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private JwtUtil jwtUtil;

    // Create a new expense (using categoryId and userId as path variables)
//    @PostMapping("/user/{userId}/category/{categoryId}/amount/{amount}/transactionDate/{transactionDate}")
//    public ResponseEntity<Expense> addExpense(
//            @PathVariable Long userId,
//            @PathVariable Long categoryId,
//            @PathVariable Double amount,
//            @PathVariable LocalDate transactionDate) {
//
//        Expense expense = expenseService.addExpense(userId, categoryId, amount, transactionDate);
//        return ResponseEntity.ok(expense);
//    }

    @PostMapping("/add")
    public ResponseEntity<Expense> addExpense(
            @RequestParam String email,
            @RequestParam String categoryName,
            @RequestParam Double amount,
            @RequestParam LocalDate transactionDate) {

        Expense expense = expenseService.addExpense(email, categoryName, amount, transactionDate);
        return ResponseEntity.ok(expense);
    }

    // Get expenses for a specific month (updated to use email from token)
    @GetMapping("/month")
    public ResponseEntity<List<Expense>> getExpensesForMonth(
            @RequestHeader("Authorization") String token,
            @RequestParam int year,
            @RequestParam int month) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        List<Expense> expenses = expenseService.getExpensesForMonth(email, year, month);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/current-month-expenses")
    public ResponseEntity<List<Map<String, Object>>> getCurrentMonthExpenses(
            @RequestHeader("Authorization") String token) {

        // Extract email from the JWT token
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        // Call the service method to get the current month's expenses
        List<Map<String, Object>> currentMonthExpenses = expenseService.getCurrentMonthExpenses(email);

        return ResponseEntity.ok(currentMonthExpenses);
    }   

    // Get expenses for a specific year (updated to use email from token)
    @GetMapping("/year")
    public ResponseEntity<List<Expense>> getExpensesForYear(
            @RequestHeader("Authorization") String token,
            @RequestParam int year) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        List<Expense> expenses = expenseService.getExpensesForYear(email, year);
        return ResponseEntity.ok(expenses);
    }

    // Get monthly expenses for a specific year (updated to use email from token)
    @GetMapping("/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyExpensesForYear(
            @RequestHeader("Authorization") String token,
            @RequestParam int year) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        Map<String, Object> monthlyExpenses = expenseService.getMonthlyExpensesForYear(email, year);
        return ResponseEntity.ok(monthlyExpenses);
    }

    @GetMapping("/month-total")
    public ResponseEntity<Map<String, Double>> getTotalExpensesForCurrentMonth(
            @RequestHeader("Authorization") String token) {

        // Extract email from the JWT token
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        // Call the service method to get the total expenses for the current month
        Double totalExpenses = expenseService.getTotalExpensesForCurrentMonth(email);

        // Create a JSON response with the key "month-total"
        Map<String, Double> response = new HashMap<>();
        response.put("month-total", totalExpenses);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/currentMonth-categoryWise")
    public ResponseEntity<Map<String, Double>> getCategoryWiseExpensesForCurrentMonth(
            @RequestHeader("Authorization") String token) {

        // Extract email from the JWT token
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        // Call the service method to get category-wise expenses for the current month
        Map<String, Double> categoryWiseExpenses = expenseService.getCategoryWiseExpensesForCurrentMonth(email);

        return ResponseEntity.ok(categoryWiseExpenses);
    }

    @GetMapping("/selectedMonth-categoryWise")
    public ResponseEntity<Map<String, Object>> getCategoryWiseExpensesForSelectedMonth(
            @RequestHeader("Authorization") String token,
            @RequestParam int year,
            @RequestParam int month) {

        // Extract email from the JWT token
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        // Call the service method to get category-wise expenses for the selected month and year
        Map<String, Object> categoryWiseExpenses = expenseService.getCategoryWiseExpensesForSelectedMonth(email, year, month);

        return ResponseEntity.ok(categoryWiseExpenses);
    }

//    @GetMapping("/user/{userId}/month/{year}/{month}")
//    public ResponseEntity<List<Expense>> getExpensesForMonth(
//            @PathVariable Long userId,
//            @PathVariable int year,
//            @PathVariable int month) {
//        List<Expense> expenses = expenseService.getExpensesForMonth(userId, year, month);
//        return ResponseEntity.ok(expenses);
//    }
//
//    // Fetch expenses for a specific year
//    @GetMapping("/user/{userId}/year/{year}")
//    public ResponseEntity<List<Expense>> getExpensesForYear(
//            @PathVariable Long userId,
//            @PathVariable int year) {
//        List<Expense> expenses = expenseService.getExpensesForYear(userId, year);
//        return ResponseEntity.ok(expenses);
//    }
//
//    // New endpoint for CSV file upload
    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCSVFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userEmail") String userEmail) {
        try {
            expenseService.processCSVFile(file, userEmail);
            return ResponseEntity.ok("CSV file processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing CSV file: " + e.getMessage());
        }
    }

//    // New endpoint to get monthly expenses for a specific year
//    @GetMapping("/user/{userId}/yearly/{year}")
//    public ResponseEntity<Map<String, Object>> getMonthlyExpensesForYear(
//            @PathVariable Long userId,
//            @PathVariable int year) {
//        Map<String, Object> monthlyExpenses = expenseService.getMonthlyExpensesForYear(userId, year);
//        return ResponseEntity.ok(monthlyExpenses);
//    }
}