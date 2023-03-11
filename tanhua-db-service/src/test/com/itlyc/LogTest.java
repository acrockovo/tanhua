package com.itlyc;

import cn.hutool.core.util.RandomUtil;
import com.itlyc.domain.db.Log;
import com.itlyc.mapper.LogMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LogTest {

    @Autowired
    private LogMapper logMapper;

    @Test
    public void testInsertLoginLog() {

        String yesterday = "2023-03-10";//昨天
        String today = "2023-03-11";//今天

        //模拟昨日注册
        for (int i = 0; i < 10; i++) {
            Log log = new Log();
            log.setUserId(RandomUtil.randomLong(1, 50));
            log.setLogTime(yesterday);
            log.setType("0102");
            logMapper.save(log);
        }

        //模拟今日登录
        for (int i = 0; i < 5; i++) {
            Log log = new Log();
            log.setUserId(RandomUtil.randomLong(1, 50));
            log.setLogTime(today);
            log.setType("0101");
            logMapper.save(log);
        }

        //模拟今日注册
        for (int i = 0; i < 5; i++) {
            Log log = new Log();
            log.setUserId(RandomUtil.randomLong(1, 50));
            log.setLogTime(today);
            log.setType("0102");
            logMapper.save(log);
        }


        //模拟今日其他操作
        String[] types = new String[]{"0201", "0202", "0203", "0204", "0205", "0206", "0207"};
        for (int i = 0; i < 10; i++) {
            Log log = new Log();
            log.setUserId(RandomUtil.randomLong(1, 50));
            log.setLogTime(today);
            int index = new Random().nextInt(7);
            log.setType(types[index]);
            logMapper.save(log);
        }
    }
}
