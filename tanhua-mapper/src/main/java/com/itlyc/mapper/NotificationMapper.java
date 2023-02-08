package com.itlyc.mapper;

import com.itlyc.domain.db.Notification;
import org.apache.ibatis.annotations.Param;

/**
 * @author lyc
 * @date 2023-01-17
 * @description 通用设置mapper
 */
public interface NotificationMapper {
    Notification findByUserId(@Param("id") Long userId);

    void update(Notification notificationParam);

    void save(Notification notificationParam);
}
