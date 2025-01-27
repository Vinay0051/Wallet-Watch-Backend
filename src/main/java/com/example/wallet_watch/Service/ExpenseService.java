package com.example.wallet_watch.Service;

import com.example.wallet_watch.Model.Category;
import com.example.wallet_watch.Model.Expense;
import com.example.wallet_watch.Model.User;
import com.example.wallet_watch.Repository.CategoryRepository;
import com.example.wallet_watch.Repository.ExpenseRepository;
import com.example.wallet_watch.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    public Expense addExpense(String email, String categoryName, Double amount, LocalDate transactionDate) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));


        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with name: " + categoryName));


        Expense expense = new Expense(amount, transactionDate);
        expense.setUser(user);
        expense.setCategory(category);

        return expenseRepository.save(expense);
    }

    public Double getTotalExpensesForCurrentMonth(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));


        YearMonth currentYearMonth = YearMonth.now();
        LocalDate startDate = currentYearMonth.atDay(1);
        LocalDate endDate = currentYearMonth.atEndOfMonth();

        List<Expense> expenses = expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);

        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public List<Expense> getExpensesForMonth(String email, int year, int month) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);
    }


    public List<Expense> getExpensesForYear(String email, int year) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        return expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);
    }


    public Map<String, Object> getMonthlyExpensesForYear(String email, int year) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Expense> expenses = expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);


        Map<String, Object> monthlyExpenses = groupExpensesByMonth(expenses);
        return monthlyExpenses;
    }


    private Map<String, Object> groupExpensesByMonth(List<Expense> expenses) {

        Map<Month, List<Expense>> expensesByMonth = new HashMap<>();

        for (Expense expense : expenses) {
            Month month = expense.getTransactionDate().getMonth();
            expensesByMonth.computeIfAbsent(month, k -> new ArrayList<>()).add(expense);
        }

        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> monthlyExpenses = new ArrayList<>();


        for (Month month : Month.values()) {
            List<Expense> monthExpenses = expensesByMonth.getOrDefault(month, Collections.emptyList());

            // Calculate total expenses for the month
            double totalExpenses = monthExpenses.stream().mapToDouble(Expense::getAmount).sum();

            // Prepare the monthly data
            Map<String, Object> monthlyData = new HashMap<>();
            monthlyData.put("month", month.toString()); // Month name (e.g., "JANUARY")
            monthlyData.put("total_expenses", totalExpenses);

            // Prepare the list of expenses for the month
            List<Map<String, Object>> expenseList = new ArrayList<>();
            for (Expense expense : monthExpenses) {
                Map<String, Object> expenseData = new HashMap<>();
                expenseData.put("expense_id", expense.getExpenseId());
                expenseData.put("amount", expense.getAmount());
                expenseData.put("category", expense.getCategory().getName());
                expenseData.put("date", expense.getTransactionDate().toString());
                expenseList.add(expenseData);
            }

            monthlyData.put("expenses", expenseList);
            monthlyExpenses.add(monthlyData);
        }

        response.put("monthly_expenses", monthlyExpenses);
        return response;
    }

    public void processCSVFile(MultipartFile file, String userEmail) throws Exception {

        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("User not found with email: " + userEmail);
        }

        User user = userOptional.get();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isHeader = true;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] fields = line.split(",");
                if (fields.length < 4) {
                    throw new IllegalArgumentException("Invalid CSV format. Expected 4 fields per row.");
                }


                String transactionId = fields[0].trim();
                String categoryName = fields[1].trim();
                String dateString = fields[2].trim();
                String amountString = fields[3].trim();


                LocalDate transactionDate = LocalDate.parse(dateString, dateFormatter);
                double amount = Double.parseDouble(amountString);

                // Find or create the category
                Optional<Category> categoryOptional = categoryRepository.findByName(categoryName);
                Category category;
                if (categoryOptional.isPresent()) {
                    category = categoryOptional.get();
                } else {
                    // Create a new category if it doesn't exist
                    category = new Category(categoryName);
                    categoryRepository.save(category);
                }

                // Create and save the expense
                Expense expense = new Expense(amount, transactionDate);
                expense.setUser(user);
                expense.setCategory(category);
                expenseRepository.save(expense);
            }
        } catch (Exception e) {
            throw new Exception("Error processing CSV file: " + e.getMessage());
        }
    }

    public Map<String, Double> getCategoryWiseExpensesForCurrentMonth(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));


        YearMonth currentYearMonth = YearMonth.now();
        LocalDate startDate = currentYearMonth.atDay(1);
        LocalDate endDate = currentYearMonth.atEndOfMonth();


        List<Expense> expenses = expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);

        // Group expenses by category and calculate totals
        Map<String, Double> categoryWiseExpenses = new HashMap<>();
        for (Expense expense : expenses) {
            String categoryName = expense.getCategory().getName();
            categoryWiseExpenses.put(categoryName, categoryWiseExpenses.getOrDefault(categoryName, 0.0) + expense.getAmount());
        }

        return categoryWiseExpenses;
    }

    public Map<String, Object> getCategoryWiseExpensesForSelectedMonth(String email, int year, int month) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));


        YearMonth selectedYearMonth = YearMonth.of(year, month);
        LocalDate startDate = selectedYearMonth.atDay(1); // First day of the month
        LocalDate endDate = selectedYearMonth.atEndOfMonth(); // Last day of the month

        // Fetch expenses for the selected month
        List<Expense> expenses = expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);

        // Group expenses by category and calculate totals
        Map<String, Double> categoryWiseExpenses = new HashMap<>();
        for (Expense expense : expenses) {
            String categoryName = expense.getCategory().getName();
            categoryWiseExpenses.put(categoryName, categoryWiseExpenses.getOrDefault(categoryName, 0.0) + expense.getAmount());
        }


        Map<String, Object> response = new HashMap<>();
        response.put("month", selectedYearMonth.getMonth().toString()); // Month name (e.g., "JANUARY")
        response.put("category", new ArrayList<>(categoryWiseExpenses.keySet())); // List of category names
        response.put("total_expense", new ArrayList<>(categoryWiseExpenses.values())); // List of total expenses

        return response;
    }

    public Map<String, Object> getCategoryWiseExpensesForDate(String email, String date) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        LocalDate transactionDate = LocalDate.parse(date);

        // Fetch expenses for the user on the given date
        List<Expense> expenses = expenseRepository.findByUserAndTransactionDate(user, transactionDate);

        // Group expenses by category and calculate totals
        Map<String, Double> categoryWiseTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getCategory().getName(),
                        Collectors.summingDouble(Expense::getAmount)
                ));

        List<String> allCategories = Arrays.asList("Food", "Transport", "Entertainment", "Groceries", "Utilities", "Healthcare", "Education", "Miscellaneous");


        List<Double> totals = allCategories.stream()
                .map(category -> categoryWiseTotals.getOrDefault(category, 0.0))
                .collect(Collectors.toList());


        Map<String, Object> response = new HashMap<>();
        response.put("date", date);
        response.put("categories", allCategories);
        response.put("expenses", totals);

        return response;
    }

    public List<Map<String, Object>> getCurrentMonthExpenses(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));


        YearMonth currentYearMonth = YearMonth.now();
        LocalDate startDate = currentYearMonth.atDay(1); // First day of the month
        LocalDate endDate = currentYearMonth.atEndOfMonth(); // Last day of the month

        // Fetch expenses for the current month, sorted by date in descending order
        List<Expense> expenses = expenseRepository.findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(user, startDate, endDate);


        List<Map<String, Object>> expenseEntries = new ArrayList<>();
        for (Expense expense : expenses) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("date", expense.getTransactionDate().toString());
            entry.put("category", expense.getCategory().getName());
            entry.put("amount", expense.getAmount());
            expenseEntries.add(entry);
        }

        return expenseEntries;
    }

    public Map<String, Object> getCategoryWiseExpensesForYear(String email, int year) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));


        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        // Fetch expenses for the user within the given year
        List<Expense> expenses = expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);

        // Group expenses by category and calculate totals
        Map<String, Double> categoryWiseTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getCategory().getName(),
                        Collectors.summingDouble(Expense::getAmount)
                ));


        List<String> categories = new ArrayList<>(categoryWiseTotals.keySet());
        List<Double> totals = new ArrayList<>(categoryWiseTotals.values());

        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        response.put("expenses", totals);

        return response;
    }

    //    public Expense addExpense(String email, String categoryName, Double amount, LocalDate transactionDate) {
//        // Fetch user by email
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
//
//        // Fetch or create category by name
//        Category category = categoryRepository.findByName(categoryName)
//                .orElseGet(() -> {
//                    // Create a new category if it doesn't exist
//                    Category newCategory = new Category(categoryName);
//                    return categoryRepository.save(newCategory);
//                });
//
//        // Create the new expense
//        Expense expense = new Expense(amount, transactionDate);
//        expense.setUser(user);
//        expense.setCategory(category);
//
//        // Save the expense and return it
//        return expenseRepository.save(expense);
//    }
}


















//package com.example.wallet_watch.Service;
//
//import com.example.wallet_watch.Model.Category;
//import com.example.wallet_watch.Model.Expense;
//import com.example.wallet_watch.Model.User;
//import com.example.wallet_watch.Repository.CategoryRepository;
//import com.example.wallet_watch.Repository.ExpenseRepository;
//import com.example.wallet_watch.Repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.Month;
//import java.time.YearMonth;
//import java.util.*;
//
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//
//import java.time.format.DateTimeFormatter;
//
//@Service
//public class ExpenseService {
//
//    @Autowired
//    private ExpenseRepository expenseRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    // Method to add an expense
////    public Expense addExpense(Long userId, Long categoryId, Double amount, LocalDate transactionDate) {
////        // Fetch user and category by ID
////        Optional<User> userOptional = userRepository.findById(userId);
////        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
////
////        // Check if user and category exist
////        if (userOptional.isPresent() && categoryOptional.isPresent()) {
////            User user = userOptional.get();
////            Category category = categoryOptional.get();
////
////            // Create the new expense
////            Expense expense = new Expense(amount, transactionDate);
////            expense.setUser(user); // Set user from the ID provided
////            expense.setCategory(category); // Set category from the ID provided
////
////            // Save the expense and return it
////            return expenseRepository.save(expense);
////        } else {
////            throw new IllegalArgumentException("User or Category not found.");
////        }
////    }
//
//    public Expense addExpense(String email, String categoryName, Double amount, LocalDate transactionDate) {
//        // Fetch user by email
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
//
//        // Fetch or create category by name
//        Category category = categoryRepository.findByName(categoryName)
//                .orElseGet(() -> {
//                    // Create a new category if it doesn't exist
//                    Category newCategory = new Category(categoryName);
//                    return categoryRepository.save(newCategory);
//                });
//
//        // Create the new expense
//        Expense expense = new Expense(amount, transactionDate);
//        expense.setUser(user);
//        expense.setCategory(category);
//
//        // Save the expense and return it
//        return expenseRepository.save(expense);
//    }
//
//    public List<Expense> getExpensesForMonth(String email, int year, int month) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
//
//        YearMonth yearMonth = YearMonth.of(year, month);
//        LocalDate startDate = yearMonth.atDay(1);
//        LocalDate endDate = yearMonth.atEndOfMonth();
//
//        return expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);
//    }
//
//    // Get expenses for a specific year (updated to use email)
//    public List<Expense> getExpensesForYear(String email, int year) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
//
//        LocalDate startDate = LocalDate.of(year, 1, 1);
//        LocalDate endDate = LocalDate.of(year, 12, 31);
//
//        return expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);
//    }
//
//    // Get monthly expenses for a specific year (updated to use email)
//    public Map<String, Object> getMonthlyExpensesForYear(String email, int year) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
//
//        LocalDate startDate = LocalDate.of(year, 1, 1);
//        LocalDate endDate = LocalDate.of(year, 12, 31);
//
//        List<Expense> expenses = expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);
//
//        // Group expenses by month and calculate totals
//        Map<String, Object> monthlyExpenses = expenseService.groupExpensesByMonth(expenses);
//        return monthlyExpenses;
//    }
//
////    public List<Expense> getExpensesForMonth(Long userId, int year, int month) {
////        Optional<User> userOptional = userRepository.findById(userId);
////
////        if (userOptional.isPresent()) {
////            User user = userOptional.get();
////            YearMonth yearMonth = YearMonth.of(year, month);
////            LocalDate startDate = yearMonth.atDay(1);
////            LocalDate endDate = yearMonth.atEndOfMonth();
////            return expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);
////        } else {
////            throw new IllegalArgumentException("User not found with ID: " + userId);
////        }
////    }
////
////    // Fetch expenses for a specific year
////    public List<Expense> getExpensesForYear(Long userId, int year) {
////        Optional<User> userOptional = userRepository.findById(userId);
////
////        if (userOptional.isPresent()) {
////            User user = userOptional.get();
////            LocalDate startDate = LocalDate.of(year, 1, 1);
////            LocalDate endDate = LocalDate.of(year, 12, 31);
////            return expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);
////        } else {
////            throw new IllegalArgumentException("User not found with ID: " + userId);
////        }
////    }
//
//    public void processCSVFile(MultipartFile file, String userEmail) throws Exception {
//        // Find the user by email
//        Optional<User> userOptional = userRepository.findByEmail(userEmail);
//        if (!userOptional.isPresent()) {
//            throw new IllegalArgumentException("User not found with email: " + userEmail);
//        }
//        User user = userOptional.get();
//
//        // Parse the CSV file
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//            String line;
//            boolean isHeader = true;
//            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Adjust the date format as needed
//
//            while ((line = reader.readLine()) != null) {
//                if (isHeader) {
//                    isHeader = false; // Skip the header row
//                    continue;
//                }
//
//                String[] fields = line.split(","); // Assuming CSV is comma-separated
//                if (fields.length < 4) {
//                    throw new IllegalArgumentException("Invalid CSV format. Expected 4 fields per row.");
//                }
//
//                // Parse fields
//                String transactionId = fields[0].trim();
//                String categoryName = fields[1].trim();
//                String dateString = fields[2].trim();
//                String amountString = fields[3].trim();
//
//                // Parse date and amount
//                LocalDate transactionDate = LocalDate.parse(dateString, dateFormatter);
//                double amount = Double.parseDouble(amountString);
//
//                // Find or create the category
//                Optional<Category> categoryOptional = categoryRepository.findByName(categoryName);
//                Category category;
//                if (categoryOptional.isPresent()) {
//                    category = categoryOptional.get();
//                } else {
//                    // Create a new category if it doesn't exist
//                    category = new Category(categoryName);
//                    categoryRepository.save(category);
//                }
//
//                // Create and save the expense
//                Expense expense = new Expense(amount, transactionDate);
//                expense.setUser(user);
//                expense.setCategory(category);
//                expenseRepository.save(expense);
//            }
//        } catch (Exception e) {
//            throw new Exception("Error processing CSV file: " + e.getMessage());
//        }
//    }
//
//    // New method to get monthly expenses for a specific year
//    public Map<String, Object> getMonthlyExpensesForYear(Long userId, int year) {
//        Optional<User> userOptional = userRepository.findById(userId);
//        if (!userOptional.isPresent()) {
//            throw new IllegalArgumentException("User not found with ID: " + userId);
//        }
//
//        User user = userOptional.get();
//        LocalDate startDate = LocalDate.of(year, 1, 1); // Start of the year
//        LocalDate endDate = LocalDate.of(year, 12, 31); // End of the year
//
//        // Fetch all expenses for the user in the given year
//        List<Expense> expenses = expenseRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);
//
//        // Group expenses by month
//        Map<Month, List<Expense>> expensesByMonth = new HashMap<>();
//        for (Expense expense : expenses) {
//            Month month = expense.getTransactionDate().getMonth();
//            expensesByMonth.computeIfAbsent(month, k -> new ArrayList<>()).add(expense);
//        }
//
//        // Prepare the response
//        Map<String, Object> response = new HashMap<>();
//        response.put("user_id", userId);
//        response.put("year", year);
//
//        List<Map<String, Object>> monthlyExpenses = new ArrayList<>();
//
//        // Iterate through all months of the year
//        for (Month month : Month.values()) {
//            List<Expense> monthExpenses = expensesByMonth.getOrDefault(month, Collections.emptyList());
//
//            // Calculate total expenses for the month
//            double totalExpenses = monthExpenses.stream().mapToDouble(Expense::getAmount).sum();
//
//            // Prepare the monthly data
//            Map<String, Object> monthlyData = new HashMap<>();
//            monthlyData.put("month", month.toString()); // Month name (e.g., "JANUARY")
//            monthlyData.put("total_expenses", totalExpenses);
//
//            // Prepare the list of expenses for the month
//            List<Map<String, Object>> expenseList = new ArrayList<>();
//            for (Expense expense : monthExpenses) {
//                Map<String, Object> expenseData = new HashMap<>();
//                expenseData.put("expense_id", expense.getExpenseId());
//                expenseData.put("amount", expense.getAmount());
//                expenseData.put("category", expense.getCategory().getName());
//                expenseData.put("date", expense.getTransactionDate().toString());
//                expenseList.add(expenseData);
//            }
//
//            monthlyData.put("expenses", expenseList);
//            monthlyExpenses.add(monthlyData);
//        }
//
//        response.put("monthly_expenses", monthlyExpenses);
//        return response;
//    }
//}