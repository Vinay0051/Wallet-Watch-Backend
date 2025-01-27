package com.example.wallet_watch.Repository;

import com.example.wallet_watch.Model.Expense;
import com.example.wallet_watch.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser(User user);

    List<Expense> findByUserAndTransactionDateBetween(User user, LocalDate startDate, LocalDate endDate);

    List<Expense> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);

    List<Expense> findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(User user, LocalDate startDate, LocalDate endDate);

    List<Expense> findByUserAndTransactionDate(User user, LocalDate transactionDate);
}