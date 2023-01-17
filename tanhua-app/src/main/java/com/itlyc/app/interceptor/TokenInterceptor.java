package com.itlyc.app.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.itlyc.app.manager.UserManager;
import com.itlyc.domain.db.User;
import com.itlyc.util.ConstantUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserManager userManager;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("Authorization");

        if(StringUtils.isBlank(token)){
            response.setStatus(401);
            return false;
        }

        //2.从redis中查询指定token,为空返回401
        String userJson = redisTemplate.opsForValue().get(ConstantUtil.USER_TOKEN + token);
        if (StrUtil.isBlank(userJson)) {
            response.sendError(401);
            return false;
        }

        //3.获取user
        User user = JSON.parseObject(userJson, User.class);

        //4.给token续期
        redisTemplate.opsForValue().set(ConstantUtil.USER_TOKEN+token,userJson, Duration.ofDays(5));


        UserHolder.set(user);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.remove();
    }
}
