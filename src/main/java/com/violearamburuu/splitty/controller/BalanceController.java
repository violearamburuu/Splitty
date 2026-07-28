package com.violearamburuu.splitty.controller;

import com.violearamburuu.splitty.model.Group;
import com.violearamburuu.splitty.model.Transfer;
import com.violearamburuu.splitty.model.User;
import com.violearamburuu.splitty.services.BalanceService;
import com.violearamburuu.splitty.DTO.BalanceResponse;
import com.violearamburuu.splitty.DTO.TransferResponse;
import com.violearamburuu.splitty.services.GroupService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groups/{groupId}")
public class BalanceController {
    private final BalanceService balanceService;
    private final GroupService groupService;

    public BalanceController(BalanceService balanceService, GroupService groupService) {
        this.balanceService = balanceService;
        this.groupService = groupService;
    }

    @GetMapping("/balances")
    public List<BalanceResponse> getNetBalances(@PathVariable long groupId){
        Group group = groupService.findGroupById(groupId);
        Map<User, BigDecimal> balances = balanceService.calculateBalances(group);
        List<BalanceResponse> balanceResponses = new ArrayList<BalanceResponse>();
        for(Map.Entry<User, BigDecimal> balance : balances.entrySet()){
            balanceResponses.add(new BalanceResponse(balance.getKey().getId(), balance.getKey().getName(), balance.getValue()));
        }
        return balanceResponses;
    }

    @GetMapping("/settle")
    public List<TransferResponse> settle(@PathVariable long groupId){
        Group group = groupService.findGroupById(groupId);
        List<Transfer> transfers = balanceService.simplifyDebts(group);
        List<TransferResponse> transferResponses = new ArrayList<TransferResponse>();
        for(Transfer transfer : transfers){
            transferResponses.add(new TransferResponse(transfer.getFromUser().getId(), transfer.getToUser().getId(), transfer.getAmount()));
        }
        return transferResponses;
    }
}
