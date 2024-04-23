package ru.itis.postmangenerator.dto;

import lombok.*;
import ru.itis.postmangenerator.dto.enumeration.EventStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private UUID id;

    @NotNull
    private Long organizerId;

    private EventStatus status;

    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotEmpty
    private List<String> attachments;

    @NotEmpty
    private List<Task> tasks;

    private Task task;
}
