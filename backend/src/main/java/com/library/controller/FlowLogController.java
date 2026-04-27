package com.library.controller;

import com.library.common.Result;
import com.library.entity.FlowLog;
import com.library.service.FlowLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flow-logs")
@CrossOrigin(origins = "*")
public class FlowLogController {

    @Autowired
    private FlowLogService flowLogService;

    @GetMapping
    public Result<List<FlowLog>> getAllFlowLogs() {
        List<FlowLog> logs = flowLogService.findAll();
        return Result.success(logs);
    }

    @GetMapping("/user/{userId}")
    public Result<List<FlowLog>> getFlowLogsByUserId(@PathVariable Long userId) {
        List<FlowLog> logs = flowLogService.findByUserId(userId);
        return Result.success(logs);
    }
}