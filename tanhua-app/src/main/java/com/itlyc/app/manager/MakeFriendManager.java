package com.itlyc.app.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.itlyc.app.interceptor.UserHolder;
import com.itlyc.domain.db.Question;
import com.itlyc.domain.db.UserInfo;
import com.itlyc.domain.mongo.RecommendUser;
import com.itlyc.domain.mongo.Visitor;
import com.itlyc.domain.vo.PageBeanVo;
import com.itlyc.domain.vo.RecommendUserVo;
import com.itlyc.domain.vo.VisitorVo;
import com.itlyc.service.db.QuestionService;
import com.itlyc.service.db.UserInfoService;
import com.itlyc.service.mongo.RecommendUserService;
import com.itlyc.service.mongo.VisitorService;
import com.itlyc.util.ConstantUtil;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class MakeFriendManager {

    @Reference
    private RecommendUserService recommendUserService;
    @Reference
    private UserInfoService userInfoService;
    @Reference
    private QuestionService questionService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Reference
    private VisitorService visitorService;
    /**
     * 查找今日佳人
     * @return
     */
    public ResponseEntity findTodayBest() {

        Long userId = UserHolder.get().getId();
        RecommendUser recommendUser = recommendUserService.findTodayBest(userId);
        // 如果是新用户登录，new一个新的今日佳人
        if(recommendUser == null){
            recommendUser = new RecommendUser();
            recommendUser.setUserId(1L);
            recommendUser.setScore(99D);
        }
        UserInfo userInfo = userInfoService.findById(recommendUser.getUserId());

        RecommendUserVo recommendUserVo = new RecommendUserVo();
        recommendUserVo.setUserInfo(userInfo);
        recommendUserVo.setFateValue(recommendUser.getScore().longValue());

        return ResponseEntity.ok(recommendUserVo);
    }

    /**
     * 查找推荐好友信息列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    public ResponseEntity findRecommendUserByPage(Integer pageNum, Integer pageSize) {

        Long userId = UserHolder.get().getId();

        PageBeanVo pageBeanVo = recommendUserService.findRecommendUserByPage(pageNum, pageSize, userId);

        List<RecommendUser> recommendUserList = (List<RecommendUser>) pageBeanVo.getItems();
        // 新用户默认推荐
        if(CollectionUtils.isEmpty(recommendUserList)){
            for (int i = 0; i < 10; i++) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(i + 2L);
                recommendUser.setScore(99D - i);
                recommendUser.setToUserId(userId);
                recommendUserList.add(recommendUser);
            }
            // 将新增的推荐人保存到mongo中
            recommendUserService.saveRecommendUser(recommendUserList);
        }

        List<RecommendUserVo> recommendUserVoList = new ArrayList<>();

        for (RecommendUser recommendUser : recommendUserList) {
            UserInfo userInfo = userInfoService.findById(recommendUser.getUserId());
            RecommendUserVo recommendUserVo = new RecommendUserVo();
            recommendUserVo.setUserInfo(userInfo);
            recommendUserVo.setFateValue(recommendUser.getScore().longValue());

            recommendUserVoList.add(recommendUserVo);
        }
        pageBeanVo.setItems(recommendUserVoList);
        return ResponseEntity.ok(pageBeanVo);
    }

    /**
     * 查找推荐好友详细信息
     * @param recommendUserId 推荐人id
     * @return
     */
    public ResponseEntity findRecommendUserPersonal(Long recommendUserId) {
        Long userId = UserHolder.get().getId();
        RecommendUser recommendUser = recommendUserService.findPersonal(recommendUserId, userId);
        // 如果是新用户则缘分值是无法查出的需要新创建一个
        if(recommendUser == null){
            recommendUser = new RecommendUser();
            recommendUser.setScore(RandomUtil.randomDouble(70,90));
        }

        UserInfo userInfo = userInfoService.findById(recommendUser.getUserId());

        RecommendUserVo recommendUserVo = new RecommendUserVo();
        recommendUserVo.setUserInfo(userInfo);
        recommendUserVo.setFateValue(recommendUser.getScore().longValue());
        // 往mongo中保存谁看了谁的主页
        visitorService.save(recommendUser.getUserId(),userId);

        return ResponseEntity.ok(recommendUserVo);
    }

    /**
     * 查找陌生人问题
     * @param userId 用户id
     * @return
     */
    public ResponseEntity findStrangerQuestions(Long userId) {
        Question question = questionService.findByUserId(userId);
        if (question == null) {
            return ResponseEntity.ok("你是喜欢渣男，还是暗恋靓女？？");
        }
        return ResponseEntity.ok(question.getStrangerQuestion());
    }

    /**
     * 查找最近访客
     * @return
     */
    public ResponseEntity findVisitorsSinceLastAccessTime() {
        Long userId = UserHolder.get().getId();
        // 上次该用户最后登录时间
        String lastAccessTimeStr = redisTemplate.opsForValue().get(ConstantUtil.LAST_SECOND_ACCESS_TIME + userId);
        // 将时间戳转换为long类
        Long lastAccessTime = NumberUtil.isLong(lastAccessTimeStr) ? NumberUtil.parseLong(lastAccessTimeStr) : 0;

        List<Visitor> visitors = visitorService.findVisitorsSinceLastAccessTime(userId, lastAccessTime);

        List<VisitorVo> visitorVoList = new ArrayList<>();

        //4.2 遍历访客集合
        if (CollUtil.isNotEmpty(visitors)) {
            for (Visitor visitor : visitors) {
                //4.3 获取userInfo
                UserInfo userInfo = userInfoService.findById(visitor.getVisitorUserId());
                if(visitor.getUserId() == visitor.getVisitorUserId()){
                    continue;
                }

                //4.4 封装VisitorInfo
                VisitorVo visitorVo = new VisitorVo();

                visitorVo.setUserInfo(userInfo);
                visitorVo.setFateValue(visitor.getScore().longValue());

                //4.5 放入集合中
                visitorVoList.add(visitorVo);
            }
        }

        return ResponseEntity.ok(visitorVoList);
    }
}
