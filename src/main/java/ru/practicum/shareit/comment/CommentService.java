package ru.practicum.shareit.comment;

public interface CommentService {

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
