package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import ru.practicum.shareit.user.dto.UserGatewayDto;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserGatewayController {

    private final RestClient userRestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserGatewayDto userDto) {
        return userRestClient.post()
                .body(userDto)
                .retrieve()
                .toEntity(Object.class);
    }


    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(
            @PathVariable Long userId,
            @RequestBody UserGatewayDto userDto) {
        return userRestClient.patch()
                .uri("/{userId}", userId)
                .body(userDto)
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable Long userId) {
        return userRestClient.get()
                .uri("/{userId}", userId)
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userRestClient.get()
                .retrieve()
                .toEntity(Object.class);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        return userRestClient.delete()
                .uri("/{userId}", userId)
                .retrieve()
                .toEntity(Object.class);
    }
}
