package com.library.repository;

import com.library.entity.Fine;
import com.library.entity.enums.FineStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class FineRepository {
    private final Map<Long, Fine> fines = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Fine save(Fine fine) {
        if (fine.getId() == null) {
            fine.setId(idGenerator.getAndIncrement());
            fine.setCreatedAt(LocalDateTime.now());
        }
        fine.setUpdatedAt(LocalDateTime.now());
        fines.put(fine.getId(), fine);
        return fine;
    }

    public Optional<Fine> findById(Long id) {
        return Optional.ofNullable(fines.get(id));
    }

    public List<Fine> findAll() {
        return new ArrayList<>(fines.values());
    }

    public List<Fine> findByUserId(Long userId) {
        return fines.values().stream()
                .filter(f -> userId.equals(f.getUserId()))
                .collect(Collectors.toList());
    }

    public List<Fine> findByUserIdAndStatus(Long userId, FineStatus status) {
        return fines.values().stream()
                .filter(f -> userId.equals(f.getUserId()) && status == f.getStatus())
                .collect(Collectors.toList());
    }

    public List<Fine> findByBorrowRecordId(Long borrowRecordId) {
        return fines.values().stream()
                .filter(f -> borrowRecordId.equals(f.getBorrowRecordId()))
                .collect(Collectors.toList());
    }

    public Optional<Fine> findByBorrowRecordIdAndStatus(Long borrowRecordId, FineStatus status) {
        return fines.values().stream()
                .filter(f -> borrowRecordId.equals(f.getBorrowRecordId()) && status == f.getStatus())
                .findFirst();
    }

    public List<Fine> findByStatus(FineStatus status) {
        return fines.values().stream()
                .filter(f -> status == f.getStatus())
                .collect(Collectors.toList());
    }

    public List<Fine> findByDueDateBefore(LocalDate date) {
        return fines.values().stream()
                .filter(f -> f.getDueDate() != null && f.getDueDate().isBefore(date))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        fines.remove(id);
    }

    public boolean existsById(Long id) {
        return fines.containsKey(id);
    }
}