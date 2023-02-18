package com.itlyc.service.mongo.impl;

import com.itlyc.domain.mongo.Friend;
import com.itlyc.domain.mongo.FriendMovement;
import com.itlyc.domain.mongo.Movement;
import com.itlyc.domain.mongo.MyMovement;
import com.itlyc.service.mongo.MovementService;
import com.itlyc.util.ConstantUtil;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 动态详情实体类
 * @author lyc
 * @date 2023-02-18
 */
@Service
public class MovementServiceImpl implements MovementService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IdService idService;

    /**
     * 保存动态
     * @param movement 动态实体
     */
    @Override
    public void save(Movement movement) {

        // 获取动态的自增pid,为大数据服务
        Long pid = idService.getNextId(ConstantUtil.MOVEMENT_ID);
        movement.setPid(pid);

        // 保存到动态详情表中
        mongoTemplate.save(movement);

        ObjectId movementId = movement.getId(); // 获取动态id
        Long userId = movement.getUserId();//获取用户id

        // 保存到我的动态中
        MyMovement myMovement = new MyMovement();
        myMovement.setPublishId(movementId); // 发布id 每一条动态的id
        myMovement.setCreated(System.currentTimeMillis());

        mongoTemplate.save(myMovement, ConstantUtil.MOVEMENT_MINE + userId);


        // 查询我的好友列表
        Query query = new Query(
                Criteria.where("userId").is(userId)
        );

        List<Friend> friendList = mongoTemplate.find(query, Friend.class);

        if(!CollectionUtils.isEmpty(friendList)){
            for (Friend friend : friendList) {

                FriendMovement friendMovement = new FriendMovement();

                friendMovement.setPublishId(movementId);//动态id
                friendMovement.setUserId(userId);//发布人id
                friendMovement.setCreated(System.currentTimeMillis());//发布时间

                // 向我的好友列表中保存我发表的动态
                mongoTemplate.save(friendMovement,ConstantUtil.MOVEMENT_FRIEND + friend.getFriendId());
            }
        }

    }
}
