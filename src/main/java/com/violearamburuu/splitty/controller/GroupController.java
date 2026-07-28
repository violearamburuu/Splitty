package com.violearamburuu.splitty.controller;

import com.violearamburuu.splitty.model.Group;
import com.violearamburuu.splitty.model.User;
import com.violearamburuu.splitty.services.DTO.CreateGroupRequest;
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
        User user = userService.findUser(request.creatorEmail());
        return groupService.createGroup(request.name(), user);
    }

    @GetMapping("/{id}")
    public Group getGroup(@PathVariable long id) {
        return groupService.findGroupById(id);
    }
}
