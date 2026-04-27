package com.library.service;

import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.entity.Reservation;
import com.library.entity.User;
import com.library.entity.enums.ReservationStatus;
import com.library.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private FlowLogService flowLogService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompensationService compensationService;

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> findByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> findPendingByUserId(Long userId) {
        return reservationRepository.findByUserIdAndStatus(userId, ReservationStatus.PENDING);
    }

    public boolean hasPendingReservation(Long bookId) {
        return !reservationRepository.findByBookIdAndStatus(bookId, ReservationStatus.PENDING).isEmpty();
    }

    public boolean hasUserPendingReservation(Long userId, Long bookId) {
        return reservationRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, ReservationStatus.PENDING);
    }

    public Optional<Reservation> findFirstPendingReservation(Long bookId) {
        return reservationRepository.findFirstPendingByBookId(bookId);
    }

    public Reservation reserveBook(Long userId, Long bookId) throws Exception {
        Optional<User> userOpt = userService.findById(userId);
        if (!userOpt.isPresent()) {
            throw new Exception("用户不存在");
        }

        if (userService.isBlacklisted(userId)) {
            throw new Exception("用户在黑名单中，无法预约");
        }

        if (compensationService.hasPendingCompensation(userId)) {
            throw new Exception("您有未处理的图书赔偿，请先完成赔偿后再预约");
        }

        if (hasUserPendingReservation(userId, bookId)) {
            throw new Exception("您已预约过该图书");
        }

        Optional<Book> bookOpt = bookService.findById(bookId);
        if (!bookOpt.isPresent()) {
            throw new Exception("图书不存在");
        }
        Book book = bookOpt.get();

        Optional<BookCopy> availableCopy = bookService.findAvailableCopy(bookId);
        if (availableCopy.isPresent()) {
            throw new Exception("该图书目前有可借副本，请直接借阅，无需预约");
        }

        List<Reservation> existingReservations = reservationRepository.findByBookIdAndStatus(bookId, ReservationStatus.PENDING);
        int queuePosition = existingReservations.size() + 1;

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setBookId(bookId);
        reservation.setQueuePosition(queuePosition);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setReservedTime(LocalDateTime.now());
        reservation.setCreatedAt(LocalDateTime.now());

        Reservation savedReservation = reservationRepository.save(reservation);

        flowLogService.logReserve(userId, book.getTitle(), savedReservation.getId());

        return savedReservation;
    }

    public void cancelReservation(Long reservationId) throws Exception {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (!reservationOpt.isPresent()) {
            throw new Exception("预约记录不存在");
        }
        Reservation reservation = reservationOpt.get();

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new Exception("该预约不可取消");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);

        updateQueuePositions(reservation.getBookId());

        Optional<Book> bookOpt = bookService.findById(reservation.getBookId());
        bookOpt.ifPresent(book -> flowLogService.logCancelReserve(reservation.getUserId(), book.getTitle(), reservationId));
    }

    public void assignReservation(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.setStatus(ReservationStatus.ASSIGNED);
            reservation.setAssignedTime(LocalDateTime.now());
            reservation.setUpdatedAt(LocalDateTime.now());
            reservationRepository.save(reservation);

            updateQueuePositions(reservation.getBookId());
        }
    }

    private void updateQueuePositions(Long bookId) {
        List<Reservation> pendingReservations = reservationRepository.findByBookIdAndStatus(bookId, ReservationStatus.PENDING);
        for (int i = 0; i < pendingReservations.size(); i++) {
            Reservation reservation = pendingReservations.get(i);
            reservation.setQueuePosition(i + 1);
            reservation.setUpdatedAt(LocalDateTime.now());
            reservationRepository.save(reservation);
        }
    }

    public List<Reservation> findByBookId(Long bookId) {
        return reservationRepository.findByBookId(bookId);
    }
}