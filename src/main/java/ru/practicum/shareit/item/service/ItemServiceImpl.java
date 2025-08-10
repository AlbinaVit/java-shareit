package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User owner = userService.findUserById(userId);
        Item item = itemMapper.toItem(itemDto, owner);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toItemDto(savedItem, userId);
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
        return itemMapper.toItemDto(updatedItem, userId);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = findItemById(itemId);
        return itemMapper.toItemDto(item, userId);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        userService.findUserById(ownerId);

        List<Item> items = itemRepository.findByOwnerId(ownerId);
        return items.stream()
                .map(item -> itemMapper.toItemDto(item, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.searchAvailableItems(text);
        return items.stream()
                .map(item -> itemMapper.toItemDto(item, null))
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

}
