package com.itlyc.service.mongo.impl;

import com.itlyc.domain.mongo.RecommendVideo;
import com.itlyc.domain.mongo.Video;
import com.itlyc.domain.vo.PageBeanVo;
import com.itlyc.service.mongo.VideoService;
import com.itlyc.util.ConstantUtil;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IdService idService;
    /**
     * 视频推荐列表查询
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param userId 当前登录用户id
     * @return
     */
    @Override
    public PageBeanVo findRecommendVideoByPage(Integer pageNum, Integer pageSize, Long userId) {

        Query query = new Query(
                Criteria.where("userId").is(userId)
        ).skip((pageNum - 1) * pageSize).limit(pageSize)
                .with(Sort.by(Sort.Order.desc("score")));

        List<RecommendVideo> recommendVideoList = mongoTemplate.find(query, RecommendVideo.class);

        List<Video> videoList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(recommendVideoList)){
            for (RecommendVideo recommendVideo : recommendVideoList) {
                Video video = mongoTemplate.findById(recommendVideo.getVideoId(), Video.class);
                videoList.add(video);
            }
        }
        long count = mongoTemplate.count(query, RecommendVideo.class);
        return new PageBeanVo(pageNum,pageSize,count,videoList);
    }

    /**
     * 发布视频
     * @param video 视频对象
     */
    @Override
    public void save(Video video) {
        video.setVid(idService.getNextId(ConstantUtil.VIDEO_ID));

        mongoTemplate.save(video);

        RecommendVideo recommendVideo = new RecommendVideo();
        recommendVideo.setVid(video.getVid());
        recommendVideo.setUserId(video.getUserId());
        recommendVideo.setVideoId(video.getId());
        recommendVideo.setScore(90D);
        recommendVideo.setDate(System.currentTimeMillis());

        mongoTemplate.save(recommendVideo);
    }

    /**
     * 根据用户id查找视频
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param uid 用户id
     * @return
     */
    @Override
    public PageBeanVo findUserVideoByPage(int pageNum, int pageSize, Long uid) {

        Query query = new Query(
                Criteria.where("userId").is(uid)
        ).skip((pageNum - 1) * pageSize).limit(pageSize)
                .with(Sort.by(Sort.Order.desc("score")));

        List<Video> videoList = mongoTemplate.find(query, Video.class);
        long count = mongoTemplate.count(query, Video.class);
        return new PageBeanVo(pageNum,pageSize,count,videoList);
    }
}
