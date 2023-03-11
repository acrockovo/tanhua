package com.itlyc.mapper;

import com.itlyc.domain.db.AnalysisByDay;
import org.apache.ibatis.annotations.Param;

public interface AnalysisByDayMapper {
    // 查询今日数据
    AnalysisByDay findDay(String today);
    // 保存数据
    void save(AnalysisByDay analysisByDay);
    // 更新数据
    void updateById(AnalysisByDay analysisByDay);
    // 查询用户总数
    Long findUserSum();
    // 查询30天活跃用户数
    Long findActiveCountWithinPeriod(@Param("start") String passDay, @Param("end") String yesterday);
}
