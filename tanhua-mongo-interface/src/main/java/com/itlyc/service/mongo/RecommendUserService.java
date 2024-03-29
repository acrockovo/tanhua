package com.itlyc.service.mongo;

import com.itlyc.domain.mongo.RecommendUser;
import com.itlyc.domain.vo.PageBeanVo;

import java.util.List;

public interface RecommendUserService {
    // 查找今日佳人
    RecommendUser findTodayBest(Long userId);
    // 查找推荐好友列表
    PageBeanVo findRecommendUserByPage(Integer pageNum, Integer pageSize, Long userId);
    // 查找推荐人详细信息
    RecommendUser findPersonal(Long recommendUserId, Long userId);

    void saveRecommendUser(List<RecommendUser> recommendUserList);
}
