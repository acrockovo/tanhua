package com.itlyc.web.manager;

import com.itlyc.service.db.AnalysisByDayService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AnalysisManager {

    @Reference
    private AnalysisByDayService analysisByDayService;

    /**
     * 查询首页统计数据
     * @return
     */
    public ResponseEntity findSummary() {
        return ResponseEntity.ok(analysisByDayService.findSummary());
    }
}
