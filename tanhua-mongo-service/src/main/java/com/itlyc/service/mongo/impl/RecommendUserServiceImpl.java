package com.itlyc.service.mongo.impl;

import com.itlyc.domain.mongo.RecommendUser;
import com.itlyc.domain.vo.PageBeanVo;
import com.itlyc.service.mongo.RecommendUserService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class RecommendUserServiceImpl implements RecommendUserService {


    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 查找今日佳人
     * @param userId 当前登录用户id
     * @return
     */
    @Override
    public RecommendUser findTodayBest(Long userId) {

        Query query = new Query(
                Criteria.where("toUserId").is(userId)
        ).with(Sort.by(Sort.Order.desc("score")))
                .skip(0).limit(1); // 今日佳人只要一个

        return mongoTemplate.findOne(query,RecommendUser.class);
    }

    /**
     * 查找推荐好友信息列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param userId 当前登录用户id
     * @return
     */
    @Override
    public PageBeanVo findRecommendUserByPage(Integer pageNum, Integer pageSize, Long userId) {

        Query query = new Query(
                Criteria.where("toUserId").is(userId)
        ).with(Sort.by(Sort.Order.desc("score")))
                .skip((pageNum - 1) * pageSize).limit(pageSize);

        List<RecommendUser> recommendUserList = mongoTemplate.find(query, RecommendUser.class);
        long count = mongoTemplate.count(query, RecommendUser.class);

        return new PageBeanVo(pageNum, pageSize, count, recommendUserList);
    }

    /**
     * 查找推荐好友详细信息
     * @param recommendUserId 推荐人id
     * @param userId 当前登录用户id
     * @return
     */
    @Override
    public RecommendUser findPersonal(Long recommendUserId, Long userId) {
        Query query = new Query(
                Criteria.where("userId").is(recommendUserId)
                .and("toUserId").is(userId)
        );
        return mongoTemplate.findOne(query, RecommendUser.class);
    }
}
