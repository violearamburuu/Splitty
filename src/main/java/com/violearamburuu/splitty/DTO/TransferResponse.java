package com.violearamburuu.splitty.DTO;

import java.math.BigDecimal;

public record TransferResponse (long fromUserId, long toUserId, BigDecimal amount) {
}
