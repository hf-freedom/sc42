package com.library.controller;

import com.library.common.Result;
import com.library.entity.User;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return Result.success(users);
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(Result::success).orElseGet(() -> Result.error("用户不存在"));
    }

    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        try {
            User savedUser = userService.createUser(user);
            return Result.success("创建成功", savedUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/blacklist/check")
    public Result<Boolean> checkBlacklist(@PathVariable Long id) {
        boolean isBlacklisted = userService.isBlacklisted(id);
        return Result.success(isBlacklisted);
    }

    @PostMapping("/{id}/blacklist/add")
    public Result<User> addToBlacklist(@PathVariable Long id, @RequestBody(required = false) ReasonRequest request) {
        try {
            String reason = request != null && request.getReason() != null ? request.getReason() : "管理员操作";
            userService.addToBlacklist(id, reason);
            Optional<User> user = userService.findById(id);
            return user.map(u -> Result.success("已加入黑名单", u))
                    .orElseGet(() -> Result.error("用户不存在"));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/blacklist/remove")
    public Result<User> removeFromBlacklist(@PathVariable Long id) {
        try {
            userService.removeFromBlacklist(id);
            Optional<User> user = userService.findById(id);
            return user.map(u -> Result.success("已移出黑名单", u))
                    .orElseGet(() -> Result.error("用户不存在"));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/info")
    public Result<UserInfoResponse> getUserInfo(@PathVariable Long id) {
        Optional<User> userOpt = userService.findById(id);
        if (!userOpt.isPresent()) {
            return Result.error("用户不存在");
        }
        User user = userOpt.get();
        UserInfoResponse info = new UserInfoResponse();
        info.setUser(user);
        info.setBlacklisted(userService.isBlacklisted(id));
        return Result.success(info);
    }

    public static class ReasonRequest {
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class UserInfoResponse {
        private User user;
        private boolean blacklisted;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public boolean isBlacklisted() {
            return blacklisted;
        }

        public void setBlacklisted(boolean blacklisted) {
            this.blacklisted = blacklisted;
        }
    }
}