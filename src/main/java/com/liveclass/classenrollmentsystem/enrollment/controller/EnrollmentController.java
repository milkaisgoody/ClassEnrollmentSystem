package com.liveclass.classenrollmentsystem.enrollment.controller;

import com.liveclass.classenrollmentsystem.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

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
}
