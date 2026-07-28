package com.violearamburuu.splitty.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecordSettlementRequest (long fromUserId, long toUserId, long groupId, BigDecimal amount, LocalDate date){
}
