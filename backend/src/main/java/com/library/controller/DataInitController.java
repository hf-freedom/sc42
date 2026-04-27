package com.library.controller;

import com.library.common.Result;
import com.library.entity.Book;
import com.library.entity.User;
import com.library.entity.enums.BlacklistStatus;
import com.library.entity.enums.UserLevel;
import com.library.service.BookService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/init")
@CrossOrigin(origins = "*")
public class DataInitController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @PostMapping
    public Result<Map<String, Object>> initData() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            Book book1 = new Book();
            book1.setTitle("Java编程思想");
            book1.setCategory("计算机科学");
            Book savedBook1 = bookService.createBook(book1, 3, "总馆");
            
            Book book2 = new Book();
            book2.setTitle("深入理解Java虚拟机");
            book2.setCategory("计算机科学");
            Book savedBook2 = bookService.createBook(book2, 2, "总馆");
            
            Book book3 = new Book();
            book3.setTitle("数据结构与算法分析");
            book3.setCategory("计算机科学");
            Book savedBook3 = bookService.createBook(book3, 4, "分馆A");
            
            Book book4 = new Book();
            book4.setTitle("红楼梦");
            book4.setCategory("文学经典");
            Book savedBook4 = bookService.createBook(book4, 5, "总馆");
            
            Book book5 = new Book();
            book5.setTitle("三国演义");
            book5.setCategory("文学经典");
            Book savedBook5 = bookService.createBook(book5, 3, "分馆A");
            
            result.put("books", new Book[]{savedBook1, savedBook2, savedBook3, savedBook4, savedBook5});
            
            User user1 = new User();
            user1.setUsername("zhangsan");
            user1.setRealName("张三");
            user1.setLevel(UserLevel.GOLD);
            user1.setDepositBalance(100.0);
            user1.setBlacklistStatus(BlacklistStatus.NORMAL);
            User savedUser1 = userService.createUser(user1);
            
            User user2 = new User();
            user2.setUsername("lisi");
            user2.setRealName("李四");
            user2.setLevel(UserLevel.SILVER);
            user2.setDepositBalance(50.0);
            user2.setBlacklistStatus(BlacklistStatus.NORMAL);
            User savedUser2 = userService.createUser(user2);
            
            User user3 = new User();
            user3.setUsername("wangwu");
            user3.setRealName("王五");
            user3.setLevel(UserLevel.BRONZE);
            user3.setDepositBalance(0.0);
            user3.setBlacklistStatus(BlacklistStatus.NORMAL);
            User savedUser3 = userService.createUser(user3);
            
            result.put("users", new User[]{savedUser1, savedUser2, savedUser3});
            
            return Result.success("初始化数据成功", result);
        } catch (Exception e) {
            return Result.error("初始化数据失败: " + e.getMessage());
        }
    }
}