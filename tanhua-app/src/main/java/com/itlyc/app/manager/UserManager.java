package com.itlyc.app.manager;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.itlyc.autoconfig.oss.OssTemplate;
import com.itlyc.domain.db.Log;
import com.itlyc.domain.db.User;
import com.itlyc.domain.db.UserInfo;
import com.itlyc.domain.vo.ErrorResult;
import com.itlyc.domain.vo.UserInfoVo;
import com.itlyc.service.db.UserInfoService;
import com.itlyc.service.db.UserService;
import com.itlyc.util.ConstantUtil;
import com.itlyc.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户模块 manager controller具体逻辑
 * @author lyc
 * @date 2022-11-18
 */
@Service
public class UserManager {

    @Reference
    private UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Reference
    private UserInfoService userInfoService;
    @Autowired
    private OssTemplate ossTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return json
     */
    public ResponseEntity findByPhone(String phone) {

        User user = userService.findUserByPhone(phone);

        return ResponseEntity.ok(user);
    }

    /**
     * 保存用户
     * @param user 用户对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity saveUser(User user) {

        return ResponseEntity.ok(userService.save(user));
    }

    /**
     * 发送验证码
     * @param phone 手机号
     * @return
     */
    public ResponseEntity sendCode(String phone) {

        String code = "123456"; // 验证码

        redisTemplate.opsForValue().set(ConstantUtil.SMS_CODE + phone, code, Duration.ofMinutes(5));

        return ResponseEntity.ok(null);
    }

    /**
     * 注册 登录
     * @param phone 手机号
     * @param verificationCode 验证码
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity regAndLogin(String phone, String verificationCode) {

        String redisCode = redisTemplate.opsForValue().get(ConstantUtil.SMS_CODE + phone);

        // 比较redis中的验证码和用户提交的验证码
        if(!StringUtils.equals(redisCode,verificationCode)){
            return ResponseEntity.status(500).body(ErrorResult.loginError());
        }

        // 删除验证码
        redisTemplate.delete(ConstantUtil.SMS_CODE +phone);

        User user = userService.findUserByPhone(phone);

        // 是否为新用户
        boolean isNew;
        String type = "0101"; // 登录
        if(user == null){
            isNew = true;
            user = new User();
            user.setPhone(phone);
            user.setPassword(ConstantUtil.INIT_PASSWORD);

            Long id = userService.save(user);

            user.setId(id);
            type = "0102"; // 注册
        }else{

            isNew = false;
        }

        // 向rocketmq中发送消息
        Log log = new Log();
        log.setUserId(user.getId());
        log.setType(type);
        log.setLogTime(DateUtil.formatDate(new Date()));
        log.setPlace("北京顺义"); // 课下去查询user_location
        log.setEquipment("华为mate40Pro");

        rocketMQTemplate.convertAndSend("tanhua-log",log);

        // 初始化token值
        Map<String, Object> claims = new HashMap<>();
        claims.put("id",user.getId());
        claims.put("phone",user.getPhone());

        String token = JwtUtil.createToken(claims);

        // 设置token的value值 并为token续期
        String json = JSON.toJSONString(user);
        redisTemplate.opsForValue().set("token:" + token,json,Duration.ofDays(7));

        // 封装返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token",token);
        resultMap.put("isNew",isNew);
        // getAndSet先把以前的值查出来，再进行覆盖
        String date = redisTemplate.opsForValue().getAndSet(ConstantUtil.LAST_ACCESS_TIME + user.getId(),System.currentTimeMillis() + "");
        redisTemplate.opsForValue().set(ConstantUtil.LAST_SECOND_ACCESS_TIME + user.getId(),date);

        return ResponseEntity.ok(resultMap);

    }

    /**
     * 用户信息添加
     * @param userInfo 用户详情
     * @param token 令牌
     * @return
     */
    public ResponseEntity loginReginfo(UserInfo userInfo, String token) {

        // 判断用户是否提交了token
        if(StringUtils.isBlank(token)){
            return ResponseEntity.status(401).body(null);
        }

        String userJson = redisTemplate.opsForValue().get(ConstantUtil.USER_TOKEN + token);

        // 判断redis中通过token是否可以找到
        if(StringUtils.isBlank(userJson)){
            return ResponseEntity.status(401).body(null);
        }

        User user = JSON.parseObject(userJson, User.class);

        // token续期
        redisTemplate.opsForValue().set(ConstantUtil.USER_TOKEN + token,userJson,Duration.ofDays(5));

        // 保证user和userinfo中的id是相互对应的
        userInfo.setId(user.getId());

        userInfoService.save(userInfo);

        return ResponseEntity.ok(null);
    }

    /**
     * 上传用户头像
     * @param headImg 用户头像
     * @param token 令牌
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity saveUserInfoHead(MultipartFile headImg, String token) throws Exception{

        if(StringUtils.isBlank(token)){
            return ResponseEntity.status(401).body(null);
        }

        String userJson = redisTemplate.opsForValue().get(ConstantUtil.USER_TOKEN + token);

        if (StringUtils.isBlank(userJson)) {
            return ResponseEntity.status(401).body(null);
        }

        User user = JSON.parseObject(userJson, User.class);

        redisTemplate.opsForValue().set(ConstantUtil.USER_TOKEN + token,userJson,Duration.ofDays(5));

        String imgUrl = ossTemplate.upload(headImg.getOriginalFilename(), headImg.getInputStream());

        UserInfo userInfo = new UserInfo();
        userInfo.setAvatar(imgUrl);
        userInfo.setCoverPic(imgUrl);
        userInfo.setId(user.getId());

        userInfoService.update(userInfo);

        return ResponseEntity.ok(null);
    }

    /**
     * 根据id查询用户信息
     * @param id 用户id
     * @return
     */
    public ResponseEntity findUserInfoById(Long id) {
        UserInfo userInfo = userInfoService.findById(id);
        UserInfoVo vo = new UserInfoVo();

        BeanUtils.copyProperties(userInfo,vo);

        vo.setAge(userInfo.getAge().toString());

        return ResponseEntity.ok(vo);
    }

    /**
     * 更新用户信息
     * @param userInfo 用户信息对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity updateUserInfo(UserInfo userInfo) {

        userInfoService.update(userInfo);
        return ResponseEntity.ok(null);
    }
}

