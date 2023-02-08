package com.itlyc.service.db.impl;

import com.itlyc.domain.db.Notification;
import com.itlyc.mapper.NotificationMapper;
import com.itlyc.service.db.NotificationService;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;

/**
 * @author lyc
 * @date 2023-01-17
 * @decription 通用设置服务
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Resource
    private NotificationMapper notificationMapper;

    @Override
    public Notification findByUserId(Long userId) {
        return notificationMapper.findByUserId(userId);
    }

    @Override
    public void update(Notification notificationParam) {
        notificationMapper.update(notificationParam);
    }

    @Override
    public void save(Notification notificationParam) {
        notificationMapper.save(notificationParam);
    }
}
