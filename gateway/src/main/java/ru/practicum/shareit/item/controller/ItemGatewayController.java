package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.item.dto.CommentGatewayDto;
import ru.practicum.shareit.item.dto.CreateItemGatewayDto;
import ru.practicum.shareit.item.dto.ItemGatewayDto;

import java.util.Collections;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemGatewayController {

    private final RestClient itemRestClient;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody CreateItemGatewayDto itemDto) {

        return itemRestClient.post()
                .header("X-Sharer-User-Id", userId.toString())
                .body(itemDto)
                .retrieve()
                .toEntity(Object.class);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemGatewayDto itemDto) {

        return itemRestClient.patch()
                .uri("/{itemId}", itemId)
                .header("X-Sharer-User-Id", userId.toString())
                .body(itemDto)
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId) {

        return itemRestClient.get()
                .uri("/{itemId}", itemId)
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return itemRestClient.get()
                .header("X-Sharer-User-Id", userId.toString())
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam String text) {

        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return itemRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search")
                        .queryParam("text", text)
                        .build())
                .retrieve()
                .toEntity(Object.class);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentGatewayDto commentDto) {

        return itemRestClient.post()
                .uri("/{itemId}/comment", itemId)
                .header("X-Sharer-User-Id", userId.toString())
                .body(commentDto)
                .retrieve()
                .toEntity(Object.class);
    }

}
