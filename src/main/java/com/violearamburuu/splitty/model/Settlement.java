package com.violearamburuu.splitty.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "settlements")
@NoArgsConstructor
public class Settlement {
    @Id
    @GeneratedValue
    private @Getter long id;
    @ManyToOne
    private @Getter @Setter User fromUser;
    @ManyToOne
    private @Getter @Setter User toUser;
    @ManyToOne
    private @Getter @Setter Group group;
    private @Getter @Setter BigDecimal amount;
    private @Getter @Setter Date date;

    public Settlement(User fromUser, User toUser, Group group, BigDecimal amount, Date date){
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.group = group;
        this.amount = amount;
        this.date = date;
    }
}
