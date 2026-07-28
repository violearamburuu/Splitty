package com.violearamburuu.splitty.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "expenses")
@NoArgsConstructor
public class Expense {
    @Id
    @GeneratedValue
    private @Getter long id;
    @ManyToOne
    private @Setter @Getter User paidBy;
    @ManyToOne
    private @Setter @Getter Group group;
    private @Setter @Getter BigDecimal amount;

    public Expense(BigDecimal amount, User paidBy, Group group){
        this.amount = amount;
        this.paidBy = paidBy;
        this.group = group;
    }
}
