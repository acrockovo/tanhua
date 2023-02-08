package com.itlyc.service.db;

import com.itlyc.domain.db.Notification;

/**
 * @author lyc
 * @date 2023-01-17
 * @description 通用设置接口
 */
public interface NotificationService {

    // 根据id查找当前用户的配置
    Notification findByUserId(Long userId);

    void update(Notification notificationParam);

    void save(Notification notificationParam);
}
