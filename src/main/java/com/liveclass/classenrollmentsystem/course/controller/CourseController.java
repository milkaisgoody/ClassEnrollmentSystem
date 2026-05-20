package com.liveclass.classenrollmentsystem.course.controller;

import com.liveclass.classenrollmentsystem.course.domain.CourseStatus;
import com.liveclass.classenrollmentsystem.course.dto.CourseCreateRequest;
import com.liveclass.classenrollmentsystem.course.dto.CourseResponse;
import com.liveclass.classenrollmentsystem.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createCourse(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CourseCreateRequest request) {
        return courseService.createCourse(userId, request);
    }

    @PatchMapping("/{courseId}/open")
    public void openCourse(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable ("courseId") Long courseId) {
        courseService.openCourse(courseId, userId);
    }

    @PatchMapping("/{courseId}/close")
    public void closeCourse(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable ("courseId") Long courseId) {
        courseService.closeCourse(courseId, userId);
    }

    @GetMapping
    public Page<CourseResponse> getCourses(
            @RequestParam(name = "status", required = false) CourseStatus status,
            Pageable pageable) {
        return courseService.getCourses(status, pageable);
    }

    @GetMapping("/{courseId}")
    public CourseResponse getCourseDetail(@PathVariable("courseId") Long courseId) {
        return courseService.getCourseDetail(courseId);
    }
}
