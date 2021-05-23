package com.allen.moments.v2.service;

import com.allen.moments.v2.dao.UserDao;
import com.allen.moments.v2.model.User;
import com.allen.moments.v2.redis.RedisUtil;
import com.allen.moments.v2.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserDao userDao;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Autowired
    public AuthService(UserDao userDao, JwtUtil jwtUtil, RedisUtil redisUtil) {
        this.userDao = userDao;
        this.jwtUtil = jwtUtil;
        this.redisUtil = redisUtil;
    }

    public String login(String email, String password) {
            User user = userDao.selectByEmail(email);

            if (user == null) {
                throw new RuntimeException("user not found");
            }
            if (!password.equals(user.getPassword())) {
               throw new RuntimeException("Password incorrect");
            }
            return this.jwtUtil.getToken(user);
//            redisUtil.setBit("loggedUsers", user.getUid() - 10000, true);

    }
}
