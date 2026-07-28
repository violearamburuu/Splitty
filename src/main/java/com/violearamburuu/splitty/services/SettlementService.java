package com.violearamburuu.splitty.services;

import com.violearamburuu.splitty.model.*;
import com.violearamburuu.splitty.repository.GroupRepository;
import com.violearamburuu.splitty.repository.SettlementRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SettlementService {
    private final SettlementRepository settlementRepository;
    private final GroupRepository groupRepository;
    private final GroupService groupService;

    public SettlementService(SettlementRepository settlementRepository, GroupRepository groupRepository, GroupService groupService) {
        this.settlementRepository = settlementRepository;
        this.groupRepository = groupRepository;
        this.groupService = groupService;
    }

    public Settlement recordSettlement(User fromUser, User toUser, Group group, BigDecimal amount, LocalDate date){
        groupRepository.findById(group.getId()).orElseThrow(() -> new RuntimeException("This group does not exist."));
        List<User> members = groupService.getAllGroupMembers(group);
        if(!members.contains(fromUser) || !members.contains(toUser)) throw new RuntimeException("These users do not belong to this group.");
        if (toUser.getEmail().equals(fromUser.getEmail())) throw new RuntimeException("You cannot settle with yourself.");
        if(amount.compareTo(BigDecimal.ZERO) <= 0) throw new RuntimeException("Amount cannot be less or equal to zero.");

        Settlement settlement = new Settlement(fromUser, toUser, group, amount, date);
        settlementRepository.save(settlement);
        return settlement;
    }

    public List<Settlement> listSettlementsByUser(User user){
        List<Settlement> settlements = settlementRepository.findAllByFromUser(user);
        settlements.addAll(settlementRepository.findAllByToUser(user));
        return settlements;
    }

    public List<Settlement> listSettlementsByGroup(Group group){
        List<Settlement> settlements = settlementRepository.findAllByGroup(group);
        return settlements;
    }
}
