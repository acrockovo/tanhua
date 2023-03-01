package com.itlyc.app.controller;

import com.itlyc.app.manager.MakeFriendManager;
import com.itlyc.domain.vo.PageBeanVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MakeFriendController {

    @Autowired
    private MakeFriendManager makeFriendManager;

    /**
     * 查找今日佳人
     * @return
     */
    @GetMapping("/tanhua/todayBest")
    public ResponseEntity findTodayBest(){
        return makeFriendManager.findTodayBest();
    }

    /**
     * 查找推荐好友列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    @GetMapping("/tanhua/recommendation")
    public ResponseEntity findRecommendUserByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize){
        return makeFriendManager.findRecommendUserByPage(pageNum, pageSize);
    }

    /**
     * 查找单条推荐人信息
     * @param recommendUserId 推荐人id
     * @return
     */
    @GetMapping("/tanhua/{recommendUserId}/personalInfo")
    public ResponseEntity findRecommendUserPersonal(@PathVariable Long recommendUserId){
        return makeFriendManager.findRecommendUserPersonal(recommendUserId);
    }

    /**
     * 查找陌生人问题
     * @param userId 用户id
     * @return
     */
    @GetMapping("/tanhua/strangerQuestions")
    public ResponseEntity findStrangerQuestions(Long userId){
        return makeFriendManager.findStrangerQuestions(userId);
    }

    /**
     * 查找最近访客
     * @return
     */
    @GetMapping("/movements/visitors")
    public ResponseEntity findVisitorsSinceLastAccessTime(){
        return makeFriendManager.findVisitorsSinceLastAccessTime();
    }

    /**
     * 上报自己的位置
     * @param map 位置对象信息
     * @return
     */
    @PostMapping("/baidu/location")
    public ResponseEntity saveUserLocation(@RequestBody Map<String,Object> map){
        double latitude = (double) map.get("latitude");
        double longitude = (double) map.get("longitude");
        String addStr = map.get("addrStr").toString();

        System.out.println("上报自己的位置： " + map);
        return makeFriendManager.saveUserLocation(longitude,latitude,addStr);
    }

    /**
     * 查找附近的人
     * @param gender 性别
     * @param distance 距离
     * @return
     */
    @GetMapping("/tanhua/search")
    public ResponseEntity searchNear(String gender, int distance){
        return makeFriendManager.searchNear(gender, distance);
    }

    /**
     * 探花卡片查询
     * @return
     */
    @GetMapping("/tanhua/cards")
    public ResponseEntity tanhuaCards(){
        ResponseEntity responseEntity = makeFriendManager.findRecommendUserByPage(1, 20);
        PageBeanVo pageBeanVo = (PageBeanVo) responseEntity.getBody();
        return ResponseEntity.ok(pageBeanVo.getItems());
    }
}
