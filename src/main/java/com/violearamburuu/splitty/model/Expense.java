package com.violearamburuu.splitty.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

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
    private @Setter @Getter String description;
    private @Setter @Getter LocalDate date;

    public Expense(BigDecimal amount, User paidBy, Group group, String description, LocalDate date){
        this.amount = amount;
        this.paidBy = paidBy;
        this.group = group;
        this.description = description;
        this.date = date;
    }
}
