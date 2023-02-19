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
    PageBeanVo findMyMovementByPage(Integer page, Integer pageSize, Long userId);
}
