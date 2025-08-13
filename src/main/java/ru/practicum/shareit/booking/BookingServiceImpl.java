package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(Long userId, BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getStart() == null || bookingRequestDto.getEnd() == null) {
            throw new ValidationException("Дата начала и окончания бронирования не могут быть null");
        }

        User booker = userService.findUserById(userId);

        Item item = itemService.findItemById(bookingRequestDto.getItemId());

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart()) ||
                bookingRequestDto.getEnd().isEqual(bookingRequestDto.getStart())) {
            throw new ValidationException("Дата окончания должна быть позже даты начала");
        }

        Booking booking = bookingMapper.toBooking(bookingRequestDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        ItemDto itemDto = itemService.getItemById(item.getId(), userId);
        UserDto userDto = userService.getById(userId);
        return bookingMapper.toBookingDto(savedBooking, itemDto, userDto);
    }

    @Override
    public BookingDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = findBookingById(bookingId);

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Бронирование не может быть подтверждено, так как его статус не WAITING");
        }

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Только владелец вещи может подтвердить бронирование");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        ItemDto itemDto = itemService.getItemById(updatedBooking.getItem().getId(), ownerId);
        UserDto userDto = userService.getById(updatedBooking.getBooker().getId());
        return bookingMapper.toBookingDto(updatedBooking, itemDto, userDto);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("У вас нет доступа к этому бронированию");
        }
        ItemDto itemDto = itemService.getItemById(booking.getItem().getId(), userId);
        UserDto userDto = userService.getById(booking.getBooker().getId());
        return bookingMapper.toBookingDto(booking, itemDto, userDto);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        userService.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = getBookingsByState(userId, state, now);
        return bookings.stream()
                .map(booking -> {
                    ItemDto itemDto = itemService.getItemById(booking.getItem().getId(), userId);
                    UserDto userDto = userService.getById(booking.getBooker().getId());
                    return bookingMapper.toBookingDto(booking, itemDto, userDto);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        userService.findUserById(ownerId);

        List<Item> ownerItems = itemService.findItemsByOwnerId(ownerId);
        if (ownerItems.isEmpty()) {
            throw new NotFoundException("У пользователя нет вещей для бронирования, ownerId = " + ownerId);
        }

        try {
            BookingStatus.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }

        List<Booking> bookings = getBookingsByOwnerState(ownerId, state, LocalDateTime.now());

        return bookings.stream()
                .map(booking -> {
                    ItemDto itemDto = itemService.getItemById(booking.getItem().getId(), ownerId);
                    UserDto userDto = userService.getById(booking.getBooker().getId());
                    return bookingMapper.toBookingDto(booking, itemDto, userDto);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено c bookingId = " + bookingId));
    }

    private List<Booking> getBookingsByState(Long userId, String state, LocalDateTime now) {
        switch (state.toUpperCase()) {
            case "CURRENT":
                return bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case "PAST":
                return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case "FUTURE":
                return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case "WAITING":
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            case "ALL":
            default:
                return bookingRepository.findByBookerIdOrderByStartDesc(userId);
        }
    }

    private List<Booking> getBookingsByOwnerState(Long ownerId, String state, LocalDateTime now) {
        switch (state.toUpperCase()) {
            case "CURRENT":
                return bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
            case "PAST":
                return bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            case "FUTURE":
                return bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case "WAITING":
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
            case "ALL":
            default:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
        }
    }
}
