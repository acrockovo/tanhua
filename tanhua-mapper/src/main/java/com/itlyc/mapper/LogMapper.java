package com.itlyc.mapper;

import com.itlyc.domain.db.Log;
import org.apache.ibatis.annotations.Param;

public interface LogMapper {
    // 保存日志
    void save(Log logs);
    // 查询今日某种类型操作人数
    int findTodayCountByType(@Param("today") String today, @Param("type") String type);
    //查询今日活跃人数
    int findTodayActiveCount(String today);
    //查询次日留存人数
    int findRetention1DayCount(@Param("today") String today,@Param("yesterday") String yesterday);
}
