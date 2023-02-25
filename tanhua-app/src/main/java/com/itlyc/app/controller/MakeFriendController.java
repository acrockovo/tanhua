package com.itlyc.app.controller;

import com.itlyc.app.manager.MakeFriendManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
