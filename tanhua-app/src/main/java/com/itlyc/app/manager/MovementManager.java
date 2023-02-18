package com.itlyc.app.manager;

import cn.hutool.core.util.ArrayUtil;
import com.itlyc.app.interceptor.UserHolder;
import com.itlyc.autoconfig.oss.OssTemplate;
import com.itlyc.domain.mongo.Movement;
import com.itlyc.service.mongo.MovementService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MovementManager {

    @Reference
    private MovementService movementService;
    @Autowired
    private OssTemplate ossTemplate;
    /**
     * 上传动态
     * @param movement 动态详情实体
     * @param imageContent 图片列表
     * @return
     */
    public ResponseEntity save(Movement movement, MultipartFile[] imageContent) throws IOException {

        // 声明存储图片地址集合
        List<String> medias = new ArrayList<>();

        if(ArrayUtil.isNotEmpty(imageContent)){
            for (MultipartFile multipartFile : imageContent) {
                String url = ossTemplate.upload(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
                medias.add(url);
            }
        }

        Long userId = UserHolder.get().getId();

        movement.setUserId(userId);
        movement.setMedias(medias);
        movement.setState(1); // 默认审核通过
        movement.setCreated(System.currentTimeMillis());
        movement.setSeeType(1); // 默认公开

        movementService.save(movement);

        return ResponseEntity.ok(null);
    }
}
