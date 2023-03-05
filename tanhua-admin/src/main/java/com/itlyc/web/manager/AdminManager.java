package com.itlyc.web.manager;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.itlyc.domain.db.Admin;
import com.itlyc.service.db.AdminService;
import com.itlyc.util.ConstantUtil;
import com.itlyc.util.JwtUtil;
import com.itlyc.web.exception.BusinessException;
import com.itlyc.web.interceptor.AdminHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AdminManager {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Reference
    private AdminService adminService;

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
        redisTemplate.opsForValue().set(ConstantUtil.ADMIN_CODE + uuid,code, Duration.ofMillis(10000));

        //4.返回验证码图片
        return lineCaptcha;
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @param verificationCode 验证码
     * @param uuid 唯一标识
     * @return
     */
    public ResponseEntity login(String username, String password, String verificationCode, String uuid) {
        // 从redis中获取验证码
        String redisCode = redisTemplate.opsForValue().get(ConstantUtil.ADMIN_CODE + uuid);

        // 比较验证码
        if(!StringUtils.equals(verificationCode, redisCode)){
            throw new BusinessException("验证码错误");
        }

        Admin admin = adminService.findUserByName(username);
        if(admin == null){
            throw new BusinessException("用户不存在");
        }
        if(!StringUtils.equals(admin.getPassword(), SecureUtil.md5(password))){
            throw new BusinessException("密码错误");
        }

        // 删除验证码
        redisTemplate.delete(ConstantUtil.ADMIN_CODE +uuid);
        log.info(admin.getUsername() + " 登录后台管理 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        Map<String, Object> claim = new HashMap<>();
        claim.put("id",admin.getId());
        claim.put("username",admin.getUsername());

        String token = JwtUtil.createToken(claim);

        // 把token和用户放入redis中,设置时效
        redisTemplate.opsForValue().set(ConstantUtil.ADMIN_TOKEN + token, JSON.toJSONString(admin),Duration.ofHours(1));

        // 将token写回浏览器
        Map<String,String> map = new HashMap<>();
        map.put("token",token);

        return ResponseEntity.ok(map);
    }

    /**
     * 管理基本信息
     * @param token 令牌
     * @return
     */
    /*public ResponseEntity findAdminInfo(String token) {
        //1.处理token 前端工程师会在token前添加"Bearer "
        if (StrUtil.isBlank(token)) {
            return ResponseEntity.status(401).body(null);
        }
        token = token.replace("Bearer ","");

        //2.通过token获取redis中的数据
        String json = redisTemplate.opsForValue().get(ConstantUtil.ADMIN_TOKEN + token);
        if (StrUtil.isBlank(json)) {
            return ResponseEntity.status(401).body(null);
        }

        //3.若能获取到,转成admin返回
        Admin admin = null;
        try {
            admin = JSON.parseObject(json, Admin.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body(null);
        }

        //4.token续期
        redisTemplate.opsForValue().set(ConstantUtil.ADMIN_TOKEN + token,json,Duration.ofHours(1));

        return ResponseEntity.ok(admin);
    }*/

    /**
     * 用户登出
     * @param token 令牌
     * @return
     */
    public ResponseEntity logout(String token) {
        token = token.replace("Bearer ", "");

        redisTemplate.delete(ConstantUtil.ADMIN_TOKEN + token);
        log.info(AdminHolder.get().getUsername() + " 退出后台管理 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return ResponseEntity.ok(null);
    }
}
