package ru.itis.postmangenerator.controller;

import org.springframework.web.bind.annotation.*;
import ru.itis.postmangenerator.dto.Event;
import ru.itis.postmangenerator.dto.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/events")
public class EventController {

    @RequestMapping(method = RequestMethod.GET)
    public UUID testMethod() {
        return UUID.randomUUID();
    }

    @PostMapping
    public UUID createEvent(@RequestBody Event event) {
        return null;
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable UUID id) {
        return null;
    }

    @GetMapping("/tasks/{userId}")
    public List<Task> getAllEventsByUserId(boolean isActive, @PathVariable String userId) {
        return null;
    }

    @PutMapping("/{id}")
    public Event updateEvent(UUID id, @RequestBody Event event) {
        return null;
    }

    @PutMapping("/{id}/end-date")
    public Event updateEventDates(@PathVariable UUID id, LocalDate startDate, LocalDate endDate) {
        return null;
    }

    @PutMapping("/status/{eventId}/{userId}")
    public UUID changeTaskInEvent(@PathVariable UUID eventId,
                                                   @PathVariable Long userId,
                                                   @RequestBody Task task) {
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteEventById(@PathVariable UUID id) {

    }
}
