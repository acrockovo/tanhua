package com.itlyc.app.manager;

import com.itlyc.app.interceptor.UserHolder;
import com.itlyc.domain.db.Notification;
import com.itlyc.domain.db.Question;
import com.itlyc.domain.db.User;
import com.itlyc.domain.vo.PageBeanVo;
import com.itlyc.domain.vo.SettingVo;
import com.itlyc.service.db.BlackListService;
import com.itlyc.service.db.NotificationService;
import com.itlyc.service.db.QuestionService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Reference
    private BlackListService blackListService;
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

    /**
     * 保存或修改默认人问题
     * @param content 问题详情
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity setQuestion(String content) {

        Long userId = UserHolder.get().getId();

        Question question = questionService.findByUserId(userId);

        if(question == null){
            question = new Question();
            question.setUserId(userId);
            question.setStrangerQuestion(content);
            questionService.save(question);
        }else{ // 修改
            question.setStrangerQuestion(content);
            questionService.update(question);
        }

        return ResponseEntity.ok(null);
    }

    /**
     * 修改通知设置
     * @param notificationParam 通知对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity setNotification(Notification notificationParam) {

        Long userId = UserHolder.get().getId();

        Notification notification = notificationService.findByUserId(userId);

        // 修改
        if(notification != null){
            notificationParam.setId(notification.getId());
            notificationService.update(notificationParam);
        }else {
            notificationParam.setUserId(userId);
            notificationService.save(notificationParam);
        }

        return ResponseEntity.ok(null);
    }

    /**
     * 查询用户黑名单
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    public ResponseEntity findBlackListByPage(Integer pageNum, Integer pageSize) {


        Long userId = UserHolder.get().getId();

        PageBeanVo pageBeanVo = blackListService.findBlackListByPage(userId,pageNum,pageSize);

        return ResponseEntity.ok(pageBeanVo);
    }

    /**
     * 删除用户黑名单
     * @param blackId 黑名单用户id
     * @return
     */
    public void deleteBlackList(Long blackId) {

        Long userId = UserHolder.get().getId();

        blackListService.deleteBlackList(userId,blackId);
    }
}
