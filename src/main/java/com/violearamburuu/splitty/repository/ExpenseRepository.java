package com.violearamburuu.splitty.repository;

import com.violearamburuu.splitty.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByGroup(Group group);
}
