package com.violearamburuu.splitty.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "expense_shares")
@NoArgsConstructor
public class ExpenseShare
{
    @Id
    @GeneratedValue
    private @Getter long id;
    @ManyToOne
    private @Setter @Getter User debtor;
    @ManyToOne
    private @Setter @Getter Expense expense;
    private @Setter @Getter BigDecimal owedAmount;

    public ExpenseShare(User debtor, Expense expense, BigDecimal owedAmount){
        this.debtor = debtor;
        this.expense = expense;
        this.owedAmount = owedAmount;
    }
}
