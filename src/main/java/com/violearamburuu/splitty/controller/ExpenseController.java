package com.violearamburuu.splitty.controller;

import com.violearamburuu.splitty.model.Expense;
import com.violearamburuu.splitty.model.Group;
import com.violearamburuu.splitty.model.User;
import com.violearamburuu.splitty.services.DTO.CreateExpenseRequest;
import com.violearamburuu.splitty.services.DTO.ExpenseResponse;
import com.violearamburuu.splitty.services.ExpenseService;
import com.violearamburuu.splitty.services.GroupService;
import com.violearamburuu.splitty.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final UserService userService;
    private final GroupService groupService;

    public ExpenseController(ExpenseService expenseService, UserService userService, GroupService groupService) {
        this.expenseService = expenseService;
        this.userService = userService;
        this.groupService = groupService;
    }

    @PostMapping
    public ExpenseResponse createExpense(@RequestBody CreateExpenseRequest request){
        User paidBy = userService.findUserById(request.paidById());
        Group group = groupService.findGroupById(request.groupId());

        Map<User, BigDecimal> shares = new HashMap<>();
        for (Map.Entry<Long, BigDecimal> entry : request.shares().entrySet()) {
            shares.put(userService.findUserById(entry.getKey()), entry.getValue());
        }

        Expense expense = expenseService.createExpense(paidBy, group, request.amount(), request.date(), request.description(), shares);

        return new ExpenseResponse(expense.getId(), expense.getAmount(), expense.getPaidBy().getId(), expense.getGroup().getId(), expense.getDescription(), expense.getDate());
    }


}
