package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingGatewayShortDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemGatewayDto {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Статус доступности не может быть null")
    private Boolean available;
    private Long ownerId;
    private Long requestId;
    private BookingGatewayShortDto lastBooking;
    private BookingGatewayShortDto nextBooking;
    private List<CommentGatewayDto> comments;

}
