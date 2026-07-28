package com.violearamburuu.splitty.controller;

import com.violearamburuu.splitty.DTO.GroupMembershipResponse;
import com.violearamburuu.splitty.DTO.GroupResponse;
import com.violearamburuu.splitty.model.Group;
import com.violearamburuu.splitty.model.GroupMembership;
import com.violearamburuu.splitty.model.User;
import com.violearamburuu.splitty.DTO.AddMemberRequest;
import com.violearamburuu.splitty.DTO.CreateGroupRequest;
import com.violearamburuu.splitty.services.GroupService;
import com.violearamburuu.splitty.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;

    public GroupController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    @PostMapping
    public GroupResponse createGroup(@RequestBody CreateGroupRequest request, Principal principal) {
        User creator = userService.findUserByEmail(principal.getName());
        Group group = groupService.createGroup(request.name(), creator);
        return new GroupResponse(group.getName());
    }

    @GetMapping("/{id}")
    public Group getGroup(@PathVariable long id) {
        return groupService.findGroupById(id);
    }

    @PostMapping("/{id}/members")
    public GroupMembershipResponse addMember(@PathVariable long id, @RequestBody AddMemberRequest request, Principal principal) {
        Group group = groupService.findGroupById(id);
        User currentUser = userService.findUserByEmail(principal.getName());
        User newMember = userService.findUserById(request.newMemberId());
        GroupMembership membership = groupService.addMemberToGroup(currentUser, newMember, group);
        return new GroupMembershipResponse(newMember.getId());
    }

}
