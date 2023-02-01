package com.itlyc.app.controller;

import com.itlyc.app.manager.SettingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity saveQuestion(@RequestBody Map<String,String> map){

        String content = map.get("content");

        return settingManager.saveQuestion(content);
    }
}
