package com.itlyc.mapper;

import com.itlyc.domain.db.AnalysisByDay;

public interface AnalysisByDayMapper {
    // 查询今日数据
    AnalysisByDay findToday(String today);
    // 保存数据
    void save(AnalysisByDay analysisByDay);
    // 更新数据
    void updateById(AnalysisByDay analysisByDay);
}
