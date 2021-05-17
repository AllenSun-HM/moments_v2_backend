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
    public static final String AUTH_HEADER_KEY = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public String getToken(User user) {
        int[] a = new int[5];
        String token = "";
        Arrays.copyOfRange(a, 0, 3);
        token = com.auth0.jwt.JWT.create().withAudience(String.valueOf(user.getUid()))
                .sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }
}