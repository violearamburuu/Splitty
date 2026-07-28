package com.violearamburuu.splitty.services.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(long id, BigDecimal amount, long paidById,
                              long groupId, String description, LocalDate date) {}
