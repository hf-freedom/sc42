package com.library.service;

import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.enums.BookCopyStatus;
import com.library.entity.enums.BookStatus;
import com.library.repository.BookCopyRepository;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public List<BookCopy> findCopiesByBookId(Long bookId) {
        return bookCopyRepository.findByBookId(bookId);
    }

    public Book createBook(Book book, int copyCount, String libraryBranch) {
        book.setTotalCopies(copyCount);
        book.setAvailableCopies(copyCount);
        book.setDamagedCopies(0);
        book.setStatus(BookStatus.AVAILABLE);
        book.setCreatedAt(LocalDateTime.now());
        
        Book savedBook = bookRepository.save(book);
        
        for (int i = 0; i < copyCount; i++) {
            BookCopy copy = new BookCopy();
            copy.setBookId(savedBook.getId());
            copy.setBarcode("BC-" + savedBook.getId() + "-" + String.format("%03d", i + 1));
            copy.setLibraryBranch(libraryBranch);
            copy.setStatus(BookCopyStatus.AVAILABLE);
            copy.setCreatedAt(LocalDateTime.now());
            bookCopyRepository.save(copy);
        }
        
        return savedBook;
    }

    public Book updateBookStatus(Long bookId, BookStatus status) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            book.setStatus(status);
            book.setUpdatedAt(LocalDateTime.now());
            return bookRepository.save(book);
        }
        return null;
    }

    public void updateAvailableCopies(Long bookId, int delta) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            book.setAvailableCopies(book.getAvailableCopies() + delta);
            updateBookStatusBasedOnCopies(book);
            book.setUpdatedAt(LocalDateTime.now());
            bookRepository.save(book);
        }
    }

    public void updateDamagedCopies(Long bookId, int delta) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            book.setDamagedCopies(book.getDamagedCopies() + delta);
            book.setUpdatedAt(LocalDateTime.now());
            bookRepository.save(book);
        }
    }

    private void updateBookStatusBasedOnCopies(Book book) {
        if (book.getAvailableCopies() <= 0) {
            book.setStatus(BookStatus.FULLY_BORROWED);
        } else {
            book.setStatus(BookStatus.AVAILABLE);
        }
    }

    public Optional<BookCopy> findAvailableCopy(Long bookId) {
        return bookCopyRepository.findFirstAvailableByBookId(bookId);
    }

    public void updateCopyStatus(Long copyId, BookCopyStatus status) {
        Optional<BookCopy> copyOpt = bookCopyRepository.findById(copyId);
        if (copyOpt.isPresent()) {
            BookCopy copy = copyOpt.get();
            BookCopyStatus oldStatus = copy.getStatus();
            copy.setStatus(status);
            copy.setUpdatedAt(LocalDateTime.now());
            bookCopyRepository.save(copy);
            
            if (oldStatus == BookCopyStatus.AVAILABLE && status != BookCopyStatus.AVAILABLE) {
                updateAvailableCopies(copy.getBookId(), -1);
            } else if (oldStatus != BookCopyStatus.AVAILABLE && status == BookCopyStatus.AVAILABLE) {
                updateAvailableCopies(copy.getBookId(), 1);
            }
            
            if (status == BookCopyStatus.DAMAGED && oldStatus != BookCopyStatus.DAMAGED) {
                updateDamagedCopies(copy.getBookId(), 1);
            } else if (oldStatus == BookCopyStatus.DAMAGED && status != BookCopyStatus.DAMAGED) {
                updateDamagedCopies(copy.getBookId(), -1);
            }
        }
    }

    public Optional<BookCopy> findCopyById(Long copyId) {
        return bookCopyRepository.findById(copyId);
    }
}