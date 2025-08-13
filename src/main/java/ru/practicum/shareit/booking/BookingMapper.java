package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking, ItemDto itemDto, UserDto userDto) {
        if (booking == null) return null;
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemDto,
                userDto,
                booking.getStatus()
        );
    }

    public Booking toBooking(BookingRequestDto bookingRequestDto, Item item, User booker) {
        if (bookingRequestDto == null) return null;

        return Booking.builder()
                .id(bookingRequestDto.getId())
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(item)
                .booker(booker)
                .status(bookingRequestDto.getStatus())
                .build();
    }

}
