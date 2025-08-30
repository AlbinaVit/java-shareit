package ru.practicum.shareit.request;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRepository itemRepository;

    public ItemRequestDto createRequest(Long userId, CreateItemRequestDto requestDto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        ItemRequest request = ItemRequest.builder()
                .description(requestDto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();

        ItemRequest savedRequest = requestRepository.save(request);
        return itemRequestMapper.toDto(savedRequest);
    }

    public List<ItemRequestDto> getUserRequests(Long userId) {
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequestId = itemRepository.findByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> itemRequestMapper.toDtoWithItems(request,
                        itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageable);

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequestId = itemRepository.findByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> itemRequestMapper.toDtoWithItems(request,
                        itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        List<Item> items = itemRepository.findByRequestId(requestId);

        return itemRequestMapper.toDtoWithItems(request, items);
    }

}
