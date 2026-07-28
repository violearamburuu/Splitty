package com.violearamburuu.splitty.services;

import com.violearamburuu.splitty.model.*;
import com.violearamburuu.splitty.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseShareRepository expenseShareRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final GroupRepository groupRepository;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseShareRepository expenseShareRepository, GroupMembershipRepository groupMembershipRepository, GroupRepository groupRepository){
        this.expenseRepository = expenseRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.groupRepository = groupRepository;
        this.expenseShareRepository = expenseShareRepository;
    }

    public Expense createExpense(User user, Group group, BigDecimal amount, LocalDate date, String description, Map<User, BigDecimal> shares){
        groupRepository.findById(group.getId()).orElseThrow(() -> new RuntimeException("This group doesn't exist"));
        groupMembershipRepository.findByUserAndGroup(user, group).orElseThrow(() -> new RuntimeException("This user does not belong to this group."));
        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("This is not a valid amount.");
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal currentAmount : shares.values()) {
            sum = sum.add(currentAmount);
        }
        if (sum.compareTo(amount) != 0) throw new RuntimeException("The shared do not line up.");

        Expense newExpense = new Expense(amount, user, group, description, date);
        expenseRepository.save(newExpense);

        for (Map.Entry<User, BigDecimal> entry : shares.entrySet()) {
            User debtor = entry.getKey();
            BigDecimal currentAmount = entry.getValue();
            ExpenseShare newShare = new ExpenseShare(debtor, newExpense, currentAmount);
            expenseShareRepository.save(newShare);
        }
        return newExpense;
    }
}
