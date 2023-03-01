package com.itlyc.controller;

import cn.hutool.captcha.LineCaptcha;
import com.itlyc.manager.AdminManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
}
