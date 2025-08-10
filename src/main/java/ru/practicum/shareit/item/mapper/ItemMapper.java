package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingShortDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    public ItemDto toItemDto(Item item, Long userId) {
        if (item == null) return new ItemDto();

        boolean isOwner = item.getOwner() != null && item.getOwner().getId().equals(userId);

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .requestId(item.getRequestId())
                .lastBooking(isOwner ? getLastBooking(item.getId()) : null)
                .nextBooking(isOwner ? getNextBooking(item.getId()) : null)
                .comments(commentRepository.findByItemId(item.getId()).stream()
                        .map(this::toCommentDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private BookingShortDto getLastBooking(Long itemId) {
        return bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now())
                .map(b -> new BookingShortDto(b.getId(), b.getBooker().getId()))
                .orElse(null);
    }

    private BookingShortDto getNextBooking(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now())
                .map(b -> new BookingShortDto(b.getId(), b.getBooker().getId()))
                .orElse(null);
    }

    private CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public Item toItem(ItemDto dto, User owner) {
        if (dto == null) return new Item();
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(owner)
                .requestId(dto.getRequestId())
                .build();
    }

}
