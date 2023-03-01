package com.itlyc.app.manager;

import com.itlyc.app.interceptor.UserHolder;
import com.itlyc.domain.mongo.UserLike;
import com.itlyc.service.mongo.UserLikeService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserLikeManager {

    @Reference
    private UserLikeService userLikeService;

    /**
     * 右滑喜欢
     * @param likeUserId 喜欢用户id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity tanhuaLove(Long likeUserId) {

        UserLike userLike = new UserLike();
        userLike.setUserId(UserHolder.get().getId());
        userLike.setLikeUserId(likeUserId);
        userLike.setCreated(System.currentTimeMillis());

        userLikeService.save(userLike);

        return ResponseEntity.ok(null);
    }
}
