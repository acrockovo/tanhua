package com.itlyc.service.mongo.impl;

import com.itlyc.domain.mongo.Friend;
import com.itlyc.domain.mongo.FriendMovement;
import com.itlyc.domain.mongo.Movement;
import com.itlyc.domain.mongo.MyMovement;
import com.itlyc.domain.vo.PageBeanVo;
import com.itlyc.service.mongo.MovementService;
import com.itlyc.util.ConstantUtil;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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

    /**
     * 查找个人动态
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param userId 用户id
     * @return
     */
    @Override
    public PageBeanVo findMyMovementByPage(Integer pageNum, Integer pageSize, Long userId) {

        // 设置分页对象并按照时间排序
        Query query = new Query()
                .skip((pageNum - 1) * pageSize).limit(pageSize)
                .with(Sort.by(Sort.Order.desc("Created")));

        // 查询到我的动态列表
        List<MyMovement> myMovements = mongoTemplate.find(query, MyMovement.class, ConstantUtil.MOVEMENT_MINE + userId);

        // 动态未审核通过的数量
        int failStateCount = 0;

        List<Movement> movements = new ArrayList<>();

        if(!CollectionUtils.isEmpty(myMovements)){
            for (MyMovement myMovement : myMovements) {
                ObjectId movementId = myMovement.getPublishId();
                Movement movement = mongoTemplate.findById(movementId, Movement.class);
                if(movement.getState() == 1){
                    movements.add(movement);
                }else {
                    failStateCount ++;
                }
            }
        }

        // 查询我的动态列表数量
        long count = mongoTemplate.count(query, MyMovement.class, ConstantUtil.MOVEMENT_MINE + userId);

        return new PageBeanVo(pageNum,pageSize,count - failStateCount,movements);
    }
}
