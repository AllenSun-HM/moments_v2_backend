package com.allen.moments.v2.api;

import com.allen.moments.v2.model.User;
import com.allen.moments.v2.service.UserService;
import com.allen.moments.v2.utils.JsonResult;
import com.allen.moments.v2.utils.annotations.RequireToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
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
    public UserController(UserService userService, RedisTemplate<String, Object> redisTemplate) {
        this.userService = userService;
    }

    @PostMapping()
    @RequireToken
    public JsonResult<?> addUser(@JsonProperty("name") String name, @JsonProperty String email, @JsonProperty("sex") Integer sex, @JsonProperty("age") Integer age, @JsonProperty("password") String password) throws Exception {
        return userService.addUser(email, name, sex, age, password);
    }

    @GetMapping("/{id}")
    @RequireToken
    public User getUser(@PathVariable("id") int uid) {
        return userService.getUser(uid);
    }

    @PostMapping("password")
    @RequireToken
    public String setNewPassword(int uid, @RequestParam("old_passwd") String oldPassword, @RequestParam("new_passwd") String newPassword) {
        boolean isUpdateSuccess = userService.setNewPassword(uid, oldPassword, newPassword);
        return isUpdateSuccess ? "success" : "failure";
    }

    @GetMapping("/get_all")
    @RequireToken
    public List<User> showAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/follow")
    @RequireToken
    public JsonResult<?> follow(HttpServletRequest request, @RequestParam("uid_to_follow") Integer uidToFollow) {
        int uidOfFollowed = (int) request.getAttribute("logged_uid");
        return userService.follow(uidOfFollowed, uidToFollow);
    }

    @GetMapping("/{id}/followers")
    @RequireToken
    public JsonResult<?> getFollowers(@PathVariable("id") int uid) {
        return userService.getFollower(uid);
    }

    @GetMapping("/{id}/followings")
    @RequireToken
    public JsonResult<?> getFollowings(@PathVariable("id") int uid) {
        return userService.getFollowing(uid);
    }

    @DeleteMapping("/unfollow/{id}")
    @RequireToken
    public JsonResult<?> unfollow(HttpServletRequest request, @PathVariable("id") int followedId) throws Exception {
        int followerId = (int) request.getAttribute("logged_uid");
        return userService.unfollow(followedId, followerId);
    }
}
