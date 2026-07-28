package com.violearamburuu.splitty.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

public class Transfer {
    private @Getter @Setter User fromUser;
    private @Getter @Setter User toUser;
    private @Getter @Setter BigDecimal amount;

    public Transfer(User fromUser, User toUser, BigDecimal amount){
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
    }

}
