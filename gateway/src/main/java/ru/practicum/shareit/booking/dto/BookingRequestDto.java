package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.handler.ValidBookingDates;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidBookingDates(message = "Дата окончания должна быть позже даты начала бронирования")
public class BookingRequestDto {
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    @NotNull(message = "Дата начала бронирования обязательна")
    private LocalDateTime start;

    @Future(message = "Дата окончания бронирования должна быть в будущем")
    @NotNull(message = "Дата окончания бронирования обязательна")
    private LocalDateTime end;

    @NotNull(message = "Id вещи обязателен")
    private Long itemId;

    private Long booker;
    private BookingState status;
}
