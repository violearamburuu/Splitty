package com.violearamburuu.splitty.controller;

import com.violearamburuu.splitty.model.User;
import com.violearamburuu.splitty.services.DTO.CreateUserRequest;
import com.violearamburuu.splitty.services.DTO.UserResponse;
import com.violearamburuu.splitty.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request){
        User user = userService.createUser(request.name(), request.email(), request.password());
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable long id) {
        User user = userService.findUserById(id);
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
