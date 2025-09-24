package org.example.information_security_lab1.controller;

import org.example.information_security_lab1.model.User;
import org.example.information_security_lab1.service.UserService;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/data")
    public List<User> getAllUsers() {
        List<User> users = userService.getAllUsers();
        users.forEach(u -> u.setUsername(Encode.forHtml(u.getUsername())));
        return users;
    }
}
