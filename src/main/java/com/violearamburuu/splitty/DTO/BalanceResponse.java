package com.violearamburuu.splitty.DTO;

import java.math.BigDecimal;

public record BalanceResponse (long userId, String userName, BigDecimal amount) {
}
