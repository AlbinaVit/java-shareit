package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface ItemService {

    ItemDto addItem(Long ownerId, ItemDto itemDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

    ItemDto getItemById(Long itemId, Long userId);

    ItemDto getItemByOwnerId(Long itemId, Long ownerId);

    List<ItemDto> getItemsByOwner(Long ownerId);

    List<ItemDto> searchItems(String text);

    Item findItemById(Long itemId);

    List<Item> findItemsByOwnerId(Long userId);
}
