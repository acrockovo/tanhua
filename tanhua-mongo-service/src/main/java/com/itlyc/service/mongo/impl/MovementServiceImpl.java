package com.itlyc.service.mongo.impl;

import com.itlyc.domain.mongo.*;
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
    public PageBeanVo findMyMovementByPage(int pageNum, int pageSize, Long userId) {

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

    /**
     * 查询好友动态列表
     * @param page 页码
     * @param pageSize 每页条数
     * @param pageSize 当前用户id
     * @return
     */
    @Override
    public PageBeanVo getFriendMovements(int page, int pageSize, Long userId) {

        // 定义分页查询条件
        Query query = new Query()
                .skip((page - 1) * pageSize).limit(pageSize)
                .with(Sort.by(Sort.Order.desc("created")));

        // 查询好友列表中好友发布的动态
        List<FriendMovement> friendMovements = mongoTemplate.find(query, FriendMovement.class, ConstantUtil.MOVEMENT_FRIEND + userId);

        List<Movement> movements = new ArrayList<>();

        // 未审核通过的动态数量
        int failStateCount = 0;

        if(!CollectionUtils.isEmpty(friendMovements)){
            for (FriendMovement friendMovement : friendMovements) {

                ObjectId movementId = friendMovement.getPublishId();

                Movement movement = mongoTemplate.findById(movementId, Movement.class);

                // 判断动态是否审核通过
                if(movement.getState() == 1){
                    movements.add(movement);
                }else {
                    failStateCount ++;
                }

            }
        }

        long count = mongoTemplate.count(query, ConstantUtil.MOVEMENT_FRIEND + userId);

        return new PageBeanVo(page, pageSize,count - failStateCount , movements);
    }

    /**
     * 查询推荐动态列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param userId 当前登录用户id
     * @return
     */
    @Override
    public PageBeanVo findRecommendMovementByPage(int pageNum, int pageSize, Long userId) {

        Query query = new Query(
            Criteria.where("userId").is(userId)
        ).skip((pageNum - 1) * pageSize).limit(pageSize)
                .with(Sort.by(Sort.Order.desc("created")));

        List<RecommendMovement> recommendMovementList = mongoTemplate.find(query, RecommendMovement.class);

        // 审核未通过的动态数量
        int failStatCount = 0;

        List<Movement> movementList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(recommendMovementList)){
            for (RecommendMovement recommendMovement : recommendMovementList) {
                ObjectId movementId = recommendMovement.getPublishId();
                Movement movement = mongoTemplate.findById(movementId, Movement.class);
                if(movement.getState() == 1){
                    movementList.add(movement);
                }else {
                    failStatCount ++;
                }
            }
        }

        long count = mongoTemplate.count(query, RecommendMovement.class);

        return new PageBeanVo(pageNum, pageSize,count - failStatCount, movementList);
    }
}
