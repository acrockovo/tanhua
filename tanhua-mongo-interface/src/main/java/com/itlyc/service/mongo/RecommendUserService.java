package com.itlyc.service.mongo;

import com.itlyc.domain.mongo.RecommendUser;
import com.itlyc.domain.vo.PageBeanVo;

public interface RecommendUserService {
    // 查找今日佳人
    RecommendUser findTodayBest(Long userId);
    // 查找推荐好友列表
    PageBeanVo findRecommendUserByPage(Integer pageNum, Integer pageSize, Long userId);
}
