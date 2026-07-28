package com.violearamburuu.splitty;

import com.violearamburuu.splitty.model.*;
import com.violearamburuu.splitty.repository.ExpenseRepository;
import com.violearamburuu.splitty.repository.ExpenseShareRepository;
import com.violearamburuu.splitty.services.BalanceService;
import com.violearamburuu.splitty.services.GroupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock private ExpenseRepository expenseRepository;
    @Mock private ExpenseShareRepository expenseShareRepository;
    @Mock private GroupService groupService;

    @InjectMocks private BalanceService balanceService;

    // --- calculateBalances ---

    @Test
    void calculateBalances_singleExpense_splitsCorrectly() {
        User ana = new User("Ana", "ana@test.com", "hash");
        User beto = new User("Beto", "beto@test.com", "hash");
        Group group = new Group("Trip");

        Expense expense = new Expense(new BigDecimal("60"), ana, group, "Dinner", LocalDate.now());
        ExpenseShare anaShare = new ExpenseShare(ana, expense, new BigDecimal("30"));
        ExpenseShare betoShare = new ExpenseShare(beto, expense, new BigDecimal("30"));

        when(groupService.getAllGroupMembers(group)).thenReturn(List.of(ana, beto));
        when(expenseRepository.findAllByGroup(group)).thenReturn(List.of(expense));
        when(expenseShareRepository.findAllByExpense(expense)).thenReturn(List.of(anaShare, betoShare));

        Map<User, BigDecimal> balances = balanceService.calculateBalances(group);

        assertEquals(0, balances.get(ana).compareTo(new BigDecimal("30")));
        assertEquals(0, balances.get(beto).compareTo(new BigDecimal("-30")));
    }

    @Test
    void calculateBalances_noExpenses_allZero() {
        User ana = new User("Ana", "ana@test.com", "hash");
        Group group = new Group("Trip");

        when(groupService.getAllGroupMembers(group)).thenReturn(List.of(ana));
        when(expenseRepository.findAllByGroup(group)).thenReturn(List.of());

        Map<User, BigDecimal> balances = balanceService.calculateBalances(group);

        assertEquals(0, balances.get(ana).compareTo(BigDecimal.ZERO));
    }

    // --- simplifyDebts ---

    @Test
    void simplifyDebts_singleDebt_oneTransfer() {
        User ana = new User("Ana", "ana@test.com", "hash");
        User beto = new User("Beto", "beto@test.com", "hash");
        Group group = new Group("Trip");

        // Ana paid 60, split 30/30 → Ana +30, Beto -30
        Expense expense = new Expense(new BigDecimal("60"), ana, group, "Dinner", LocalDate.now());
        ExpenseShare anaShare = new ExpenseShare(ana, expense, new BigDecimal("30"));
        ExpenseShare betoShare = new ExpenseShare(beto, expense, new BigDecimal("30"));

        when(groupService.getAllGroupMembers(group)).thenReturn(List.of(ana, beto));
        when(expenseRepository.findAllByGroup(group)).thenReturn(List.of(expense));
        when(expenseShareRepository.findAllByExpense(expense)).thenReturn(List.of(anaShare, betoShare));

        List<Transfer> transfers = balanceService.simplifyDebts(group);

        assertEquals(1, transfers.size());
        Transfer t = transfers.get(0);
        assertEquals(beto, t.getFromUser());   // debtor pays
        assertEquals(ana, t.getToUser());       // creditor receives
        assertEquals(0, t.getAmount().compareTo(new BigDecimal("30")));
    }

    @Test
    void simplifyDebts_everyoneSettled_noTransfers() {
        User ana = new User("Ana", "ana@test.com", "hash");
        User beto = new User("Beto", "beto@test.com", "hash");
        Group group = new Group("Trip");

        // Ana paid 60, but split so she owes it all back to herself → everyone net 0
        Expense expense = new Expense(new BigDecimal("60"), ana, group, "Dinner", LocalDate.now());
        ExpenseShare anaShare = new ExpenseShare(ana, expense, new BigDecimal("60"));

        when(groupService.getAllGroupMembers(group)).thenReturn(List.of(ana, beto));
        when(expenseRepository.findAllByGroup(group)).thenReturn(List.of(expense));
        when(expenseShareRepository.findAllByExpense(expense)).thenReturn(List.of(anaShare));

        List<Transfer> transfers = balanceService.simplifyDebts(group);

        assertTrue(transfers.isEmpty());
    }

    @Test
    void simplifyDebts_multipleDebtors_minimalTransfers() {
        User ana = new User("Ana", "ana@test.com", "hash");
        User beto = new User("Beto", "beto@test.com", "hash");
        User caro = new User("Caro", "caro@test.com", "hash");
        Group group = new Group("Trip");

        // Ana paid 90, split 30 each → Ana +60, Beto -30, Caro -30
        Expense expense = new Expense(new BigDecimal("90"), ana, group, "Dinner", LocalDate.now());
        ExpenseShare anaShare = new ExpenseShare(ana, expense, new BigDecimal("30"));
        ExpenseShare betoShare = new ExpenseShare(beto, expense, new BigDecimal("30"));
        ExpenseShare caroShare = new ExpenseShare(caro, expense, new BigDecimal("30"));

        when(groupService.getAllGroupMembers(group)).thenReturn(List.of(ana, beto, caro));
        when(expenseRepository.findAllByGroup(group)).thenReturn(List.of(expense));
        when(expenseShareRepository.findAllByExpense(expense)).thenReturn(List.of(anaShare, betoShare, caroShare));

        List<Transfer> transfers = balanceService.simplifyDebts(group);

        // Both Beto and Caro pay Ana → 2 transfers, both to Ana, summing to 60
        assertEquals(2, transfers.size());
        BigDecimal total = transfers.stream()
                .map(Transfer::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, total.compareTo(new BigDecimal("60")));
        assertTrue(transfers.stream().allMatch(t -> t.getToUser().equals(ana)));
    }
}