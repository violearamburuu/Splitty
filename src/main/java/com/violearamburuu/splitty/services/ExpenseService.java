package com.violearamburuu.splitty.services;

import com.violearamburuu.splitty.model.*;
import com.violearamburuu.splitty.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final GroupRepository groupRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository, GroupMembershipRepository groupMembershipRepository, GroupRepository groupRepository){
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.groupRepository = groupRepository;
    }

    public Expense createExpense(User user, Group group, BigDecimal amount){
        groupRepository.findByName(group.getName()).orElseThrow(() -> new RuntimeException("This group doesn't exist"));
        groupMembershipRepository.findByUserAndGroup(user, group).orElseThrow(() -> new RuntimeException("This user does not belong to this group."));
        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("This is not a valid amount.");
        }
        Expense newExpense = new Expense(amount, user, group);
        expenseRepository.save(newExpense);
        return newExpense;

        // TERMINAR
    }
}
