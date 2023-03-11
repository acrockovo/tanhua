package com.itlyc.controller;

import com.itlyc.web.manager.AnalysisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysisController {

    @Autowired
    private AnalysisManager analysisManager;

    /**
     * 查询首页统计数据
     * @return
     */
    @GetMapping("/dashboard/summary")
    public ResponseEntity findSummary(){
        return analysisManager.findSummary();
    }
}
