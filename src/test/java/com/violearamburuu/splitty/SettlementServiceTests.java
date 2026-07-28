package com.violearamburuu.splitty;

import com.violearamburuu.splitty.model.*;
import com.violearamburuu.splitty.repository.GroupRepository;
import com.violearamburuu.splitty.repository.SettlementRepository;
import com.violearamburuu.splitty.services.GroupService;
import com.violearamburuu.splitty.services.SettlementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SettlementServiceTest {

    @Mock private SettlementRepository settlementRepository;
    @Mock private GroupRepository groupRepository;
    @Mock private GroupService groupService;

    @InjectMocks private SettlementService settlementService;

    @Test
    void recordSettlement_validInput_saves() {
        User ana = new User("Ana", "ana@test.com", "hash");
        User beto = new User("Beto", "beto@test.com", "hash");
        Group group = new Group("Trip");

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupService.getAllGroupMembers(group)).thenReturn(List.of(ana, beto));
        when(settlementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        settlementService.recordSettlement(beto, ana, group, new BigDecimal("30"), LocalDate.now());

        verify(settlementRepository).save(any());
    }

    @Test
    void recordSettlement_userNotMember_throws() {
        User ana = new User("Ana", "ana@test.com", "hash");
        User beto = new User("Beto", "beto@test.com", "hash");
        Group group = new Group("Trip");

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupService.getAllGroupMembers(group)).thenReturn(List.of(ana)); // Beto missing

        assertThrows(RuntimeException.class, () ->
                settlementService.recordSettlement(beto, ana, group, new BigDecimal("30"), LocalDate.now()));

        verify(settlementRepository, never()).save(any());
    }

    @Test
    void recordSettlement_zeroAmount_throws() {
        User ana = new User("Ana", "ana@test.com", "hash");
        User beto = new User("Beto", "beto@test.com", "hash");
        Group group = new Group("Trip");

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupService.getAllGroupMembers(group)).thenReturn(List.of(ana, beto));

        assertThrows(RuntimeException.class, () ->
                settlementService.recordSettlement(beto, ana, group, BigDecimal.ZERO, LocalDate.now()));

        verify(settlementRepository, never()).save(any());
    }
}