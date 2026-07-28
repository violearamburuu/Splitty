package com.violearamburuu.splitty.repository;

import com.violearamburuu.splitty.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {
    List<ExpenseShare> findAllByExpense(Expense expense);
}
