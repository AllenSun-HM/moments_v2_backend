package com.allen.moments.v2.service;

import com.allen.moments.v2.dao.UserDao;
import com.allen.moments.v2.model.User;
import com.allen.moments.v2.redis.RedisUtil;
import com.allen.moments.v2.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;


/**
 * service layer for userinfo-related operations
 * basic logic:
     * query in redis(cache layer) first,
     * if found, return;
     * if not found. query in DB layer and cache the result in redis
 */
@Service
public class UserService {
    private final UserDao userDao;
    private final RedisUtil redis;
    private static int maxUid;

    static {
        System.out.println("User service initialized");
    }

    @Autowired
    public UserService(UserDao userDao, RedisUtil redis) {
        this.userDao = userDao;
        this.redis = redis;
        maxUid = this.userDao.getMaxUid();
    }

    public JsonResult<?> addUser(String email, String name, Integer sex, Integer age, String password) {
        User newUser = new User(name, email, maxUid + 1, age, sex, password);
        try {
            userDao.insert(newUser);
            maxUid += 1;
            return JsonResult.success();
        }
        catch (RuntimeException exception) {
            if (exception.getClass() == org.springframework.dao.DuplicateKeyException.class) {
                return JsonResult.failure(10003, "user already registered");
            }
            return JsonResult.unknownFailure();
        }
    }

    public boolean setNewPassword(int uid, String oldPassword, String newPassword) {
        User userToUpdate = userDao.selectByPrimaryKey(uid);
        if (userToUpdate != null && userToUpdate.getPassword().equals(oldPassword)) {
            userDao.updatePassword(uid, newPassword);
            return true;
        }
        return false;
    }

    public User getUser(int uid) {
            User cachedUser = (User) redis.hashGet("allUsers", String.valueOf(uid));
            if (cachedUser != null) {
                return cachedUser;
            }
            User userInDB = userDao.selectByPrimaryKey(uid);
            if (userInDB == null) {
                throw new RuntimeException("user not found");
            }
            redis.hset("allUsers", String.valueOf(userInDB.getUid()), userInDB);
            return userInDB;
    }


    public List<User> getAllUsers() {
        try {
            List<User> allUsers = (List<User>)(Object)redis.hashGetAll("allUsers").values();
            if (allUsers.size() != 0) {
                return allUsers;
            }
            return userDao.selectAll();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public JsonResult<?> getFollower(int uid) {
            String redisKey = "user:" + uid + ":follower";
            Set<Object> followersCached = redis.sGet(redisKey);
            if (followersCached != null) {
                return JsonResult.successWithData(followersCached);
            }
            List<Integer> followersInDB = userDao.selectFollowersById(uid);
            if (followersInDB != null) {
                return JsonResult.successWithData(followersInDB);
            }
            return JsonResult.success();
        }


    public JsonResult<?> getFollowing(int uid) {
            String redisKey = "user:" + uid + ":following";
            Set<Object> followingsCached = redis.sGet(redisKey);
            if (followingsCached != null) {
                return JsonResult.successWithData(followingsCached);
            }
            List<Integer> followingsInDB = userDao.selectFollowingsById(uid);
            if (followingsInDB != null) {
                return JsonResult.successWithData(followingsInDB);
            }
            return JsonResult.success();
    }

    public JsonResult<?> follow(int followedId, int followerId) {
        try {
            userDao.addFollower(followedId, followerId);

            String followedRedisKey = "user:" + followedId + ":follower";
            redis.sSet(followedRedisKey, followedId);

            String followerRedisKey = "user:"+ followerId + ":following";
            redis.sSet(followerRedisKey, followedId);

            return JsonResult.success();
        }
        catch (RuntimeException exception) {
            System.out.println(exception.getMessage());
              return JsonResult.unknownFailure();
        }
    }

    public JsonResult<?> unfollow(int followedId, int followerId) {
        try {
            int rowsAffected = userDao.removeFollowingRelation(followedId, followerId);
            if (rowsAffected == 0) {
                throw new Exception("no following relationship exists");
            }
            return JsonResult.success();
        }
        catch(Exception exception) {
            return JsonResult.failure(30001, exception.getMessage());
        }
    }
}
