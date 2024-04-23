package ru.itis.postmangenerator.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    private UUID id;

    @NotNull
    private Long executorId;

    @NotBlank
    private String email;

    private String fullName;

    private UUID photoId;

    private String position;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(min = 5, max = 1000)
    private String comment;

    private String title;

    private String description;
}
