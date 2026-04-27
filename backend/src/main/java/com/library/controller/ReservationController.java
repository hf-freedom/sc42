package com.library.controller;

import com.library.common.Result;
import com.library.entity.Reservation;
import com.library.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public Result<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.findAll();
        return Result.success(reservations);
    }

    @GetMapping("/{id}")
    public Result<Reservation> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.findById(id);
        return reservation.map(Result::success).orElseGet(() -> Result.error("预约记录不存在"));
    }

    @GetMapping("/user/{userId}")
    public Result<List<Reservation>> getReservationsByUserId(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.findByUserId(userId);
        return Result.success(reservations);
    }

    @GetMapping("/user/{userId}/pending")
    public Result<List<Reservation>> getPendingReservationsByUserId(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.findPendingByUserId(userId);
        return Result.success(reservations);
    }

    @GetMapping("/book/{bookId}")
    public Result<List<Reservation>> getReservationsByBookId(@PathVariable Long bookId) {
        List<Reservation> reservations = reservationService.findByBookId(bookId);
        return Result.success(reservations);
    }

    @PostMapping
    public Result<Reservation> reserveBook(@RequestBody Map<String, Long> request) {
        try {
            Long userId = request.get("userId");
            Long bookId = request.get("bookId");
            
            if (userId == null || bookId == null) {
                return Result.error("userId 和 bookId 不能为空");
            }
            
            Reservation reservation = reservationService.reserveBook(userId, bookId);
            return Result.success("预约成功", reservation);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public Result<Reservation> cancelReservation(@PathVariable Long id) {
        try {
            reservationService.cancelReservation(id);
            Optional<Reservation> reservation = reservationService.findById(id);
            return reservation.map(r -> Result.success("取消预约成功", r))
                    .orElseGet(() -> Result.error("预约记录不存在"));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/book/{bookId}/has-pending")
    public Result<Boolean> hasPendingReservation(@PathVariable Long bookId) {
        boolean hasPending = reservationService.hasPendingReservation(bookId);
        return Result.success(hasPending);
    }

    @GetMapping("/user/{userId}/book/{bookId}/check")
    public Result<Boolean> checkUserPendingReservation(
            @PathVariable Long userId, 
            @PathVariable Long bookId) {
        boolean hasPending = reservationService.hasUserPendingReservation(userId, bookId);
        return Result.success(hasPending);
    }
}