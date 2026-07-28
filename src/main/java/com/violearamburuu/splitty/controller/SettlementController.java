package com.violearamburuu.splitty.controller;


import com.violearamburuu.splitty.model.Group;
import com.violearamburuu.splitty.model.Settlement;
import com.violearamburuu.splitty.model.User;
import com.violearamburuu.splitty.DTO.RecordSettlementRequest;
import com.violearamburuu.splitty.DTO.SettlementResponse;
import com.violearamburuu.splitty.services.GroupService;
import com.violearamburuu.splitty.services.SettlementService;
import com.violearamburuu.splitty.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/settlements")
public class SettlementController {
    private final SettlementService settlementService;
    private final UserService userService;
    private final GroupService groupService;

    public SettlementController(SettlementService settlementService, UserService userService, GroupService groupService) {
        this.settlementService = settlementService;
        this.userService = userService;
        this.groupService = groupService;
    }

    @PostMapping
    public SettlementResponse recordSettlement(@RequestBody RecordSettlementRequest request) {
        User fromUser = userService.findUserById(request.fromUserId());
        User toUser = userService.findUserById(request.toUserId());
        Group group = groupService.findGroupById(request.groupId());
        Settlement settlement = settlementService.recordSettlement(fromUser, toUser, group, request.amount(), request.date());
        return new SettlementResponse(fromUser.getId(), toUser.getId(), group.getId(), settlement.getAmount(), settlement.getDate());
    }

    @GetMapping("/{id}")
    public List<SettlementResponse> getSettlementsByGroup(@PathVariable long groupId) {
        Group group = groupService.findGroupById(groupId);
        List<Settlement> settlements = settlementService.listSettlementsByGroup(group);
        List<SettlementResponse> settlementResponses = new ArrayList<SettlementResponse>();
        for (Settlement settlement : settlements) {
            settlementResponses.add(new SettlementResponse(settlement.getFromUser().getId(), settlement.getToUser().getId(), settlement.getGroup().getId(), settlement.getAmount(), settlement.getDate()));
        }
        return settlementResponses;
    }

    @GetMapping("/{id}")
    public List<SettlementResponse> getSettlementsByUser(@PathVariable long userId) {
        User user = userService.findUserById(userId);
        List<Settlement> settlements = settlementService.listSettlementsByUser(user);
        List<SettlementResponse> settlementResponses = new ArrayList<SettlementResponse>();
        for (Settlement settlement : settlements) {
            settlementResponses.add(new SettlementResponse(settlement.getFromUser().getId(), settlement.getToUser().getId(), settlement.getGroup().getId(), settlement.getAmount(), settlement.getDate()));
        }
        return settlementResponses;
    }
}