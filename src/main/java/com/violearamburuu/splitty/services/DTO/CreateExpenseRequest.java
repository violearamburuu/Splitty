package com.violearamburuu.splitty.services.DTO;

import com.violearamburuu.splitty.model.ExpenseShare;
import com.violearamburuu.splitty.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record CreateExpenseRequest(
        long paidById, long groupId, BigDecimal amount,
        LocalDate date, String description,
        Map<Long, BigDecimal> shares
) {}
