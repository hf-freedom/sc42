package com.library.repository;

import com.library.entity.BookCopy;
import com.library.entity.enums.BookCopyStatus;
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
public class BookCopyRepository {
    private final Map<Long, BookCopy> bookCopies = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public BookCopy save(BookCopy bookCopy) {
        if (bookCopy.getId() == null) {
            bookCopy.setId(idGenerator.getAndIncrement());
            bookCopy.setCreatedAt(LocalDateTime.now());
        }
        bookCopy.setUpdatedAt(LocalDateTime.now());
        bookCopies.put(bookCopy.getId(), bookCopy);
        return bookCopy;
    }

    public Optional<BookCopy> findById(Long id) {
        return Optional.ofNullable(bookCopies.get(id));
    }

    public List<BookCopy> findAll() {
        return new ArrayList<>(bookCopies.values());
    }

    public List<BookCopy> findByBookId(Long bookId) {
        return bookCopies.values().stream()
                .filter(copy -> bookId.equals(copy.getBookId()))
                .collect(Collectors.toList());
    }

    public List<BookCopy> findByBookIdAndStatus(Long bookId, BookCopyStatus status) {
        return bookCopies.values().stream()
                .filter(copy -> bookId.equals(copy.getBookId()) && status == copy.getStatus())
                .collect(Collectors.toList());
    }

    public Optional<BookCopy> findFirstAvailableByBookId(Long bookId) {
        return bookCopies.values().stream()
                .filter(copy -> bookId.equals(copy.getBookId()) && BookCopyStatus.AVAILABLE == copy.getStatus())
                .findFirst();
    }

    public void deleteById(Long id) {
        bookCopies.remove(id);
    }

    public boolean existsById(Long id) {
        return bookCopies.containsKey(id);
    }
}