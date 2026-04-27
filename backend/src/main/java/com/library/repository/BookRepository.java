package com.library.repository;

import com.library.entity.Book;
import com.library.entity.enums.BookStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class BookRepository {
    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(idGenerator.getAndIncrement());
            book.setCreatedAt(LocalDateTime.now());
        }
        book.setUpdatedAt(LocalDateTime.now());
        books.put(book.getId(), book);
        return book;
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(books.get(id));
    }

    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    public List<Book> findByStatus(BookStatus status) {
        List<Book> result = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getStatus() == status) {
                result.add(book);
            }
        }
        return result;
    }

    public void deleteById(Long id) {
        books.remove(id);
    }

    public boolean existsById(Long id) {
        return books.containsKey(id);
    }
}