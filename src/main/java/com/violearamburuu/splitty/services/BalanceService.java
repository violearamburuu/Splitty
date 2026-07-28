package com.violearamburuu.splitty.services;

import com.violearamburuu.splitty.model.*;
import com.violearamburuu.splitty.repository.ExpenseRepository;
import com.violearamburuu.splitty.repository.ExpenseShareRepository;
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

    public List<Transfer> simplifyDebts(Group group){
        Map<User, BigDecimal> debtors = new HashMap<User, BigDecimal>();
        Map<User, BigDecimal> creditors = new HashMap<User, BigDecimal>();
        Map<User, BigDecimal> balances = calculateBalances(group);
        List<Transfer> transfers = new ArrayList<Transfer>();

        for(Map.Entry<User, BigDecimal> balance : balances.entrySet()){
            if (balance.getValue().compareTo(BigDecimal.ZERO) < 0){
                debtors.put(balance.getKey(), balance.getValue());
            } else if (balance.getValue().compareTo(BigDecimal.ZERO) > 0){
                creditors.put(balance.getKey(), balance.getValue());
            }
        }

        while(!debtors.isEmpty() || !creditors.isEmpty()){
            Map.Entry<User, BigDecimal> biggestDebtor = getBiggerDebtor(debtors);
            Map.Entry<User, BigDecimal> biggestCreditor = getBiggestCreditor(creditors);
            BigDecimal debt = biggestDebtor.getValue().abs();
            BigDecimal credit = biggestCreditor.getValue();
            BigDecimal amount = debt.compareTo(credit) <= 0 ? debt : credit;
            creditors.put(biggestCreditor.getKey(), creditors.get(biggestCreditor.getKey()).subtract(amount));
            debtors.put(biggestDebtor.getKey(), debtors.get(biggestDebtor.getKey()).add(amount));
            if(debtors.get(biggestDebtor.getKey()).compareTo(BigDecimal.ZERO) == 0){
                debtors.remove(biggestDebtor.getKey());
            }
            if(creditors.get(biggestCreditor.getKey()).compareTo(BigDecimal.ZERO) == 0){
                creditors.remove(biggestCreditor.getKey());
            }
            Transfer transfer = new Transfer(biggestDebtor.getKey(), biggestCreditor.getKey(), amount);
            transfers.add(transfer);
        }

        return transfers;
    }

}
