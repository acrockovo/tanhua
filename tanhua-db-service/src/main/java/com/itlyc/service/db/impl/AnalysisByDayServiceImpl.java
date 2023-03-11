package com.itlyc.service.db.impl;

import cn.hutool.core.date.DateUtil;
import com.itlyc.domain.db.AnalysisByDay;
import com.itlyc.domain.vo.AnalysisSummaryVo;
import com.itlyc.mapper.AnalysisByDayMapper;
import com.itlyc.mapper.LogMapper;
import com.itlyc.service.db.AnalysisByDayService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Service
public class AnalysisByDayServiceImpl implements AnalysisByDayService {

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private AnalysisByDayMapper analysisByDayMapper;

    @Override
    public void saveOrUpdate() {
        //1.准备今日和昨日字符串
        String today = DateUtil.offsetDay(new Date(), 0).toDateStr();
        String yesterday = DateUtil.offsetDay(new Date(), -1).toDateStr();

        //2.先查询今日记录
        AnalysisByDay analysisByDay = analysisByDayMapper.findToday(today);

        if (analysisByDay == null) {
            //若无则保存
            analysisByDay = new AnalysisByDay();
            analysisByDay.setRecordDate(new Date());
            analysisByDay.setNumRegistered(logMapper.findTodayCountByType(today,"0102"));//今日注册
            analysisByDay.setNumLogin(logMapper.findTodayCountByType(today,"0101"));//今日登陆
            analysisByDay.setNumActive(logMapper.findTodayActiveCount(today));//几日活跃
            analysisByDay.setNumRetention1d(logMapper.findRetention1DayCount(today,yesterday));//次日留存
            analysisByDay.setCreated(new Date());
            analysisByDay.setUpdated(new Date());

            analysisByDayMapper.save(analysisByDay);
        }else {
            //若有则更新
            analysisByDay.setNumRegistered(logMapper.findTodayCountByType(today,"0102"));
            analysisByDay.setNumLogin(logMapper.findTodayCountByType(today,"0101"));
            analysisByDay.setNumActive(logMapper.findTodayActiveCount(today));
            analysisByDay.setNumRetention1d(logMapper.findRetention1DayCount(today,yesterday));
            analysisByDay.setUpdated(new Date());

            analysisByDayMapper.updateById(analysisByDay);
        }
    }

    @Override
    public AnalysisSummaryVo findSummary() {
        return null;
    }
}
