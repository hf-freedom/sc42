package com.library.repository;

import com.library.entity.Reservation;
import com.library.entity.enums.ReservationStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ReservationRepository {
    private final Map<Long, Reservation> reservations = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Reservation save(Reservation reservation) {
        if (reservation.getId() == null) {
            reservation.setId(idGenerator.getAndIncrement());
            reservation.setCreatedAt(LocalDateTime.now());
        }
        reservation.setUpdatedAt(LocalDateTime.now());
        reservations.put(reservation.getId(), reservation);
        return reservation;
    }

    public Optional<Reservation> findById(Long id) {
        return Optional.ofNullable(reservations.get(id));
    }

    public List<Reservation> findAll() {
        return new ArrayList<>(reservations.values());
    }

    public List<Reservation> findByUserId(Long userId) {
        return reservations.values().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .collect(Collectors.toList());
    }

    public List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status) {
        return reservations.values().stream()
                .filter(r -> userId.equals(r.getUserId()) && status == r.getStatus())
                .collect(Collectors.toList());
    }

    public List<Reservation> findByBookId(Long bookId) {
        return reservations.values().stream()
                .filter(r -> bookId.equals(r.getBookId()))
                .collect(Collectors.toList());
    }

    public List<Reservation> findByBookIdAndStatus(Long bookId, ReservationStatus status) {
        return reservations.values().stream()
                .filter(r -> bookId.equals(r.getBookId()) && status == r.getStatus())
                .collect(Collectors.toList());
    }

    public Optional<Reservation> findFirstPendingByBookId(Long bookId) {
        return reservations.values().stream()
                .filter(r -> bookId.equals(r.getBookId()) && ReservationStatus.PENDING == r.getStatus())
                .min(Comparator.comparing(Reservation::getReservedTime));
    }

    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservations.values().stream()
                .filter(r -> status == r.getStatus())
                .collect(Collectors.toList());
    }

    public boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, ReservationStatus status) {
        return reservations.values().stream()
                .anyMatch(r -> userId.equals(r.getUserId()) && bookId.equals(r.getBookId()) && status == r.getStatus());
    }

    public void deleteById(Long id) {
        reservations.remove(id);
    }

    public boolean existsById(Long id) {
        return reservations.containsKey(id);
    }
}