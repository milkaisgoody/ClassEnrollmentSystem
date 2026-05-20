package com.liveclass.classenrollmentsystem.course.repository;

import com.liveclass.classenrollmentsystem.course.domain.Course;
import com.liveclass.classenrollmentsystem.course.domain.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CourseRepository extends JpaRepository<Course, Long>{
    // 상태별 강의 목록 조회를 위한 쿼리 메서드 (페이징 포함)
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);
}
