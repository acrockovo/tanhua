package com.itlyc.app.manager;

import com.itlyc.app.interceptor.UserHolder;
import com.itlyc.domain.db.Notification;
import com.itlyc.domain.db.Question;
import com.itlyc.domain.db.User;
import com.itlyc.domain.vo.SettingVo;
import com.itlyc.service.db.NotificationService;
import com.itlyc.service.db.QuestionService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author lyc
 * @date 2023-01-17
 * @decription 用户通用设置
 */
@Component
public class SettingManager {

    @Reference
    private NotificationService notificationService;
    @Reference
    private QuestionService questionService;
    /**
     * 查询用户通用设置选项
     * @return
     */
    public ResponseEntity findSetting() {

        User user = UserHolder.get();

        Question question = questionService.findByUserId(user.getId());

        Notification notification = notificationService.findByUserId(user.getId());

        SettingVo vo = new SettingVo();

        vo.setId(user.getId());
        vo.setPhone(user.getPhone());

        if (question != null) {
            vo.setStrangerQuestion(question.getStrangerQuestion());
        }

        if (notification != null) {
            vo.setGonggaoNotification(notification.getGonggaoNotification());
            vo.setLikeNotification(notification.getLikeNotification());
            vo.setPinglunNotification(notification.getPinglunNotification());
        }

        return ResponseEntity.ok(vo);
    }
}
