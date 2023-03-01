package com.itlyc.app.controller;

import com.itlyc.app.manager.UserLikeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserLikeController {

    @Autowired
    private UserLikeManager userLikeManager;

    /**
     * 右滑喜欢
     * @param likeUserId 喜欢用户id
     * @return
     */
    @GetMapping("/tanhua/{likeUserId}/love")
    public ResponseEntity tanhuaLove(@PathVariable Long likeUserId){

        return userLikeManager.tanhuaLove(likeUserId);
    }
}
