package com.allen.demo.service;

import com.allen.demo.dao.UserDao;
import com.allen.demo.model.User;
import com.allen.demo.redis.RedisUtil;
import com.allen.demo.utils.JwtUtil;
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
            String token = this.jwtUtil.getToken(user);
            redisUtil.setBit("loggedUsers", user.getUid() - 10000, true);
        return token;
    }
}
