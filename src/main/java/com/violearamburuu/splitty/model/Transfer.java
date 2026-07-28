package com.violearamburuu.splitty.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "transfers")
@NoArgsConstructor
public class Transfer {
    @Id
    @GeneratedValue
    private @Getter long id;
    private @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "from_id") User fromUser;
    private @Getter @Setter @ManyToOne
    @JoinColumn(name = "from_id") User toUser;
    private @Getter @Setter BigDecimal amount;

    public Transfer(User fromUser, User toUser, BigDecimal amount){
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
    }

}
