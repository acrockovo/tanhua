package com.itlyc.service.db.impl;

import cn.hutool.core.date.DateUtil;
import com.itlyc.domain.db.AnalysisByDay;
import com.itlyc.domain.vo.AnalysisSummaryVo;
import com.itlyc.mapper.AnalysisByDayMapper;
import com.itlyc.mapper.LogMapper;
import com.itlyc.service.db.AnalysisByDayService;
import com.itlyc.util.ComputeUtil;
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
        AnalysisByDay analysisByDay = analysisByDayMapper.findDay(today);

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

    /**
     * 首页数据统计
     * @return
     */
    @Override
    public AnalysisSummaryVo findSummary() {
        AnalysisSummaryVo analysisSummaryVo = new AnalysisSummaryVo();
        String today = DateUtil.offsetDay(new Date(),0).toDateStr(); // 今天
        String yesterday = DateUtil.offsetDay(new Date(),-1).toDateStr(); // 昨天
        String passWeek = DateUtil.offsetDay(new Date(),-7).toDateStr(); // 最近7天
        String  passMonth = DateUtil.offsetDay(new Date(),-30).toDateStr(); // 最近30天
        // 查询用户总数
        Long userSum = analysisByDayMapper.findUserSum();
        // 查询30天活跃用户数
        Long activePassMonth = analysisByDayMapper.findActiveCountWithinPeriod(passMonth,yesterday);
        // 查询7天活跃用户数
        Long activePassWeek = analysisByDayMapper.findActiveCountWithinPeriod(passWeek,yesterday);
        // 查询今日数据
        AnalysisByDay todaySummary = analysisByDayMapper.findDay(today);
        // 查询昨日数据
        AnalysisByDay yesterdaySummary = analysisByDayMapper.findDay(yesterday);
        // 封装
        analysisSummaryVo.setCumulativeUsers(userSum); // 总用户数据
        analysisSummaryVo.setActivePassMonth(activePassMonth); // 30天活跃用户数
        analysisSummaryVo.setActivePassWeek(activePassWeek); // 7天活跃用户数
        analysisSummaryVo.setNewUsersToday(todaySummary.getNumRegistered().longValue()); // 今日注册数据
        analysisSummaryVo.setNewUsersTodayRate(ComputeUtil.computeRate(todaySummary.getNumRegistered(),yesterdaySummary.getNumRegistered()));
        analysisSummaryVo.setLoginTimesToday(todaySummary.getNumLogin().longValue());
        analysisSummaryVo.setLoginTimesTodayRate(ComputeUtil.computeRate(todaySummary.getNumLogin(),yesterdaySummary.getNumLogin()));
        analysisSummaryVo.setActiveUsersToday(todaySummary.getNumActive().longValue());
        analysisSummaryVo.setActiveUsersTodayRate(ComputeUtil.computeRate(todaySummary.getNumActive(),yesterdaySummary.getNumActive()));
        return analysisSummaryVo;
    }
}
