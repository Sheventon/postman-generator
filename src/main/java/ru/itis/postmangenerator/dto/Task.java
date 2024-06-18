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
    @Email
    private String emmail;

    private String fullName;

    private UUID photoId;

    @Pattern(regexp = "(\\d{3}[-]?){2}\\d{4}")
    private String phoneNumber;

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

    @Max(-10)
    @Negative
    private int integer;

    @Positive
    private double doubleValue;
}
