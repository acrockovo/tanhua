package com.itlyc.web.job;

import com.itlyc.service.db.AnalysisByDayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class MyJob {

    @Reference
    private AnalysisByDayService analysisByDayService;

    //@Scheduled(cron = "0 0/1 * * * ?") // 每分钟
    @Scheduled(cron = "0 0 0/1 * * ?") // 每小时
    public void saveOrUpdateAnalysisByDay(){
        analysisByDayService.saveOrUpdate();
        log.info( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 每日统计数据已保存");
    }
}
