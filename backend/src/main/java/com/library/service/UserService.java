package com.library.service;

import com.library.entity.User;
import com.library.entity.enums.BlacklistStatus;
import com.library.entity.enums.UserLevel;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlowLogService flowLogService;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(User user) {
        if (user.getLevel() == null) {
            user.setLevel(UserLevel.BRONZE);
        }
        if (user.getMaxBorrowCount() == null) {
            user.setMaxBorrowCount(user.getLevel().getMaxBorrowCount());
        }
        if (user.getDepositBalance() == null) {
            user.setDepositBalance(0.0);
        }
        if (user.getBlacklistStatus() == null) {
            user.setBlacklistStatus(BlacklistStatus.NORMAL);
        }
        if (user.getOverdueCount() == null) {
            user.setOverdueCount(0);
        }
        return userRepository.save(user);
    }

    public User updateLevel(Long userId, UserLevel level) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLevel(level);
            user.setMaxBorrowCount(level.getMaxBorrowCount());
            return userRepository.save(user);
        }
        return null;
    }

    public void addToBlacklist(Long userId, String reason) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setBlacklistStatus(BlacklistStatus.BLACKLISTED);
            userRepository.save(user);
            flowLogService.logBlacklistAdd(userId, reason);
        }
    }

    public void removeFromBlacklist(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setBlacklistStatus(BlacklistStatus.NORMAL);
            user.setOverdueCount(0);
            userRepository.save(user);
            flowLogService.logBlacklistRemove(userId);
        }
    }

    public void incrementOverdueCount(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setOverdueCount(user.getOverdueCount() + 1);
            userRepository.save(user);
        }
    }

    public boolean isBlacklisted(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(u -> BlacklistStatus.BLACKLISTED == u.getBlacklistStatus()).orElse(false);
    }

    public int getCurrentBorrowCount(Long userId) {
        return 0;
    }

    public void updateDeposit(Long userId, Double amount) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setDepositBalance(user.getDepositBalance() + amount);
            userRepository.save(user);
        }
    }
}