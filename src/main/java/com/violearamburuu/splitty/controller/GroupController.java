package com.violearamburuu.splitty.controller;

import com.violearamburuu.splitty.model.Group;
import com.violearamburuu.splitty.model.GroupMembership;
import com.violearamburuu.splitty.model.User;
import com.violearamburuu.splitty.DTO.AddMemberRequest;
import com.violearamburuu.splitty.DTO.CreateGroupRequest;
import com.violearamburuu.splitty.services.GroupService;
import com.violearamburuu.splitty.services.UserService;
import org.springframework.web.bind.annotation.*;

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
    public Group createGroup(@RequestBody CreateGroupRequest request) {
        User user = userService.findUserByEmail(request.creatorEmail());
        return groupService.createGroup(request.name(), user);
    }

    @GetMapping("/{id}")
    public Group getGroup(@PathVariable long id) {
        return groupService.findGroupById(id);
    }

    @PostMapping("/{id}/members")
    public GroupMembership addMember(@PathVariable long id, @RequestBody AddMemberRequest request) {
        Group group = groupService.findGroupById(id);
        User currentUser = userService.findUserById(request.currentUserId());
        User newMember = userService.findUserById(request.newMemberId());
        return groupService.addMemberToGroup(currentUser, newMember, group);
    }

}
