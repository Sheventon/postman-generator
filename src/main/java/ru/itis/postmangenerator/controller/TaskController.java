package ru.itis.postmangenerator.controller;

import org.springframework.web.bind.annotation.*;
import ru.itis.postmangenerator.dto.Task;

import java.util.UUID;

@RestController
@RequestMapping("/api/v2/tasks")
public class TaskController {


    @GetMapping("/{id}")
    Task getTaskById(@PathVariable UUID id) {
        return null;
    }


    @PutMapping("/status/{id}")
    UUID updateTask(@PathVariable UUID id, @RequestBody Task task) {
        return null;
    }


    @DeleteMapping("/{id}")
    void deleteTaskById(@PathVariable UUID id) {

    }
}
