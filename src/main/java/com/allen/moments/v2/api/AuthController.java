package com.allen.moments.v2.api;

import com.allen.moments.v2.model.User;
import com.allen.moments.v2.service.AuthService;
import com.allen.moments.v2.utils.JsonResult;
import com.allen.moments.v2.utils.JwtUtil;
import com.allen.moments.v2.utils.annotations.PassToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/login")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }


    @PassToken
    @PostMapping("")
    public JsonResult<?> login(HttpServletRequest request, @JsonProperty("email") String email, @JsonProperty("password") String password) {
        String token = request.getHeader("token");
        if (token != null) {
            return JsonResult.failure(100002, "user already logged in");
        }
        try {
            User user = authService.login(email, password);
            token = this.jwtUtil.getToken(user.getUid());
            HashMap<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user_info", user);
            return JsonResult.successWithData(data);
        }
        catch (RuntimeException exception) {
            exception.printStackTrace();
            return JsonResult.failure(100003, exception.getMessage());
        }
    }

}
