package com.itlyc.controller;

import com.itlyc.web.manager.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserManager userManager;
    /**
     * 查找用户列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    @GetMapping("/manage/users")
    public ResponseEntity findByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "8") Integer pageSize){
        return userManager.findByPage(pageNum,pageSize);
    }

    /**
     * 根据用户id查找用户详细信息
     * @param userId 用户id
     * @return
     */
    @GetMapping("/manage/users/{userId}")
    public ResponseEntity findUserById(@PathVariable Long userId){
        return userManager.findUserById(userId);
    }

    /**
     * 查看用户发表动态列表
     * @param uid 用户id
     * @param state 状态
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    @GetMapping("/manage/messages")
    public ResponseEntity findMovementByCondition4Page(
            Long uid,
            Integer state,
            @RequestParam(value = "page",defaultValue = "1")int pageNum,
            @RequestParam(value = "pagesize",defaultValue = "8")int pageSize){
        return userManager.findMovementByCondition4Page(uid,state,pageNum,pageSize);
    }

    /**
     * 动态详情
     * @param id 动态id
     * @return
     */
    @GetMapping("/manage/messages/{id}")
    public ResponseEntity findMovementById(@PathVariable String id){
        return userManager.findMovementById(id);
    }

    /**
     * 动态评论列表
     * @param messageID 评论id
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    @GetMapping("/manage/messages/comments")
    public ResponseEntity findMovementComemntByPage(
            String messageID,
            @RequestParam(value = "page",defaultValue = "1")int pageNum,
            @RequestParam(value = "pagesize",defaultValue = "8")int pageSize){
        return userManager.findMovementCommentByPage(pageNum,pageSize,messageID);
    }

    /**
     * 查找视频列表
     * @param uid 用户id
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    @GetMapping("manage/videos")
    public ResponseEntity findUserVideoByPage(
            Long uid,
            @RequestParam(value = "page",defaultValue = "1")int pageNum,
            @RequestParam(value = "pagesize",defaultValue = "8")int pageSize){
        return userManager.findUserVideoByPage(pageNum,pageSize,uid);
    }
}
