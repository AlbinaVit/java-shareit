package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {

    public ItemRequestDto toDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(new ArrayList<>())
                .build();
    }

    public ItemRequestDto toDtoWithItems(ItemRequest request, List<Item> items) {
        List<ItemResponseDto> itemDtos = items.stream()
                .map(this::toItemResponseDto)
                .collect(Collectors.toList());

        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(itemDtos)
                .build();
    }

    public ItemResponseDto toItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

}
