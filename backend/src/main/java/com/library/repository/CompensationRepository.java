package com.library.repository;

import com.library.entity.Compensation;
import com.library.entity.enums.CompensationStatus;
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
public class CompensationRepository {
    private final Map<Long, Compensation> compensations = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Compensation save(Compensation compensation) {
        if (compensation.getId() == null) {
            compensation.setId(idGenerator.getAndIncrement());
            compensation.setCreatedAt(LocalDateTime.now());
        }
        compensation.setUpdatedAt(LocalDateTime.now());
        compensations.put(compensation.getId(), compensation);
        return compensation;
    }

    public Optional<Compensation> findById(Long id) {
        return Optional.ofNullable(compensations.get(id));
    }

    public List<Compensation> findAll() {
        return new ArrayList<>(compensations.values());
    }

    public List<Compensation> findByUserId(Long userId) {
        return compensations.values().stream()
                .filter(c -> userId.equals(c.getUserId()))
                .collect(Collectors.toList());
    }

    public List<Compensation> findByUserIdAndStatus(Long userId, CompensationStatus status) {
        return compensations.values().stream()
                .filter(c -> userId.equals(c.getUserId()) && status == c.getStatus())
                .collect(Collectors.toList());
    }

    public List<Compensation> findByBorrowRecordId(Long borrowRecordId) {
        return compensations.values().stream()
                .filter(c -> borrowRecordId.equals(c.getBorrowRecordId()))
                .collect(Collectors.toList());
    }

    public Optional<Compensation> findByBorrowRecordIdAndStatus(Long borrowRecordId, CompensationStatus status) {
        return compensations.values().stream()
                .filter(c -> borrowRecordId.equals(c.getBorrowRecordId()) && status == c.getStatus())
                .findFirst();
    }

    public List<Compensation> findByStatus(CompensationStatus status) {
        return compensations.values().stream()
                .filter(c -> status == c.getStatus())
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        compensations.remove(id);
    }

    public boolean existsById(Long id) {
        return compensations.containsKey(id);
    }
}