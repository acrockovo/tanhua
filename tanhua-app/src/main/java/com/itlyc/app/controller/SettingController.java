package com.itlyc.app.controller;

import com.itlyc.app.manager.SettingManager;
import com.itlyc.domain.db.Notification;
import org.apache.coyote.OutputBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 通用设置Controller
 * @author lyc
 * @date 2023-01-17
 */
@RestController
public class SettingController {

    @Autowired
    private SettingManager settingManager;

    /**
     * 查询用户通用设置选项
     * @return
     */
    @GetMapping("/users/settings")
    public ResponseEntity findSetting(){
        return settingManager.findSetting();
    }

    /**
     * 保存或修改默认人问题
     * @param map 问题详情
     * @return
     */
    @PostMapping("/users/questions")
    public ResponseEntity setQuestion(@RequestBody Map<String,String> map){

        String content = map.get("content");

        return settingManager.setQuestion(content);
    }

    /**
     * 修改通知设置
     * @param notification 通知对象
     * @return
     */
    @PostMapping("/users/notifications/setting")
    public ResponseEntity setNotification(@RequestBody Notification notification){
        return settingManager.setNotification(notification);
    }

    /**
     * 查询用户黑名单
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    @GetMapping("/users/blacklist")
    public ResponseEntity findBlackListByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "8") Integer pageSize

    ){
        return settingManager.findBlackListByPage(pageNum,pageSize);
    }

    /**
     * 删除用户黑名单
     * @param blackId 黑名单用户id
     * @return
     */
    @DeleteMapping("/users/blacklist/{uid}")
    public ResponseEntity deleteBlackList(@PathVariable("uid") Long blackId){

        settingManager.deleteBlackList(blackId);

        return ResponseEntity.ok(null);
    }
}
