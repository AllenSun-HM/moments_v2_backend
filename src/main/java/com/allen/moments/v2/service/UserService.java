package com.allen.moments.v2.service;

import com.allen.moments.v2.dao.UserDao;
import com.allen.moments.v2.model.ErrorType;
import com.allen.moments.v2.model.User;
import com.allen.moments.v2.redis.RedisUtil;
import com.allen.moments.v2.utils.ApplicationException;
import com.allen.moments.v2.utils.ThreadPoolManager;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.allen.moments.v2.utils.errorHandler.DBExceptionChecker.checkIfRowsAffectedIsOne;


/**
 * @Description service layer for userinfo-related operations
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
        LogManager.getLogger(UserService.class).info("User service initialized");
    }

    @Autowired
    public UserService(UserDao userDao, RedisUtil redis) {
        this.userDao = userDao;
        this.redis = redis;
        maxUid = this.userDao.getMaxUid();
    }

    public User addUser(String email, String name, Integer sex, Integer age, String password) throws Exception {
        User newUser = new User(name, email, maxUid + 1, age, sex, password);
        try {
            int rowsAffected = userDao.insert(newUser);
            checkIfRowsAffectedIsOne(rowsAffected, ErrorType.UNKNOWN_ERROR);
            maxUid += 1;
            return newUser;
        }
        catch (RuntimeException exception) {
            if (exception.getClass() == org.springframework.dao.DuplicateKeyException.class) {
                throw new ApplicationException(ErrorType.USER_ALREADY_REGISTERED.errNo, ErrorType.USER_ALREADY_REGISTERED.message);
            }
            throw new ApplicationException(exception.getMessage());
        }
    }

    public void setNewPassword(int uid, String oldPassword, String newPassword) throws Exception {
            int rowsAffected = userDao.updatePassword(uid, oldPassword, newPassword);
            checkIfRowsAffectedIsOne(rowsAffected, ErrorType.USER_UNIDENTIFIED);
    }

    public @Nullable
    User getUser(int uid) {
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

    public List<User> selectUsersOrderByFollowerCounts(int start, int limit) {
        try {
            if (start < 100) { // to avoid having big key in redis, only writes the first 100 popular posts into redis
                List<User> usersCached = redis.listGet("usersWithHighestFollowerCounts", start, limit);
                if (usersCached != null) {
                    return usersCached;
                }
            }
            List<User>  usersInDB = userDao.selectUsersOrderByFollowerCounts(start, limit);
            redis.listMSetWithExpiration("postsWithHighestLikeCounts", usersInDB.subList(0, Math.min(limit, 100)), 20);
            return usersInDB;
        }
        catch (ClassCastException castException) {
            return null;
        }
    }

    public Set<Integer> getFollowersId(int uid) {
            String redisKey = "user:" + uid + ":follower";
            Set<Integer> followersCached = redis.setGetAll(redisKey);
            if (followersCached != null) {
                return followersCached;
            }
            Set<Integer> followersInDB = userDao.selectFollowersById(uid);
            if (followersInDB != null) {
                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        redis.setSet(redisKey, followersInDB);
                    }
                });
                return followersInDB;
            }
            return null;
        }


    public Set<Integer> getFollowingsId(int uid) {
            String redisKey = "user:" + uid + ":following";
            Set<Integer> followingsCached = redis.setGetAll(redisKey);
            if (followingsCached != null && followingsCached.size() > 0) {
                return followingsCached;
            }
            Set<Integer> followingsInDB = (Set<Integer>) userDao.selectFollowingsById(uid);
            if (followingsInDB != null) {
                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        redis.setSet(redisKey, followingsInDB);
                    }
                });
                return followingsInDB;
            }
            return null;
    }

    public void follow(int followedId, int followerId) {
        try {
            int rowsAffected = userDao.addFollower(followedId, followerId);
            ThreadPoolManager.getInstance().execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            String followedRedisKey = "user:" + followedId + ":follower";
                            redis.setSet(followedRedisKey, followedId);

                            String followerRedisKey = "user:" + followerId + ":following";
                            redis.setSet(followerRedisKey, followedId);
                        }
                    }
            );
        }
        catch(Exception exception) {
            if (exception.getClass() == org.springframework.dao.DuplicateKeyException.class) {
                throw new ApplicationException(ErrorType.USER_ALREADY_FOLLOWED.errNo, ErrorType.USER_ALREADY_FOLLOWED.message);
            }
            throw new ApplicationException(exception.getMessage());
        }
    }

    public void unfollow(int followedId, int followerId) throws Exception {
            int rowsAffected = userDao.removeFollowingRelation(followedId, followerId);
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    redis.setRemove("user:" + followedId + ":followers", followerId);
                }
            });
            checkIfRowsAffectedIsOne(rowsAffected, ErrorType.NO_FOLLOWING_RELATION);

    }

    public void addCustomizedAvatar(int uid, String avatarURI) throws Exception {
        int rowsAffected = userDao.addAvatarURI(uid, avatarURI);
        checkIfRowsAffectedIsOne(rowsAffected, ErrorType.USER_UNIDENTIFIED);
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

}
