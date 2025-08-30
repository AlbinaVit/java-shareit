package ru.practicum.shareit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GatewayConfiguration {

    @Bean
    public RestClient userRestClient(@Value("${shareit-server.url}/users") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public RestClient bookingRestClient(@Value("${shareit-server.url}/bookings") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public RestClient itemRestClient(@Value("${shareit-server.url}/items") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public RestClient itemRequestRestClient(@Value("${shareit-server.url}/requests") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
