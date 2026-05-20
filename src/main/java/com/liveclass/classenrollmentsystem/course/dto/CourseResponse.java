package com.liveclass.classenrollmentsystem.course.dto;

import com.liveclass.classenrollmentsystem.course.domain.Course;
import com.liveclass.classenrollmentsystem.course.domain.CourseStatus;
import java.time.LocalDate;

public record CourseResponse(
        Long id,
        String title,
        String description,
        Integer price,
        Integer capacity,
        Integer currentEnrollment,
        LocalDate startDate,
        LocalDate endDate,
        CourseStatus status
) {
    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getId(), course.getTitle(), course.getDescription(),
                course.getPrice(), course.getCapacity(), course.getCurrentEnrollment(),
                course.getStartDate(), course.getEndDate(), course.getStatus()
        );
    }
}
