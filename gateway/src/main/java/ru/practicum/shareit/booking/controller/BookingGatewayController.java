package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
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
import org.springframework.web.client.RestClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingGatewayController {

    private final RestClient bookingRestClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingRequestDto bookingRequestDto) {

        return bookingRestClient.post()
                .header("X-Sharer-User-Id", userId.toString())
                .body(bookingRequestDto)
                .retrieve()
                .toEntity(Object.class);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {

        return bookingRestClient.patch()
                .uri("/{bookingId}?approved={approved}", bookingId, approved)
                .header("X-Sharer-User-Id", ownerId.toString())
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {

        return bookingRestClient.get()
                .uri("/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {

        return bookingRestClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("state", state).build())
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state) {

        return bookingRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/owner").queryParam("state", state).build())
                .header("X-Sharer-User-Id", ownerId.toString())
                .retrieve()
                .toEntity(Object.class);
    }
}
