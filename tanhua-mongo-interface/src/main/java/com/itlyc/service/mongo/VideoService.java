package com.itlyc.service.mongo;

import com.itlyc.domain.mongo.Video;
import com.itlyc.domain.vo.PageBeanVo;

public interface VideoService {
    // 视频推荐列表查询
    PageBeanVo findRecommendVideoByPage(Integer pageNum, Integer pageSize, Long userId);
    // 发布视频
    void save(Video video);
    // 根据用户id查找视频
    PageBeanVo findUserVideoByPage(int pageNum, int pageSize, Long uid);
}
