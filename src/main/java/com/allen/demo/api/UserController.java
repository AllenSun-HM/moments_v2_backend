package com.allen.demo.api;

import com.allen.demo.model.User;
import com.allen.demo.service.UserService;
import com.allen.demo.utils.JsonResult;
import com.allen.demo.utils.annotations.RequireToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * handle person info CRUD
 *
 */
@RestController
@RequestMapping("/api/v1/user")
@RequireToken
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService, RedisTemplate redisTemplate) {
        this.userService = userService;
    }

    @PostMapping()
    public JsonResult addUser(@JsonProperty("name") String name, @JsonProperty String email, @JsonProperty("sex") Integer sex, @JsonProperty("age") Integer age, @JsonProperty("password") String password) {
        return userService.addUser(email, name, sex, age, password);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") int uid) {
        return userService.getUser(uid);
    }

    @PostMapping("change_password")
    public String setNewPassword(@JsonProperty("uid") int uid, @JsonProperty("old_passwd") String oldPassword, @JsonProperty("new_passwd") String newPassword) {
        boolean isUpdateSuccess = userService.setNewPassword(uid, oldPassword, newPassword);
        return isUpdateSuccess ? "success" : "failure";
    }

    @GetMapping("/get_all")
    public List<User> showAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/follow")
    @RequireToken
    public List<User> follow() {
        return userService.getAllUsers();
    }

//    @GetMapping("/test")
//    public String test() {
//        return "a";
//    }
}
