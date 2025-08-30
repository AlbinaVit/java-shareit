package ru.practicum.shareit.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({HttpClientErrorException.class, HttpServerErrorException.class})
    public ResponseEntity<Map<String, String>> adviceError(HttpStatusCodeException e) {
        try {
            String errorMessage = new ObjectMapper()
                    .readTree(e.getResponseBodyAsString())
                    .path("error")
                    .asText();
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", errorMessage));
        } catch (Exception ex) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getResponseBodyAsString()));
        }
    }
}
