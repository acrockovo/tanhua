package com.itlyc.app.controller;

import com.itlyc.app.manager.MovementManager;
import com.itlyc.domain.mongo.Movement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 动态详情控制器
 * @author lyc
 * @date 2023-02-18
 */
@RestController
public class MovementController {

    @Autowired
    private MovementManager movementManager;

    /**
     * 上传动态
     * @param movement 动态详情实体
     * @param imageContent 图片列表
     * @return
     */
    @PostMapping("/movements")
    public ResponseEntity save(Movement movement, MultipartFile[] imageContent) throws IOException {

        return movementManager.save(movement,imageContent);
    }
}
