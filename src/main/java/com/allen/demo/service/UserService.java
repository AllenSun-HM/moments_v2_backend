package com.allen.demo.service;

import com.allen.demo.dao.UserDao;
import com.allen.demo.model.User;
import com.allen.demo.redis.RedisUtil;
import com.allen.demo.utils.JsonResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


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

    public JsonResult addUser(String email, String name, Integer sex, Integer age, String password) {
        User newUser = new User(name, email, maxUid + 1, age, sex, password);
        boolean isAddSuccess = false;
        try {
            userDao.insert(newUser);
            isAddSuccess = true;
            maxUid += 1;
        }
        catch (RuntimeException exception) {
            if (exception.getClass() == org.springframework.dao.DuplicateKeyException.class) {
                return new JsonResult(10003, "duplicate entry");
            }
            return new JsonResult(false);
        }
        return new JsonResult(true);
    }

    public boolean setNewPassword(int uid, String oldPassword, String newPassword) {
        User userToUpdate = userDao.selectByPrimaryKey(uid);
        if (userToUpdate != null && userToUpdate.getPassword().equals(oldPassword)) {
            userDao.updatePassword(uid, newPassword);
            return true;
        }
        return false;
    }

    /**
     * query in cache layer first,
     * if found, return;
     * if not found. query in DB layer and cache the result in redis
     * @param uid
     * @return
     */
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
            System.err.println(e.toString());
            return null;
        }
    }

    public JsonResult getFollower(int uid) {
        try {
            String userRedisKey = "user:" + String.valueOf(uid);
            User userInRedis = (User) redis.get(userRedisKey);
            if (userInRedis != null) {
                return new JsonResult(userInRedis.getFollowers());
            }
            User userInDB = userDao.selectByPrimaryKey(uid);
            if (userInDB != null) {
                return new JsonResult(userInDB.getFollowers());
            }
            return new JsonResult(10002, "user not found");
        }
        catch (RuntimeException exception) {
            return new JsonResult(false);
        }
    }

    public JsonResult getFollowing(int uid) {
        try {
            String userRedisKey = "user:" + String.valueOf(uid);
            User userInRedis = (User) redis.get(userRedisKey);
            if (userInRedis != null) {
                return new JsonResult(userInRedis.getFollowings());
            }
            User userInDB = userDao.selectByPrimaryKey(uid);
            if (userInDB != null) {
                return new JsonResult(userInDB.getFollowings());
            }
            return new JsonResult(10002, "user not found");
        }
        catch (RuntimeException exception) {
            return new JsonResult(false);
        }
    }

    public JsonResult Follow(int followerId, int followedId) {
        try {
            User follower = userDao.selectByPrimaryKey(followerId);
            follower.addFollowing(followedId);
            String followerRedisKey = "user:"+String.valueOf(followerId);
            userDao.addFollowing(followerId, follower.getFollowings());
            redis.set(followerRedisKey, follower);

            User followed = userDao.selectByPrimaryKey(followedId);
            followed.addFollowing(followedId);
            String followedRedisKey = "user:"+String.valueOf(followerId);
            userDao.addFollower(followedId, followed.getFollowers());
            redis.set(followerRedisKey, followed);

            return new JsonResult(true);
        }
        catch (RuntimeException exception) {
              return new JsonResult(false);
        }
    }
}
