package com.itlyc.manager;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.itlyc.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AdminManager {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 生成验证码
     * @param uuid
     * @return
     */
    public LineCaptcha getCaptcha(String uuid) {
        //1.生成验证码图片
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(300, 150);

        //2.获取验证码
        String code = lineCaptcha.getCode();

        //3.将验证码放入redis中,设置时效5分钟
        redisTemplate.opsForValue().set(ConstantUtil.ADMIN_CODE + uuid,code, Duration.ofMinutes(5));

        //4.返回验证码图片
        return lineCaptcha;
    }
}
