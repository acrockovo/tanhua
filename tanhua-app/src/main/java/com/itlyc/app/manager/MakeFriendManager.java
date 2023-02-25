package com.itlyc.app.manager;

import cn.hutool.core.util.RandomUtil;
import com.itlyc.app.interceptor.UserHolder;
import com.itlyc.domain.db.UserInfo;
import com.itlyc.domain.mongo.RecommendUser;
import com.itlyc.domain.vo.PageBeanVo;
import com.itlyc.domain.vo.RecommendUserVo;
import com.itlyc.service.db.UserInfoService;
import com.itlyc.service.mongo.RecommendUserService;
import org.apache.dubbo.config.annotation.Reference;
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

        List<RecommendUser> items = (List<RecommendUser>) pageBeanVo.getItems();
        // 新用户默认推荐
        if(CollectionUtils.isEmpty(items)){
            for (int i = 0; i < 10; i++) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(i + 2L);
                recommendUser.setScore(99D - i);
                items.add(recommendUser);
            }
        }

        List<RecommendUserVo> recommendUserVoList = new ArrayList<>();

        for (RecommendUser recommendUser : items) {
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

        return ResponseEntity.ok(recommendUserVo);
    }
}
