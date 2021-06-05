package com.allen.moments.v2.utils;
import com.allen.moments.v2.model.User;
import com.auth0.jwt.algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtUtil {
    private static Logger log = LoggerFactory.getLogger(JwtUtil.class);
    private static final long aWeekInMs = 604800000;
    public String getToken(Integer uid) {
        String token = "";
        token = com.auth0.jwt.JWT.create().withAudience(String.valueOf(uid)).withExpiresAt(new Date(System.currentTimeMillis() + aWeekInMs))
                .sign(Algorithm.HMAC256("${application.jwt.secret_key}"));
        return token;
    }
}

