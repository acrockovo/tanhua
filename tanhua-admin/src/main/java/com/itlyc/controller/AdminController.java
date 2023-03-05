package com.itlyc.controller;

import cn.hutool.captcha.LineCaptcha;
import com.itlyc.manager.AdminManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
public class AdminController {

    @Autowired
    private AdminManager adminManager;

    /**
     * 获取验证码图片
     * @param uuid
     * @param response
     * @throws IOException
     */
    @GetMapping("/system/users/verification")
    public void getVerification(String uuid, HttpServletResponse response) throws IOException {

        LineCaptcha lineCaptcha = adminManager.getCaptcha(uuid);

        lineCaptcha.write(response.getOutputStream());
    }

    /**
     * 管理员登录
     * @param map 登录对象
     * @return
     */
    @PostMapping("/system/users/login")
    public ResponseEntity login(@RequestBody Map<String, String> map){
        String username = map.get("username");
        String password = map.get("password");
        String verificationCode = map.get("verificationCode");
        String uuid = map.get("uuid");

        return adminManager.login(username, password, verificationCode, uuid);
    }

    /**
     * 管理基本信息
     * @param token 令牌
     * @return
     */
    @PostMapping("/system/users/profile")
    public ResponseEntity findAdminInfo(@RequestHeader("Authorization") String token){
        return adminManager.findAdminInfo(token);
    }
}
