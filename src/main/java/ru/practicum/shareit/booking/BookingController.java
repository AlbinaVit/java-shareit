package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Создание бронирования: пользователь id = {}, запрос = {}", userId, bookingRequestDto);
        BookingDto createdBooking = bookingService.createBooking(userId, bookingRequestDto);
        log.info("Бронирование создано: {}", createdBooking);
        return ResponseEntity.ok(createdBooking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        log.info("Одобрение бронирования: владелец id = {}, бронирование id = {}, одобрено = {}", ownerId, bookingId, approved);
        BookingDto approvedBooking = bookingService.approveBooking(ownerId, bookingId, approved);
        log.info("Бронирование одобрено: {}", approvedBooking);
        return ResponseEntity.ok(approvedBooking);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        log.info("Получение бронирования по id: пользователь id = {}, бронирование id = {}", userId, bookingId);
        BookingDto booking = bookingService.getBookingById(userId, bookingId);
        log.info("Бронирование получено: {}", booking);
        return ResponseEntity.ok(booking);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получение бронирований пользователя: пользователь id = {}, состояние = {}", userId, state);
        List<BookingDto> bookings = bookingService.getUserBookings(userId, state);
        log.info("Бронирования пользователя получены: {}", bookings);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос бронирований владельца: ownerId={}, state={}", ownerId, state);
        List<BookingDto> bookings = bookingService.getOwnerBookings(ownerId, state);
        log.info("Найдено бронирований для владельца {}: {}", ownerId, bookings.size());
        return ResponseEntity.ok(bookings);
    }

}
