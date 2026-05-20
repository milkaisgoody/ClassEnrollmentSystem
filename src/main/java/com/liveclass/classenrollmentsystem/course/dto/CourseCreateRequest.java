package com.liveclass.classenrollmentsystem.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CourseCreateRequest (
    @NotBlank String title,
    String description,
    @NotNull @Min(0) Integer price,
    @NotNull @Min(1) Integer capacity,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate
){
}
