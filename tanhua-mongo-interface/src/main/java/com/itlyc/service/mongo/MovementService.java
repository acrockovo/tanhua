package com.itlyc.service.mongo;

import com.itlyc.domain.mongo.Movement;
import com.itlyc.domain.vo.PageBeanVo;

/**
 * 动态详情服务
 * @author lyc
 * @date 2023-02-18
 */
public interface MovementService {
    // 保存动态详情
    void save(Movement movement);

    // 查询个人动态
    PageBeanVo findMyMovementByPage(int page, int pageSize, Long userId);

    // 查询好友动态列表
    PageBeanVo getFriendMovements(int page, int pageSize, Long userId);

    // 查询推荐动态
    PageBeanVo findRecommendMovementByPage(int pageNum, int pageSize, Long userId);
}
