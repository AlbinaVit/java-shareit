package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import ru.practicum.shareit.request.dto.CreateItemRequestGatewayDto;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestGatewayController {

    private final RestClient itemRequestRestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody CreateItemRequestGatewayDto requestDto) {

        return itemRequestRestClient.post()
                .header("X-Sharer-User-Id", userId.toString())
                .body(requestDto)
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return itemRequestRestClient.get()
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        return itemRequestRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/all")
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {

        return itemRequestRestClient.get()
                .uri("/{requestId}", requestId)
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .toEntity(Object.class);
    }

}
