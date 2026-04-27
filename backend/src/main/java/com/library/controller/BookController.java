package com.library.controller;

import com.library.common.Result;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public Result<List<Book>> getAllBooks() {
        List<Book> books = bookService.findAll();
        return Result.success(books);
    }

    @GetMapping("/{id}")
    public Result<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.findById(id);
        return book.map(Result::success).orElseGet(() -> Result.error("图书不存在"));
    }

    @GetMapping("/{id}/copies")
    public Result<List<BookCopy>> getBookCopies(@PathVariable Long id) {
        List<BookCopy> copies = bookService.findCopiesByBookId(id);
        return Result.success(copies);
    }

    @PostMapping
    public Result<Book> createBook(@RequestBody Map<String, Object> request) {
        try {
            Book book = new Book();
            book.setTitle((String) request.get("title"));
            book.setCategory((String) request.get("category"));
            
            int copyCount = request.get("copyCount") != null ? 
                ((Number) request.get("copyCount")).intValue() : 1;
            String libraryBranch = (String) request.getOrDefault("libraryBranch", "总馆");
            
            Book savedBook = bookService.createBook(book, copyCount, libraryBranch);
            return Result.success("创建成功", savedBook);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/status")
    public Result<Map<String, Object>> getBookStatus(@PathVariable Long id) {
        Optional<Book> bookOpt = bookService.findById(id);
        if (!bookOpt.isPresent()) {
            return Result.error("图书不存在");
        }
        Book book = bookOpt.get();
        List<BookCopy> copies = bookService.findCopiesByBookId(id);
        
        Map<String, Object> status = new HashMap<>();
        status.put("book", book);
        status.put("copies", copies);
        status.put("availableCount", book.getAvailableCopies());
        status.put("totalCount", book.getTotalCopies());
        status.put("damagedCount", book.getDamagedCopies());
        
        return Result.success(status);
    }
}