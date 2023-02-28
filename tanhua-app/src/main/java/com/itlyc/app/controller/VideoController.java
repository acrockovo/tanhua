package com.itlyc.app.controller;

import com.itlyc.app.manager.VideoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 小视频接口
 */
@RestController
public class VideoController {

    @Autowired
    private VideoManager videoManager;

    /**
     * 推荐视频列表查询
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    @GetMapping("/smallVideos")
    public ResponseEntity findRecommendVideoByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "8") Integer pageSize){
        return videoManager.findRecommendVideoByPage(pageNum, pageSize);
    }

    /**
     * 发布视频
     * @param videoThumbnail
     * @param videoFile
     * @return
     * @throws IOException
     */
    @PostMapping("/smallVideos")
    public ResponseEntity save(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        videoManager.save(videoThumbnail,videoFile);
        return ResponseEntity.ok(null);
    }
}
