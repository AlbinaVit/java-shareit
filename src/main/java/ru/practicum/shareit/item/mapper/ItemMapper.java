package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        if (item == null) return null;
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequestId()
                );
    }

    public Item toItem(ItemDto dto) {
        if (dto == null) return null;
        return new Item(
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                dto.getOwner(),
                dto.getRequestId()
        );
    }
}
