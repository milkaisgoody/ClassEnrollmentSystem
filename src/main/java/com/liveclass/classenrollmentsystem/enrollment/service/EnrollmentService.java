package com.liveclass.classenrollmentsystem.enrollment.service;

import com.liveclass.classenrollmentsystem.course.domain.Course;
import com.liveclass.classenrollmentsystem.course.domain.CourseStatus;
import com.liveclass.classenrollmentsystem.course.repository.CourseRepository;
import com.liveclass.classenrollmentsystem.enrollment.domain.Enrollment;
import com.liveclass.classenrollmentsystem.enrollment.domain.EnrollmentStatus;
import com.liveclass.classenrollmentsystem.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Long enroll(Long courseId, Long studentId) {
        // 1. 비관적 락으로 강의 데이터 획득
        Course course = courseRepository.findByIdWithPessimisticLock(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        if (course.getStatus() != CourseStatus.OPEN) {
            throw new IllegalStateException("현재 모집 중인 강의가 아닙니다.");
        }
        if (enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            throw new IllegalStateException("이미 신청한 강의입니다.");
        }

        EnrollmentStatus initialStatus = EnrollmentStatus.PENDING;

        // 2. 정원 확인 및 대기열 분기
        if (course.isFull()) {
            initialStatus = EnrollmentStatus.WAITLISTED; // 꽉 찼으면 대기열로
        } else {
            course.addEnrollment(); // 자리 있으면 수강 인원 1 증가
        }

        Enrollment enrollment = new Enrollment(courseId, studentId, initialStatus);
        return enrollmentRepository.save(enrollment).getId();
    }

    @Transactional
    public void confirmPayment(Long enrollmentId, Long studentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 신청 내역이 없습니다."));
        enrollment.confirm(studentId);
    }

    @Transactional
    public void cancelEnrollment(Long enrollmentId, Long studentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 신청 내역이 없습니다."));

        EnrollmentStatus previousStatus = enrollment.getStatus();

        // 1. 취소 기한 검증 및 상태 변경
        enrollment.cancel(studentId);

        // 2. 취소한 사람이 자리를 차지하고 있던 사람(PENDING, CONFIRMED)이라면 대기열 승급 진행
        if (previousStatus == EnrollmentStatus.PENDING || previousStatus == EnrollmentStatus.CONFIRMED) {
            // 빈자리가 생겼으므로 락을 걸고 강의 정보 획득
            Course course = courseRepository.findByIdWithPessimisticLock(enrollment.getCourseId())
                    .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

            // 대기열에서 가장 오래 기다린 사람 조회
            Optional<Enrollment> nextWaitlisted = enrollmentRepository
                    .findTopByCourseIdAndStatusOrderByCreatedAtAsc(course.getId(), EnrollmentStatus.WAITLISTED);

            if (nextWaitlisted.isPresent()) {
                // 대기자가 있으면 빈자리를 넘겨줌 (강의 인원 감소 안 함)
                nextWaitlisted.get().promoteFromWaitlist();
            } else {
                // 대기자가 없으면 찐으로 빈자리가 되므로 강의 인원 1 감소
                course.removeEnrollment();
            }
        }
    }
}
