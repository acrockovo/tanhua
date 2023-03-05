package com.itlyc.web.listener;

import com.itlyc.domain.db.Log;
import com.itlyc.service.db.LogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "consumer-log-group", topic = "tanhua-log")
public class LogMQListener implements RocketMQListener<Log> {

    @Reference
    private LogService logService;

    @Override
    public void onMessage(Log logs) {
        log.info("日志消费者接收到消息 " + logs);
        logService.save(logs);
    }
}
