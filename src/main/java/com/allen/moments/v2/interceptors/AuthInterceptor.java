package com.allen.moments.v2.interceptors;

import com.allen.moments.v2.redis.RedisUtil;
import com.allen.moments.v2.utils.JsonResult;
import com.allen.moments.v2.utils.annotations.PassToken;
import com.allen.moments.v2.utils.annotations.RequireToken;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class) ) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken == null) {
                passToken = method.getClass().getAnnotation(PassToken.class);
            }
            if (passToken.isTokenNeedless()) {
                return true;
            }
        }
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(RequireToken.class) || method.getClass().isAnnotationPresent(RequireToken.class)) {
            RequireToken userLoginToken = method.getAnnotation(RequireToken.class);
            if (userLoginToken == null){
                userLoginToken = method.getClass().getAnnotation(RequireToken.class);
            }
            if (userLoginToken.isTokenNeeded()) {
                // 执行认证
                try {
                    String token = request.getHeader("Authorization").substring(7); // 从 http 请求头中取出 token
                    System.out.println(token);
                    if (token == null) {
                        throw new RuntimeException("no token found, login is needed");
                    }
                    // 验证 token
                    JWTVerifier jwtVerifier = com.auth0.jwt.JWT.require(Algorithm.HMAC256("${application.jwt.secret_key}")).build();
                    DecodedJWT jwt = jwtVerifier.verify(token);
                    int uid;
                    uid = (Integer.parseInt(jwt.getAudience().get(0)));
                    request.setAttribute("logged_uid", uid);
//                    isLogged = redisUtil.getBit("loggedUsers", uid - 10000); // use redis as the session manager
//                    if (!isLogged) {
//                        throw new RuntimeException("user not found, please login again");
//                    }
                }
                catch (Exception exception) {
                    ObjectMapper mapper = new ObjectMapper();
                    System.err.println(exception.getMessage());
                    JsonResult<?> jsonResult = JsonResult.failure(10000, "user not logged");// customised pojo for error json message
                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(mapper.writeValueAsString(jsonResult));
                    return false;
                }

           }
        }
        return true;
    }


}
