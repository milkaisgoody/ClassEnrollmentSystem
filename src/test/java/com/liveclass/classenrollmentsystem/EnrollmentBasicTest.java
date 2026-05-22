package com.liveclass.classenrollmentsystem;

import com.liveclass.classenrollmentsystem.course.domain.Course;
import com.liveclass.classenrollmentsystem.course.repository.CourseRepository;
import com.liveclass.classenrollmentsystem.enrollment.domain.Enrollment;
import com.liveclass.classenrollmentsystem.enrollment.domain.EnrollmentStatus;
import com.liveclass.classenrollmentsystem.enrollment.repository.EnrollmentRepository;
import com.liveclass.classenrollmentsystem.enrollment.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class EnrollmentBasicTest { // 수강 신청, 대기열 진입, 취소 시 대기자 승급, 결제 확정, 취소 기한 예외 처리 등 Enrollment 도메인의 기본 로직 test
    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private Long courseId;

    @BeforeEach
    void setUp() {
        //테스트 직전에 정원이 2명인 오픈된 강의를 준비함
        Course course = new Course(1L, "기본 로직 테스트 강의", "설명", 10000, 2,
                LocalDate.now(), LocalDate.now().plusDays(30));
        courseRepository.save(course);
        course.open(1L);
        courseId = course.getId();
    }

    @Test
    void 수강신청_정원이_남아있으면_PENDING_상태로_저장된다() {
        Long studentId = 101L;
        Long enrollmentId = enrollmentService.enroll(courseId, studentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
        assertEquals(EnrollmentStatus.PENDING, enrollment.getStatus());

        Course course = courseRepository.findById(courseId).orElseThrow();
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void 정원이_초과된_상태에서_신청하면_대기열_WAITLISTED_상태가_된다() {
        enrollmentService.enroll(courseId, 101L);
        enrollmentService.enroll(courseId, 102L);

        Long waitlistedStudentId = 103L;
        Long enrollmentId = enrollmentService.enroll(courseId, waitlistedStudentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
        assertEquals(EnrollmentStatus.WAITLISTED, enrollment.getStatus());
    }

    @Test
    void 누군가_수강을_취소하면_대기열의_첫번째_학생이_PENDING으로_승급된다() {
        Long firstStudentEnrollmentId = enrollmentService.enroll(courseId, 101L);
        enrollmentService.enroll(courseId, 102L);
        Long waitlistedStudentEnrollmentId = enrollmentService.enroll(courseId, 103L);

        enrollmentService.cancelEnrollment(firstStudentEnrollmentId, 101L);

        Enrollment waitlistedEnrollment = enrollmentRepository.findById(waitlistedStudentEnrollmentId).orElseThrow();
        assertEquals(EnrollmentStatus.PENDING, waitlistedEnrollment.getStatus());
    }

    @Test
    void 결제_확정을_하면_상태가_CONFIRMED로_변경된다() {
        Long studentId = 101L;
        Long enrollmentId = enrollmentService.enroll(courseId, studentId);

        enrollmentService.confirmPayment(enrollmentId, studentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
        assertEquals(EnrollmentStatus.CONFIRMED, enrollment.getStatus());
    }

    @Test
    void 결제_후_7일이_지난_수강건을_취소하려하면_예외가_발생한다() throws Exception {
        Long studentId = 101L;
        Long enrollmentId = enrollmentService.enroll(courseId, studentId);
        enrollmentService.confirmPayment(enrollmentId, studentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();

        //강제로 결제일을 8일 전으로 조작
        java.lang.reflect.Field paymentDateField = Enrollment.class.getDeclaredField("paymentDate");
        paymentDateField.setAccessible(true);
        paymentDateField.set(enrollment, java.time.LocalDateTime.now().minusDays(8));
        enrollmentRepository.saveAndFlush(enrollment);

        assertThrows(IllegalStateException.class, () -> {
            enrollmentService.cancelEnrollment(enrollmentId, studentId);
        }, "결제 후 7일이 지나 취소할 수 없습니다.");
    }
}
