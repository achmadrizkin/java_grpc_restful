package com.example.demo.controller.api;

import com.example.demo.model.Response;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Response<List<User>>> getAllUser() {
        return userService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Response<User>> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Response<User>> loginUser(@RequestBody User userReq) {
        return userService.loginUser(userReq);
    }
}
