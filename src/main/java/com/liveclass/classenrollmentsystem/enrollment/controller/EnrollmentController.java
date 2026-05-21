package com.liveclass.classenrollmentsystem.enrollment.controller;

import com.liveclass.classenrollmentsystem.enrollment.domain.Enrollment;
import com.liveclass.classenrollmentsystem.enrollment.service.EnrollmentService;
import com.liveclass.classenrollmentsystem.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;

    // 수강 신청 (강의 상세 페이지에서 신청)
    @PostMapping("/courses/{courseId}/enrollments")
    @ResponseStatus(HttpStatus.CREATED)
    public Long enroll(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("courseId") Long courseId) {
        return enrollmentService.enroll(courseId, userId);
    }

    // 결제 확정
    @PatchMapping("/enrollments/{enrollmentId}/confirm")
    public void confirmPayment(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("enrollmentId") Long enrollmentId) {
        enrollmentService.confirmPayment(enrollmentId, userId);
    }

    // 수강 취소
    @PatchMapping("/enrollments/{enrollmentId}/cancel")
    public void cancelEnrollment(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("enrollmentId") Long enrollmentId) {
        enrollmentService.cancelEnrollment(enrollmentId, userId);
    }

    // 내 수강 신청 목록 조회 (페이지네이션 적용)
    @GetMapping("/enrollments/me")
    public Page<Enrollment> getMyEnrollments(
            @RequestHeader("X-User-Id") Long userId,
            Pageable pageable) {
        return enrollmentRepository.findByStudentId(userId, pageable);
    }

    // 강의별 수강생 목록 조회 (크리에이터 전용, 페이지네이션 적용)
    @GetMapping("/courses/{courseId}/enrollments")
    public Page<Enrollment> getCourseEnrollments(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("courseId") Long courseId,
            Pageable pageable) {
        // 실제로는 여기서 userId가 해당 courseId의 creatorId인지 검증하는 로직이 서비스 계층에 필요함
        return enrollmentRepository.findByCourseId(courseId, pageable);
    }
}
