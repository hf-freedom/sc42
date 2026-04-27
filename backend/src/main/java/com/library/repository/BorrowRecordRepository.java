package com.library.repository;

import com.library.entity.BorrowRecord;
import com.library.entity.enums.BorrowStatus;
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
public class BorrowRecordRepository {
    private final Map<Long, BorrowRecord> borrowRecords = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public BorrowRecord save(BorrowRecord record) {
        if (record.getId() == null) {
            record.setId(idGenerator.getAndIncrement());
            record.setCreatedAt(LocalDateTime.now());
        }
        record.setUpdatedAt(LocalDateTime.now());
        borrowRecords.put(record.getId(), record);
        return record;
    }

    public Optional<BorrowRecord> findById(Long id) {
        return Optional.ofNullable(borrowRecords.get(id));
    }

    public List<BorrowRecord> findAll() {
        return new ArrayList<>(borrowRecords.values());
    }

    public List<BorrowRecord> findByUserId(Long userId) {
        return borrowRecords.values().stream()
                .filter(record -> userId.equals(record.getUserId()))
                .collect(Collectors.toList());
    }

    public List<BorrowRecord> findByUserIdAndStatus(Long userId, BorrowStatus status) {
        return borrowRecords.values().stream()
                .filter(record -> userId.equals(record.getUserId()) && status == record.getStatus())
                .collect(Collectors.toList());
    }

    public List<BorrowRecord> findByBookId(Long bookId) {
        return borrowRecords.values().stream()
                .filter(record -> bookId.equals(record.getBookId()))
                .collect(Collectors.toList());
    }

    public List<BorrowRecord> findByBookCopyId(Long bookCopyId) {
        return borrowRecords.values().stream()
                .filter(record -> bookCopyId.equals(record.getBookCopyId()))
                .collect(Collectors.toList());
    }

    public Optional<BorrowRecord> findByBookCopyIdAndStatus(Long bookCopyId, BorrowStatus status) {
        return borrowRecords.values().stream()
                .filter(record -> bookCopyId.equals(record.getBookCopyId()) && status == record.getStatus())
                .findFirst();
    }

    public List<BorrowRecord> findByStatus(BorrowStatus status) {
        return borrowRecords.values().stream()
                .filter(record -> status == record.getStatus())
                .collect(Collectors.toList());
    }

    public List<BorrowRecord> findByDueTimeBefore(LocalDateTime time) {
        return borrowRecords.values().stream()
                .filter(record -> record.getDueTime() != null && record.getDueTime().isBefore(time))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        borrowRecords.remove(id);
    }

    public boolean existsById(Long id) {
        return borrowRecords.containsKey(id);
    }
}