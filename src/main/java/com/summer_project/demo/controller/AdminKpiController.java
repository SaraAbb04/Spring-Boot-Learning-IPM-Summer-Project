package com.summer_project.demo.controller;

import com.summer_project.demo.dto.KpiOverviewResponse;
import com.summer_project.demo.service.KpiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/kpi")
public class AdminKpiController {
    private final KpiService kpiService;
    public AdminKpiController(KpiService kpiService) {
        this.kpiService = kpiService;
    }
    @GetMapping("/overview")
    public ResponseEntity<KpiOverviewResponse> getOverview() {
        return ResponseEntity.ok(kpiService.getOverview());
    }
}