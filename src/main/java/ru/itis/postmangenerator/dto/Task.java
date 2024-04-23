package ru.itis.postmangenerator.dto;

import lombok.*;

import javax.validation.constraints.*;
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

    @Min(2)
    @Max(100)
    private String position;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(min = 5, max = 1000)
    private String comment;

    private String title;

    @Min(20)
    @Max(200)
    private String description;

    @Max(10)
    private int integer;

    private double doubleValue;
}
