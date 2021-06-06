package com.allen.moments.v2.service;

import com.allen.moments.v2.dao.UserDao;
import com.allen.moments.v2.model.ErrorType;
import com.allen.moments.v2.model.User;
import com.allen.moments.v2.utils.ApplicationException;
import com.allen.moments.v2.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final UserDao userDao;
//    private final RedisUtil redisUtil;

    static {
        System.out.println(("Authentication service initialized at" + LocalDateTime.now()));
    }

    @Autowired
    public AuthService(UserDao userDao, JwtUtil jwtUtil) {
        this.userDao = userDao;
//        this.redisUtil = redisUtil;
    }

    public User login(String email, String password) {
            User user = userDao.selectByEmail(email);
            if (user == null) {
                throw new ApplicationException(ErrorType.USER_UNIDENTIFIED.errNo, ErrorType.USER_UNIDENTIFIED.message);
            }
            if (!password.equals(user.getPassword())) {
               throw new ApplicationException(ErrorType.PASSWORD_ACCOUNT_MISMATCH.errNo, ErrorType.PASSWORD_ACCOUNT_MISMATCH.message);
            }
            user.setPassword(null);
            return user;
//            redisUtil.setBit("loggedUsers", user.getUid() - 10000, true);
    }
}
