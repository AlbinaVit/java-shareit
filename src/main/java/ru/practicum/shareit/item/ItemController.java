package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody @Valid ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи от пользователя с id = {}: {}", userId, itemDto);
       ItemDto createdItem = itemService.addItem(userId, itemDto);
        log.info("Вещь успешно добавлена: {}", createdItem);
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на обновление вещи с id = {} от пользователя с id = {}: {}", itemId, userId, itemDto);
        ItemDto updatedItem = itemService.updateItem(userId, itemId, itemDto);
        log.info("Вещь успешно обновлена: {}", updatedItem);
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        log.info("Получен запрос на получение вещи с itemId = {} от пользователя с userId = {}", itemId, userId);
        ItemDto item = itemService.getItemById(itemId);
        log.info("Вещь успешно получена: {}", item);
        return item;
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение всех вещей пользователя с id = {}", userId);
        List<ItemDto> items = itemService.getItemsByOwner(userId);
        log.info("Найдено {} вещей пользователя с id = {}", items.size(), userId);
        return items;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Получен запрос на поиск вещей по тексту: '{}'", text);
        List<ItemDto> items = itemService.searchItems(text);
        log.info("Найдено {} вещей по тексту '{}'", items.size(), text);
        return items;
    }
}
