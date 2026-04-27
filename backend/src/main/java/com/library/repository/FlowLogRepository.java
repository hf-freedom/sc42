package com.library.repository;

import com.library.entity.FlowLog;
import com.library.entity.enums.FlowType;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class FlowLogRepository {
    private final Map<Long, FlowLog> flowLogs = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public FlowLog save(FlowLog flowLog) {
        if (flowLog.getId() == null) {
            flowLog.setId(idGenerator.getAndIncrement());
        }
        if (flowLog.getCreatedAt() == null) {
            flowLog.setCreatedAt(LocalDateTime.now());
        }
        flowLogs.put(flowLog.getId(), flowLog);
        return flowLog;
    }

    public Optional<FlowLog> findById(Long id) {
        return Optional.ofNullable(flowLogs.get(id));
    }

    public List<FlowLog> findAll() {
        return flowLogs.values().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<FlowLog> findByUserId(Long userId) {
        return flowLogs.values().stream()
                .filter(log -> userId.equals(log.getUserId()))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<FlowLog> findByFlowType(FlowType flowType) {
        return flowLogs.values().stream()
                .filter(log -> flowType == log.getFlowType())
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<FlowLog> findByRelatedId(Long relatedId) {
        return flowLogs.values().stream()
                .filter(log -> relatedId.equals(log.getRelatedId()))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        flowLogs.remove(id);
    }

    public boolean existsById(Long id) {
        return flowLogs.containsKey(id);
    }
}