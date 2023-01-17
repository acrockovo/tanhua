package com.itlyc.app.controller;

import com.itlyc.app.interceptor.UserHolder;
import com.itlyc.app.manager.UserManager;
import com.itlyc.domain.db.User;
import com.itlyc.domain.db.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 用户模块 controller
 * @author lyc
 * @date 2022-11-18
 */
@RestController
public class UserController {

    @Autowired
    private UserManager userManager;

    @GetMapping("/user/findByPhone")
    public ResponseEntity findByPhone(@RequestParam("phone") String phone){
       return userManager.findByPhone(phone);
    }

    /**
     * 保存用户
     * @param user 用户对象
     * @return
     */
    @PostMapping("/user/saveUser")
    public ResponseEntity saveUser(@RequestBody User user){

        return userManager.saveUser(user);
    }

    /**
     * 用户登录 发送验证码
     * @param map 手机号
     * @return
     */
    @PostMapping("/user/login")
    public ResponseEntity sendCode(@RequestBody Map<String,String> map){

        String phone = map.get("phone");

        return userManager.sendCode(phone);
    }

    /**
     * 用户登录 注册
     * @param map 请求体对象
     * @return
     */
    @PostMapping("/user/loginVerification")
    public ResponseEntity regAndLogin(@RequestBody Map<String,String> map){

        String phone = map.get("phone");
        String verificationCode = map.get("verificationCode");

        return userManager.regAndLogin(phone,verificationCode);

    }

    /**
     * 用户信息添加
     * @param userInfo 用户详情
     * @param token 令牌
     * @return
     */
    @PostMapping("/user/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo,@RequestHeader("Authorization") String token){

        return userManager.loginReginfo(userInfo,token);
    }

    /**
     * 上传用户头像
     * @param headPhoto 用户头像
     * @param token 令牌
     * @return
     */
    @PostMapping({"/user/loginReginfo/head","/users/header"})
    public ResponseEntity saveUserInfoHead(MultipartFile headPhoto,@RequestHeader("Authorization") String token) throws Exception{
        return userManager.saveUserInfoHead(headPhoto,token);
    }

    /**
     * 根据id查询用户信息
     * @param userID 用户id
     * @param huanxinID 环信id
     * @return
     */
    @GetMapping("/users")
    public ResponseEntity findUserInfoById(Long userID,Long huanxinID){
        if(userID != null){
            return userManager.findUserInfoById(userID);
        }

        if(huanxinID != null){
           return userManager.findUserInfoById(huanxinID);
        }

        User user = UserHolder.get();

        return userManager.findUserInfoById(user.getId());
    }

    /**
     * 更新用户信息
     * @param userInfo 用户信息对象
     * @return
     */
    @PutMapping("/users")
    public ResponseEntity updateUserInfo(@RequestBody UserInfo userInfo) {

        return userManager.updateUserInfo(userInfo);
    }
}
