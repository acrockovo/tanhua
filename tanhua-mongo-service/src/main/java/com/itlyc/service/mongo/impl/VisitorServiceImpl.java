package com.itlyc.service.mongo.impl;

import cn.hutool.core.util.RandomUtil;
import com.itlyc.domain.mongo.Visitor;
import com.itlyc.service.mongo.VisitorService;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.IvParameterSpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

@Service
public class VisitorServiceImpl implements VisitorService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 查找最近访客
     * @param userId 当前登录用户id
     * @param lastAccessTime 最后登录时间
     * @return
     */
    @Override
    public List<Visitor> findVisitorsSinceLastAccessTime(Long userId, Long lastAccessTime) {

        Query query = new Query(
                Criteria.where("userId").is(userId)
                        .and("date").gte(lastAccessTime)//大于最后一次登陆时间
        ).with(Sort.by(Sort.Order.desc("date"))).skip(0).limit(4);

        return  mongoTemplate.find(query,Visitor.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Long userId, Long visitorUserId) {

        Query query = new Query(
                Criteria.where("userId").is(userId)
                .and("visitorUserId").is(visitorUserId)
        );

        Update update = new Update();
        update.set("date",Long.valueOf(System.currentTimeMillis() + ""));
        update.set("from","动态");
        update.set("userId",userId);
        update.set("visitorUserId",visitorUserId);
        update.set("score",RandomUtil.randomDouble(70,90));
        // 如果存在就更新，不存在就插入
        mongoTemplate.upsert(query,update,Visitor.class);
    }
}
