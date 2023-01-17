package com.itlyc.app.controller;

import com.itlyc.app.manager.SettingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
