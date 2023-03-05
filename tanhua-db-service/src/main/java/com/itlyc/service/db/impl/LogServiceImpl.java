package com.itlyc.service.db.impl;

import com.itlyc.domain.db.Log;
import com.itlyc.mapper.LogMapper;
import com.itlyc.service.db.LogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
public class LogServiceImpl implements LogService {

    @Resource
    private LogMapper logMapper;

    /**
     * 保存日志消息
     * @param logs
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Log logs) {
        logMapper.save(logs);
        log.info("日志保存成功 " + logs);
    }
}
