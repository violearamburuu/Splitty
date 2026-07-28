package com.violearamburuu.splitty;

import com.violearamburuu.splitty.model.*;
import com.violearamburuu.splitty.repository.ExpenseRepository;
import com.violearamburuu.splitty.repository.ExpenseShareRepository;
import com.violearamburuu.splitty.repository.GroupMembershipRepository;
import com.violearamburuu.splitty.repository.GroupRepository;
import com.violearamburuu.splitty.services.ExpenseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock private ExpenseRepository expenseRepository;
    @Mock private ExpenseShareRepository expenseShareRepository;
    @Mock private GroupMembershipRepository groupMembershipRepository;
    @Mock private GroupRepository groupRepository;

    @InjectMocks private ExpenseService expenseService;

    @Test
    void createExpense_validInput_savesExpenseAndShares() {
        User ana = new User("Ana", "ana@test.com", "hash");
        User beto = new User("Beto", "beto@test.com", "hash");
        Group group = new Group("Trip");

        // payer is a member
        when(groupMembershipRepository.findByUserAndGroup(ana, group))
                .thenReturn(Optional.of(new GroupMembership(ana, group, GroupRole.OWNER)));
        when(expenseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Map<User, BigDecimal> shares = Map.of(
                ana, new BigDecimal("30"),
                beto, new BigDecimal("30")
        );
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        expenseService.createExpense(ana, group, new BigDecimal("60"),
                LocalDate.now(), "Dinner", shares);

        verify(expenseRepository).save(any());           // expense saved
        verify(expenseShareRepository, times(2)).save(any()); // two shares saved
    }

    @Test
    void createExpense_sharesDontSumToTotal_throwsAndSavesNothing() {
        User ana = new User("Ana", "ana@test.com", "hash");
        User beto = new User("Beto", "beto@test.com", "hash");
        Group group = new Group("Trip");

        when(groupMembershipRepository.findByUserAndGroup(ana, group))
                .thenReturn(Optional.of(new GroupMembership(ana, group, GroupRole.OWNER)));

        // shares sum to 50, but total is 60 → invalid
        Map<User, BigDecimal> shares = Map.of(
                ana, new BigDecimal("30"),
                beto, new BigDecimal("20")
        );

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        assertThrows(RuntimeException.class, () ->
                expenseService.createExpense(ana, group, new BigDecimal("60"),
                        LocalDate.now(), "Dinner", shares));

        verify(expenseRepository, never()).save(any());  // nothing saved
    }

    @Test
    void createExpense_zeroAmount_throws() {
        User ana = new User("Ana", "ana@test.com", "hash");
        Group group = new Group("Trip");

        when(groupMembershipRepository.findByUserAndGroup(ana, group))
                .thenReturn(Optional.of(new GroupMembership(ana, group, GroupRole.OWNER)));

        Map<User, BigDecimal> shares = Map.of(ana, BigDecimal.ZERO);

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        assertThrows(RuntimeException.class, () ->
                expenseService.createExpense(ana, group, BigDecimal.ZERO,
                        LocalDate.now(), "Dinner", shares));

        verify(expenseRepository, never()).save(any());
    }

    @Test
    void createExpense_payerNotMember_throws() {
        User ana = new User("Ana", "ana@test.com", "hash");
        Group group = new Group("Trip");

        // payer has NO membership
        when(groupMembershipRepository.findByUserAndGroup(ana, group))
                .thenReturn(Optional.empty());

        Map<User, BigDecimal> shares = Map.of(ana, new BigDecimal("60"));

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        assertThrows(RuntimeException.class, () ->
                expenseService.createExpense(ana, group, new BigDecimal("60"),
                        LocalDate.now(), "Dinner", shares));

        verify(expenseRepository, never()).save(any());
    }
}