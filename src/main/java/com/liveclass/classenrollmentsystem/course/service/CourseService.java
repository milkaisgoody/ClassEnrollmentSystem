package com.liveclass.classenrollmentsystem.course.service;

import com.liveclass.classenrollmentsystem.course.domain.Course;
import com.liveclass.classenrollmentsystem.course.domain.CourseStatus;
import com.liveclass.classenrollmentsystem.course.dto.CourseCreateRequest;
import com.liveclass.classenrollmentsystem.course.dto.CourseResponse;
import com.liveclass.classenrollmentsystem.course.exception.CourseNotFoundException;
import com.liveclass.classenrollmentsystem.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class CourseService {
    private final CourseRepository courseRepository;

    @Transactional
    public Long createCourse(Long creatorId, CourseCreateRequest request) {
        Course course = Course.builder()
                .creatorId(creatorId)
                .title(request.title())
                .description(request.description())
                .price(request.price())
                .capacity(request.capacity())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();

        return courseRepository.save(course).getId();
    }

    @Transactional
    public void openCourse(Long courseId, Long requestUserId) {
        Course course = getCourseById(courseId);
        course.open(requestUserId);
    }

    @Transactional
    public void closeCourse(Long courseId, Long requestUserId) {
        Course course = getCourseById(courseId);
        course.close(requestUserId);
    }

    public Page<CourseResponse> getCourses(CourseStatus status, Pageable pageable) {
        Page<Course> courses = (status != null)
                ? courseRepository.findByStatus(status, pageable)
                : courseRepository.findAll(pageable);

        return courses.map(CourseResponse::from);
    }

    public CourseResponse getCourseDetail(Long courseId) {
        return CourseResponse.from(getCourseById(courseId));
    }

    private Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
    }
}
