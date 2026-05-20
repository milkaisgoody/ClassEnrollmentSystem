package com.liveclass.classenrollmentsystem.course.repository;

import com.liveclass.classenrollmentsystem.course.domain.Course;
import com.liveclass.classenrollmentsystem.course.domain.CourseStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>{
    // 상태별 강의 목록 조회를 위한 쿼리 메서드 (페이징 포함)
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);

    //동시성 제어를 위한 비관적 락 조회 쿼리
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    Optional<Course> findByIdWithPessimisticLock(@Param("id") Long id);
}
