package com.itlyc.service.mongo;

import com.itlyc.domain.mongo.Comment;

/**
 * 动态操作动作逻辑
 */
public interface CommentService {

    // 动态点赞
    int saveMovementType(Comment comment);

    // 取消动态点赞
    int deleteMovementType(String movementId, Integer commentType, Long userId);

}
