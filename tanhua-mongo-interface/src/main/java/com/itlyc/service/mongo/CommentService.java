package com.itlyc.service.mongo;

import com.itlyc.domain.mongo.Comment;
import com.itlyc.domain.vo.PageBeanVo;

/**
 * 动态操作动作逻辑
 */
public interface CommentService {
    // 动态点赞
    int saveMovementType(Comment comment);
    // 取消动态点赞
    int deleteMovementType(String movementId, Integer commentType, Long userId);
    // 查询动态评论列表
    PageBeanVo findMovementComment(Integer pageNum, Integer pageSize, String movementId);
}
