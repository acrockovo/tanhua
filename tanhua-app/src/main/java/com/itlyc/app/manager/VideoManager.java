package com.itlyc.app.manager;

import cn.hutool.core.io.FileUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.itlyc.app.interceptor.UserHolder;
import com.itlyc.autoconfig.oss.OssTemplate;
import com.itlyc.domain.db.UserInfo;
import com.itlyc.domain.mongo.Video;
import com.itlyc.domain.vo.PageBeanVo;
import com.itlyc.domain.vo.VideoVo;
import com.itlyc.service.db.UserInfoService;
import com.itlyc.service.mongo.VideoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class VideoManager {

    @Reference
    private UserInfoService userInfoService;
    @Reference
    private VideoService videoService;
    @Autowired
    private OssTemplate ossTemplate;
    @Autowired
    private FastFileStorageClient client;
    @Autowired
    private FdfsWebServer server;

    /**
     * 视频推荐列表查询
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    public ResponseEntity findRecommendVideoByPage(Integer pageNum, Integer pageSize) {

        Long userId = UserHolder.get().getId();

        PageBeanVo  pageBeanVo = videoService.findRecommendVideoByPage(pageNum,pageSize,userId);

        List<Video> items = (List<Video>) pageBeanVo.getItems();

        List<VideoVo> videoVoList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(items)){
            for (Video video : items) {
                UserInfo userInfo = userInfoService.findById(video.getUserId());
                VideoVo videoVo = new VideoVo();
                BeanUtils.copyProperties(userInfo,videoVo);
                BeanUtils.copyProperties(video,videoVo);
                videoVo.setCover(video.getPicUrl());//视频封面
                videoVo.setSignature(video.getText());//视频文字
                videoVo.setHasFocus(0);//暂时没有关注
                videoVo.setHasLiked(0);//暂时没有点赞

                videoVoList.add(videoVo);
            }
        }
        pageBeanVo.setItems(videoVoList);
        return ResponseEntity.ok(pageBeanVo);
    }

    //发布视频
    @CacheEvict(value = "tanhua-video",key = "#userId+'_*'")
    @Transactional(rollbackFor = Exception.class)
    public void save(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        Long userId = UserHolder.get().getId();
        //1.上传封面图片到oss上
        String picUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());

        //2.上传视频到fastDfs上
        StorePath storePath = client.uploadFile(videoFile.getInputStream(),
                videoFile.getSize(), FileUtil.extName(videoFile.getOriginalFilename()), null);
        String videoUrl = server.getWebServerUrl()+storePath.getFullPath();

        //3.封装视频
        Video video = new Video();
        video.setCreated(System.currentTimeMillis());
        video.setUserId(userId);
        video.setText("向幸福出发");//目前写死
        video.setPicUrl(picUrl);
        video.setVideoUrl(videoUrl);

        //4.调用service完成保存
        videoService.save(video);
    }
}
