package com.allen.demo.interceptors;

import com.allen.demo.redis.RedisUtil;
import com.allen.demo.utils.JsonResult;
import com.allen.demo.utils.annotations.PassToken;
import com.allen.demo.utils.annotations.RequireToken;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
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
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.isTokenNeedless()) {
                return true;
            }
        }
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(RequireToken.class)) {
            RequireToken userLoginToken = method.getAnnotation(RequireToken.class);
            if (userLoginToken.isTokenNeeded()) {
                // 执行认证
                try {
                    String token = request.getHeader("token"); // 从 http 请求头中取出 token
                    if (token == null) {
                        throw new RuntimeException("no token found, login is needed");
                    }
                    int uid;
                    Boolean isLogged;
                    try {
                        uid = (Integer.parseInt(com.auth0.jwt.JWT.decode(token).getAudience().get(0)));
                        isLogged = redisUtil.getBit("loggedUsers", uid - 10000);
                    } catch (JWTDecodeException j) {
                        throw new RuntimeException("502");
                    }
                    if (!isLogged) {
                        throw new RuntimeException("user not found, please login again");
                    }
                }
                catch (Exception exception) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonResult jsonResult = new JsonResult(10000,exception.getMessage());// customised pojo for error json message
                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(mapper.writeValueAsString(jsonResult));
                    return false;
                }
//                // 验证 token
//                JWTVerifier jwtVerifier = com.auth0.jwt.JWT.require(Algorithm.HMAC256(user.getPassword())).build();
//                try {
//                    jwtVerifier.verify(token);
//                } catch (JWTVerificationException e) {
//                    throw new RuntimeException("502");
//                }
                return true;
            }
        }
        return true;
    }


}
