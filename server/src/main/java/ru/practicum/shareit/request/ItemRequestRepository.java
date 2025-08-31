package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long requesterId);

    @Query("SELECT r FROM ItemRequest r WHERE r.requester.id <> :requesterId ORDER BY r.created DESC")
    List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(@Param("requesterId") Long requesterId, Pageable pageable);

}



