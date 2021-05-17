package com.allen.moments.v2.api;

import com.allen.moments.v2.service.AuthService;
import com.allen.moments.v2.utils.JsonResult;
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

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PassToken
    @PostMapping("")
    public JsonResult<HashMap> login(HttpServletRequest request, @JsonProperty("email") String email, @JsonProperty("password") String password) {
        String token = request.getHeader("token");
        if (token != null) {
            JsonResult jsonResult = new JsonResult<>(100002, "用户已经登陆，无需重复登录");
            return jsonResult;
        }
        try {
            token = authService.login(email, password);
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            JsonResult jsonResult = new JsonResult<>(100003, exception.getMessage());
            return jsonResult;
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("token", token);
        JsonResult<HashMap> jsonResult = new JsonResult(data);
        return jsonResult;
    }

}
