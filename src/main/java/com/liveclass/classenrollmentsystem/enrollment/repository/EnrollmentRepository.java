package com.liveclass.classenrollmentsystem.enrollment.repository;

import com.liveclass.classenrollmentsystem.enrollment.domain.Enrollment;
import com.liveclass.classenrollmentsystem.enrollment.domain.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // 중복 신청 방지용
    boolean existsByCourseIdAndStudentId(Long courseId, Long studentId);

    //대기열 승급 시 가장 먼저(오래 전에) 신청한 사람 1명 찾기
    Optional<Enrollment> findTopByCourseIdAndStatusOrderByCreatedAtAsc(Long courseId, EnrollmentStatus status);

    // 내 수강 신청 목록(페이지네이션 적용)
    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);

    // 강의별 수강생 목록 조회(크리에이터 전용, 페이지네이션 적용)
    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);
}
