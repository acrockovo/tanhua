package com.itlyc.service.mongo.impl;

import com.itlyc.domain.mongo.Comment;
import com.itlyc.domain.mongo.Movement;
import com.itlyc.service.mongo.CommentService;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 动态点赞
     * @param comment 点赞对象
     * @return
     */
    @Override
    public int saveMovementType(Comment comment) {

        ObjectId movementId = comment.getPublishId(); //动态id
        Movement movement = mongoTemplate.findById(movementId, Movement.class);
        comment.setPublishUserId(movement.getUserId()); // 设置这条动态是谁发布的

        mongoTemplate.save(comment);

        Integer commentType = comment.getCommentType();
        if (commentType == 1){
            //动态点赞
            movement.setLikeCount(movement.getLikeCount()+1);
        }else if (commentType == 2){
            //动态评论
            movement.setCommentCount(movement.getCommentCount()+1);
        }else if (commentType == 3){
            //动态喜欢
            movement.setLoveCount(movement.getLoveCount() + 1);
        }

        mongoTemplate.save(movement);

        //3.返回刚才操作后的指定操作数量
        if (commentType == 1){
            //动态点赞
            return movement.getLikeCount();
        }else if (commentType == 2){
            //动态评论
            return movement.getCommentCount();
        }else if (commentType == 3){
            //动态喜欢
            return movement.getLoveCount();
        }
        return 0;
    }

    /**
     *
     * @param movementId 动态id
     * @param commentType 操作类型
     * @param userId 当前登录用户id
     * @return
     */
    @Override
    public int deleteMovementType(String movementId, Integer commentType, Long userId) {

        // 定义查找条件
        Query query = new Query(
                Criteria.where("userId").is(userId)
                .and("commentType").is(commentType)
                .and("publishId").is(new ObjectId(movementId))
        );

        // 删除评论表中的操作记录
        mongoTemplate.remove(query,Comment.class);

        // 修改动态表中的点赞数量

        Movement movement = mongoTemplate.findById(movementId, Movement.class);

        if (commentType == 1){
            //动态点赞
            movement.setLikeCount(movement.getLikeCount()-1);
        }else if (commentType == 2){
            //动态评论
            movement.setCommentCount(movement.getCommentCount()-1);
        }else if (commentType == 3){
            //动态喜欢
            movement.setLoveCount(movement.getLoveCount()-1);
        }

        mongoTemplate.save(movement);

        //3.返回刚才操作后的指定操作数量
        if (commentType == 1){
            //动态点赞
            return movement.getLikeCount();
        }else if (commentType == 2){
            //动态评论
            return movement.getCommentCount();
        }else if (commentType == 3){
            //动态喜欢
            return movement.getLoveCount();
        }
        return 0;
    }
}
