package com.allen.moments.v2.service;

import com.allen.moments.v2.dao.UserDao;
import com.allen.moments.v2.model.User;
import com.allen.moments.v2.redis.RedisUtil;
import com.allen.moments.v2.utils.JsonResult;
import com.allen.moments.v2.utils.ThreadPoolManager;
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

    public JsonResult<?> addUser(String email, String name, Integer sex, Integer age, String password) throws Exception {
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
            throw new Exception("unknown error");
        }
    }

    public boolean setNewPassword(int uid, String oldPassword, String newPassword) {
        User userToUpdate = userDao.selectByUid(uid);
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
            User userInDB = userDao.selectByUid(uid);
            if (userInDB == null) {
                throw new RuntimeException("user not found");
            }
            redis.hashSet("allUsers", String.valueOf(userInDB.getUid()), userInDB);
            return userInDB;
    }


    public List<User> getAllUsers() {
//           List<User> allUsersCached = (List<User>)(Object) redis.sGet("all_users_id");
//            if (allUsersCached.size() != 0) {
//                return allUsersCached;
//            }
            return userDao.selectAll();
//            if (allUsersDB != null) {
//                redis.sSet("all_users", allUsersDB);
//            }
    }

    public List<User> selectUsersOrderByFollowerCounts(int start, int limit) {
//        List<User> selectedUsersCached = redis.("");
        List<User> selectedUsers = userDao.selectUsersOrderByFollowerCounts(start, limit);
        return selectedUsers;
    }

    public JsonResult<?> getFollower(int uid) {
            String redisKey = "user:" + uid + ":follower";
            Set<Object> followersCached = redis.setGetAll(redisKey);
            if (followersCached != null) {
                return JsonResult.successWithData(followersCached);
            }
            List<Integer> followersInDB = userDao.selectFollowersById(uid);
            if (followersInDB != null) {
                redis.setSet(redisKey, followersInDB);
                return JsonResult.successWithData(followersInDB);
            }
            return JsonResult.success();
        }


    public JsonResult<?> getFollowing(int uid) {
            String redisKey = "user:" + uid + ":following";
            Set<Object> followingsCached = redis.setGetAll(redisKey);
            if (followingsCached != null) {
                return JsonResult.successWithData(followingsCached);
            }
            List<Integer> followingsInDB = userDao.selectFollowingsById(uid);
            if (followingsInDB != null) {
                redis.setSet(redisKey, followingsInDB);
                return JsonResult.successWithData(followingsInDB);
            }
            return JsonResult.success();
    }

    public JsonResult<?> follow(int followedId, int followerId) {
            userDao.addFollower(followedId, followerId);
            ThreadPoolManager.getInstance().execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            String followedRedisKey = "user:" + followedId + ":follower";
                            redis.setSet(followedRedisKey, followedId);

                            String followerRedisKey = "user:"+ followerId + ":following";
                            redis.setSet(followerRedisKey, followedId);
                        }
                    }
            );
            return JsonResult.success();
    }

    public JsonResult<?> unfollow(int followedId, int followerId) throws Exception {
            int rowsAffected = userDao.removeFollowingRelation(followedId, followerId);
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    redis.setRemove("user:" + followedId + ":followers", followerId);
                }
            });
            if (rowsAffected == 0) {
                return JsonResult.failure(20005, "no following relationship exists");
            }
            return JsonResult.success();
    }
}
