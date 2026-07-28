package com.violearamburuu.splitty.services;

import com.violearamburuu.splitty.model.*;
import com.violearamburuu.splitty.repository.ExpenseRepository;
import com.violearamburuu.splitty.repository.ExpenseShareRepository;
import com.violearamburuu.splitty.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BalanceService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseShareRepository expenseShareRepository;
    private final GroupService groupService;

    public BalanceService(ExpenseRepository expenseRepository, ExpenseShareRepository expenseShareRepository, GroupService groupService){
        this.expenseRepository = expenseRepository;
        this.expenseShareRepository = expenseShareRepository;
        this.groupService = groupService;
    }

    public Map<User, BigDecimal> calculateBalances(Group group){
        List<Expense> expenses = expenseRepository.findAllByGroup(group);
        List<User> members = groupService.getAllGroupMembers(group);
        Map<User, BigDecimal> balances = new HashMap<User, BigDecimal>();
        for (User user : members){
            balances.put(user, BigDecimal.ZERO);
        }

        for(Expense expense : expenses){
            balances.put(expense.getPaidBy(), balances.get(expense.getPaidBy()).add(expense.getAmount()));
            for(ExpenseShare share : expenseShareRepository.findAllByExpense(expense)){
                balances.put(share.getDebtor(), balances.get(share.getDebtor()).subtract(share.getOwedAmount()));
            }
        }

        return balances;
    }

    public List<Transfer> simplifyDebts(){
        return null;
    }

}
