package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    public ItemDto toItemDto(Item item, Long userId, List<Comment> comments, BookingShortDto lastBooking, BookingShortDto nextBooking) {
        if (item == null) return new ItemDto();

        boolean isOwner = item.getOwner() != null && item.getOwner().getId().equals(userId);

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(isOwner ? lastBooking : null)
                .nextBooking(isOwner ? nextBooking : null)
                .comments(comments.stream()
                        .map(this::toCommentDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }
}
