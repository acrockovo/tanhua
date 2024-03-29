package com.itlyc;

import cn.hutool.core.util.RandomUtil;
import com.itlyc.domain.mongo.Movement;
import com.itlyc.domain.mongo.RecommendMovement;
import com.itlyc.service.mongo.impl.IdService;
import com.itlyc.util.ConstantUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RecommendMovementTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdService idService;

    // 添加5条推荐动态测试数据
    @Test
    public void test01() throws Exception {
        //查询5条动态
        Query query = new Query().skip(0).limit(10);
        List<Movement> movementList = mongoTemplate.find(query, Movement.class);

        for (int i = 0; i < 10; i++) {
            //创建推荐动态
            RecommendMovement recommendMovement = new RecommendMovement();
            recommendMovement.setCreated(System.currentTimeMillis());
            recommendMovement.setUserId(1L);//推荐给指定用户
            recommendMovement.setPid(idService.getNextId(ConstantUtil.MOVEMENT_ID));
            recommendMovement.setScore(RandomUtil.randomDouble(70, 99));
            recommendMovement.setPublishId(movementList.get(i).getId());
            mongoTemplate.save(recommendMovement);
        }
    }
}
