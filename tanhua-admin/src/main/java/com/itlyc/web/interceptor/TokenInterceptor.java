package com.itlyc.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.itlyc.domain.db.Admin;
import com.itlyc.util.ConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;


@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("Authorization");

        if(StringUtils.isNotBlank(token)){
            token = token.replace("Bearer ", "");
        }

        String json = redisTemplate.opsForValue().get(ConstantUtil.ADMIN_TOKEN + token);

        if(StringUtils.isBlank(json)){
            response.sendError(401);
            return false;
        }

        Admin admin = null;

        try {
            admin = JSON.parseObject(json, Admin.class);
        } catch (Exception exception) {
            response.sendError(401);
            return false;
        }
        AdminHolder.set(admin);
        // token续期
        redisTemplate.opsForValue().set(ConstantUtil.ADMIN_CODE + token, json, Duration.ofHours(1));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AdminHolder.remove();
    }
}
