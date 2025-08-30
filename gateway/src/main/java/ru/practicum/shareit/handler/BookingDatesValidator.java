package ru.practicum.shareit.handler;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

public class BookingDatesValidator implements ConstraintValidator<ValidBookingDates, BookingRequestDto> {

    @Override
    public void initialize(ValidBookingDates constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingRequestDto bookingRequestDto, ConstraintValidatorContext context) {
        if (bookingRequestDto.getStart() == null || bookingRequestDto.getEnd() == null) {
            return true;
        }

        return bookingRequestDto.getEnd().isAfter(bookingRequestDto.getStart());
    }
}
