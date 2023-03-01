package com.itlyc.service.mongo.impl;

import com.itlyc.domain.mongo.UserLike;
import com.itlyc.service.mongo.UserLikeService;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Service
@Log
public class UserLikeServiceImpl implements UserLikeService {

    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 右滑喜欢
     * @param userLike 对象
     */
    @Override
    public void save(UserLike userLike) {

        Query query = new Query(
                Criteria.where("userId").is(userLike.getUserId())
                .and("likeUserId").is(userLike.getLikeUserId())
        );

        boolean exists = mongoTemplate.exists(query, UserLike.class);

        if(!exists){
            mongoTemplate.save(userLike);
            log.info("用户:" + userLike.getUserId() + "-喜欢:" + userLike.getLikeUserId() + "-成功");
        }
    }
}
