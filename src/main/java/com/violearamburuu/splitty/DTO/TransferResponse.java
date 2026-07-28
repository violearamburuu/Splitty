package com.violearamburuu.splitty.DTO;

import java.math.BigDecimal;

public record TransferResponse (long toUserId, long fromUserId, BigDecimal amount) {
}
