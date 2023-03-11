package com.itlyc.service.db;

import com.itlyc.domain.vo.AnalysisSummaryVo;

public interface AnalysisByDayService {
    // 保存每日分析数据
    void saveOrUpdate();
    // 统计数据展示
    AnalysisSummaryVo findSummary();

}
