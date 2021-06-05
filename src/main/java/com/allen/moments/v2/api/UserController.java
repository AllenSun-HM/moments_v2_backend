package com.allen.moments.v2.api;

import com.allen.moments.v2.model.User;
import com.allen.moments.v2.service.S3Service;
import com.allen.moments.v2.service.UserService;
import com.allen.moments.v2.utils.JsonResult;
import com.allen.moments.v2.utils.JwtUtil;
import com.allen.moments.v2.utils.annotations.RequireToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * handle user info CRUD
 *
 */
@RestController
@RequestMapping("/api/v1/user")
@RequireToken
public class UserController {
    private final UserService userService;
    private final S3Service s3Service;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, S3Service s3Service, JwtUtil jwtUtil) {
        this.userService = userService;
        this.s3Service = s3Service;
        this.jwtUtil = jwtUtil;
    }

    private int getLoggedUid(HttpServletRequest request) {
        return (int) request.getAttribute("logged_uid");
    }

    @PostMapping("/register")
    @RequireToken
    public JsonResult<?> register(@JsonProperty("name") String name, @JsonProperty String email, @JsonProperty("sex") Integer sex, @JsonProperty("age") Integer age, @JsonProperty("password") String password) throws Exception {
        User addedUser = userService.addUser(email, name, sex, age, password);
        String token = jwtUtil.getToken(addedUser.getUid());
        return JsonResult.successWithData(token);
    }


    @GetMapping("/{id}")
    @RequireToken
    public JsonResult<?> getUserById(@PathVariable("id") int uid) {
        User user = userService.getUser(uid);
        if (user == null) {
            return JsonResult.failure(40001, "user does not exist");
        }
        return JsonResult.successWithData(user);
    }

    @PostMapping("/password")
    @RequireToken
    public JsonResult<?> setNewPassword(int uid, @RequestParam("old_passwd") String oldPassword, @RequestParam("new_passwd") String newPassword) throws Exception {
        userService.setNewPassword(uid, oldPassword, newPassword);
        return JsonResult.success();
    }

    @GetMapping("/get_all")
    @RequireToken
    public List<User> showAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/follow")
    @RequireToken
    public JsonResult<?> follow(HttpServletRequest request, @RequestParam("uid_to_follow") Integer uidToFollow) {
        int uidOfFollowed = this.getLoggedUid(request);
        userService.follow(uidOfFollowed, uidToFollow);
        return JsonResult.success();
    }

    @GetMapping("/{id}/followers")
    @RequireToken
    public JsonResult<?> getFollowers(@PathVariable("id") int uid) {
        return JsonResult.successWithData(userService.getFollowersId(uid));
    }

    @GetMapping("/{id}/followings")
    @RequireToken
    public JsonResult<?> getFollowings(@PathVariable("id") int uid) {
        return JsonResult.successWithData(userService.getFollowingsId(uid));
    }

    @DeleteMapping("/unfollow/{id}")
    @RequireToken
    public JsonResult<?> unfollow(HttpServletRequest request, @PathVariable("id") int followedId) throws Exception {
        int uidOfFollower = this.getLoggedUid(request);
        userService.unfollow(followedId, uidOfFollower);
        return JsonResult.success();
    }

    @GetMapping("/rank/follower")
    @RequireToken
    public JsonResult<?> getPopularUsers(HttpServletRequest request, int start, int limit) throws Exception {
        return JsonResult.successWithData(userService.selectUsersOrderByFollowerCounts(start, limit));
    }

    @PostMapping("/avatar")
    @RequireToken
    public JsonResult<?> setCustomizedAvatar(HttpServletRequest request, MultipartFile avatar) throws Exception {
        int uid = this.getLoggedUid(request);
        String avatarURI = s3Service.upload(avatar);
        userService.addCustomizedAvatar(uid, avatarURI);
        return JsonResult.successWithData(avatarURI);
    }
}
