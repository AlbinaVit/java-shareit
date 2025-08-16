package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingShortDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User owner = userService.findUserById(userId);
        Item item = itemMapper.toItem(itemDto, owner);
        Item savedItem = itemRepository.save(item);
        List<Comment> comments = commentRepository.findByItemId(savedItem.getId());
        BookingShortDto lastBooking = getLastBooking(savedItem.getId());
        BookingShortDto nextBooking = getNextBooking(savedItem.getId());

        return itemMapper.toItemDto(savedItem, userId, comments, lastBooking, nextBooking);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = findItemById(itemId);

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Редактировать вещь может только владелец");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequestId() != null) {
            item.setRequestId(itemDto.getRequestId());
        }
        Item updatedItem = itemRepository.save(item);
        List<Comment> comments = commentRepository.findByItemId(updatedItem.getId());
        BookingShortDto lastBooking = getLastBooking(updatedItem.getId());
        BookingShortDto nextBooking = getNextBooking(updatedItem.getId());

        return itemMapper.toItemDto(updatedItem, userId, comments, lastBooking, nextBooking);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = findItemById(itemId);
        List<Comment> comments = commentRepository.findByItemId(itemId);
        BookingShortDto lastBooking = getLastBooking(itemId);
        BookingShortDto nextBooking = getNextBooking(itemId);

        return itemMapper.toItemDto(item, userId, comments, lastBooking, nextBooking);
    }

    @Override
    public ItemDto getItemByOwnerId(Long itemId, Long ownerId) {
        Item item = findItemById(itemId);
        return itemMapper.toItemDto(item, ownerId, Collections.emptyList(), null, null);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        userService.findUserById(ownerId);
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        if (items.isEmpty()) return Collections.emptyList();

        List<Long> itemIds = extractItemIds(items);
        Map<Long, List<Booking>> bookingsByItem = getBookingsMap(itemIds);
        Map<Long, List<Comment>> commentsByItem = getCommentsMap(itemIds);

        return items.stream()
                .map(item -> toItemDtoWithEnrichment(item, ownerId, bookingsByItem, commentsByItem))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();

        List<Item> items = itemRepository.searchAvailableItems(text);
        if (items.isEmpty()) return Collections.emptyList();

        Map<Long, List<Comment>> commentsByItem = getCommentsMap(extractItemIds(items));

        return items.stream()
                .map(item -> itemMapper.toItemDto(
                        item,
                        null,
                        commentsByItem.getOrDefault(item.getId(), Collections.emptyList()),
                        null,
                        null))
                .collect(Collectors.toList());
    }

    @Override
    public Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }

    @Override
    public List<Item> findItemsByOwnerId(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId);
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

    private ItemDto toItemDtoWithEnrichment(Item item, Long userId,
                                            Map<Long, List<Booking>> bookingsMap,
                                            Map<Long, List<Comment>> commentsMap) {
        List<Booking> itemBookings = bookingsMap.getOrDefault(item.getId(), Collections.emptyList());

        return itemMapper.toItemDto(
                item,
                userId,
                commentsMap.getOrDefault(item.getId(), Collections.emptyList()),
                findLastBooking(itemBookings),
                findNextBooking(itemBookings));
    }

    private BookingShortDto findLastBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd))
                .map(b -> new BookingShortDto(b.getId(), b.getBooker().getId()))
                .orElse(null);
    }

    private BookingShortDto findNextBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .map(b -> new BookingShortDto(b.getId(), b.getBooker().getId()))
                .orElse(null);
    }

    private Map<Long, List<Booking>> getBookingsMap(List<Long> itemIds) {
        return bookingRepository.findApprovedBookingsForItems(itemIds)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
    }

    private Map<Long, List<Comment>> getCommentsMap(List<Long> itemIds) {
        return commentRepository.findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));
    }

    private List<Long> extractItemIds(List<Item> items) {
        return items.stream().map(Item::getId).collect(Collectors.toList());
    }

}
