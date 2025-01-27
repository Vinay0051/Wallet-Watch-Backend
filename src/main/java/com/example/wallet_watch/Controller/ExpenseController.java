package com.example.wallet_watch.Controller;

import com.example.wallet_watch.Model.Expense;
import com.example.wallet_watch.Service.ExpenseService;
import com.example.wallet_watch.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/expenses")
@Tag(name = "Expense Management", description = "APIs for managing expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private JwtUtil jwtUtil;


    @Operation(summary = "Add a new expense", description = "Adds a new expense for the authenticated user")
    @PostMapping("/add")
    public ResponseEntity<Expense> addExpense(
            @RequestHeader("Authorization") String token,
            @RequestParam String categoryName,
            @RequestParam Double amount,
            @RequestParam LocalDate transactionDate) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        Expense expense = expenseService.addExpense(email, categoryName, amount, transactionDate);
        return ResponseEntity.ok(expense);
    }


    @Operation(summary = "Upload CSV file", description = "Uploads a CSV file to process expenses")
    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCSVFile(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) {
        try {
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            expenseService.processCSVFile(file, email);
            return ResponseEntity.ok("CSV file processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing CSV file: " + e.getMessage());
        }
    }


    @Operation(summary = "Get expenses for a specific month", description = "Retrieves expenses for a specific month and year")
    @GetMapping("/month")
    public ResponseEntity<List<Expense>> getExpensesForMonth(
            @RequestHeader("Authorization") String token,
            @RequestParam int year,
            @RequestParam int month) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        List<Expense> expenses = expenseService.getExpensesForMonth(email, year, month);
        return ResponseEntity.ok(expenses);
    }


    @Operation(summary = "Get category-wise expenses for a specific date", description = "Retrieves category-wise expenses for a specific date")
    @GetMapping("/date-categoryWise")
    public ResponseEntity<Map<String, Object>> getCategoryWiseExpensesForDate(
            @RequestHeader("Authorization") String token,
            @RequestParam String date) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        Map<String, Object> categoryWiseExpenses = expenseService.getCategoryWiseExpensesForDate(email, date);
        return ResponseEntity.ok(categoryWiseExpenses);
    }

    @Operation(summary = "Get current month expenses", description = "Retrieves expenses for the current month")
    @GetMapping("/current-month-expenses")
    public ResponseEntity<List<Map<String, Object>>> getCurrentMonthExpenses(
            @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        List<Map<String, Object>> currentMonthExpenses = expenseService.getCurrentMonthExpenses(email);

        return ResponseEntity.ok(currentMonthExpenses);
    }


    @Operation(summary = "Get category-wise expenses for a specific year", description = "Retrieves category-wise expenses for a specific year")
    @GetMapping("/year-categoryWise")
    public ResponseEntity<Map<String, Object>> getCategoryWiseExpensesForYear(
            @RequestHeader("Authorization") String token,
            @RequestParam int year) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        Map<String, Object> categoryWiseExpenses = expenseService.getCategoryWiseExpensesForYear(email, year);
        return ResponseEntity.ok(categoryWiseExpenses);
    }


    @Operation(summary = "Get monthly expenses for a specific year", description = "Retrieves monthly expenses for a specific year")
    @GetMapping("/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyExpensesForYear(
            @RequestHeader("Authorization") String token,
            @RequestParam int year) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        Map<String, Object> monthlyExpenses = expenseService.getMonthlyExpensesForYear(email, year);
        return ResponseEntity.ok(monthlyExpenses);
    }

    @Operation(summary = "Get total expenses for the current month", description = "Retrieves total expenses for the current month")
    @GetMapping("/month-total")
    public ResponseEntity<Map<String, Double>> getTotalExpensesForCurrentMonth(
            @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        Double totalExpenses = expenseService.getTotalExpensesForCurrentMonth(email);

        Map<String, Double> response = new HashMap<>();
        response.put("month-total", totalExpenses);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get category-wise expenses for the current month", description = "Retrieves category-wise expenses for the current month")
    @GetMapping("/currentMonth-categoryWise")
    public ResponseEntity<Map<String, Double>> getCategoryWiseExpensesForCurrentMonth(
            @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        Map<String, Double> categoryWiseExpenses = expenseService.getCategoryWiseExpensesForCurrentMonth(email);

        return ResponseEntity.ok(categoryWiseExpenses);
    }

    @Operation(summary = "Get category-wise expenses for a selected month", description = "Retrieves category-wise expenses for a selected month and year")
    @GetMapping("/selectedMonth-categoryWise")
    public ResponseEntity<Map<String, Object>> getCategoryWiseExpensesForSelectedMonth(
            @RequestHeader("Authorization") String token,
            @RequestParam int year,
            @RequestParam int month) {

        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        Map<String, Object> categoryWiseExpenses = expenseService.getCategoryWiseExpensesForSelectedMonth(email, year, month);

        return ResponseEntity.ok(categoryWiseExpenses);
    }




//    @PostMapping("/upload-csv")
//    public ResponseEntity<String> uploadCSVFile(
//            @RequestHeader("Authorization") String token,
//            @RequestParam("file") MultipartFile file) {
//        try {
//            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
//            expenseService.processCSVFile(file, email);
//            return ResponseEntity.ok("CSV file processed successfully.");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error processing CSV file: " + e.getMessage());
//        }
//    }

    // Get expenses for a specific year (updated to use email from token)
//    @GetMapping("/year")
//    public ResponseEntity<List<Expense>> getExpensesForYear(
//            @RequestHeader("Authorization") String token,
//            @RequestParam int year) {
//
//        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
//        List<Expense> expenses = expenseService.getExpensesForYear(email, year);
//        return ResponseEntity.ok(expenses);
//    }

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



//    @PostMapping("/upload-csv")
//    public ResponseEntity<String> uploadCSVFile(
//            @RequestHeader("Authorization") String token,
//            @RequestParam("file")
//            @Parameter(
//                    in = ParameterIn.QUERY,
//                    description = "CSV file to upload",
//                    schema = @Schema(type = "string", format = "binary")
//            )
//            MultipartFile file) {
//        try {
//            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
//            expenseService.processCSVFile(file, email);
//            return ResponseEntity.ok("CSV file processed successfully.");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error processing CSV file: " + e.getMessage());
//        }
//    }

//    @PostMapping("/add")
//    public ResponseEntity<Expense> addExpense(
//            @RequestParam String email,
//            @RequestParam String categoryName,
//            @RequestParam Double amount,
//            @RequestParam LocalDate transactionDate) {
//
//        Expense expense = expenseService.addExpense(email, categoryName, amount, transactionDate);
//        return ResponseEntity.ok(expense);
//    }

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
//    @PostMapping("/upload-csv")
//    public ResponseEntity<String> uploadCSVFile(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("userEmail") String userEmail) {
//        try {
//            expenseService.processCSVFile(file, userEmail);
//            return ResponseEntity.ok("CSV file processed successfully.");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error processing CSV file: " + e.getMessage());
//        }
//    }

//    // New endpoint to get monthly expenses for a specific year
//    @GetMapping("/user/{userId}/yearly/{year}")
//    public ResponseEntity<Map<String, Object>> getMonthlyExpensesForYear(
//            @PathVariable Long userId,
//            @PathVariable int year) {
//        Map<String, Object> monthlyExpenses = expenseService.getMonthlyExpensesForYear(userId, year);
//        return ResponseEntity.ok(monthlyExpenses);
//    }
}