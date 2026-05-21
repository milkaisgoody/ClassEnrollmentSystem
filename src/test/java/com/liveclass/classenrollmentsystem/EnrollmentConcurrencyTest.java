package com.liveclass.classenrollmentsystem;

import com.liveclass.classenrollmentsystem.course.domain.Course;
import com.liveclass.classenrollmentsystem.course.repository.CourseRepository;
import com.liveclass.classenrollmentsystem.enrollment.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class EnrollmentConcurrencyTest { //동시성 제어 test
    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    public void 수강신청_동시성_테스트() throws InterruptedException {
        // 1. 테스트용 강의 생성 (정원 딱 10명)
        Course course = new Course(
                1L, // creatorId
                "동시성 테스트 강의",
                "설명",
                10000,
                10, // capacity (정원 10명)
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
        courseRepository.save(course);

        // 강의 상태를 OPEN으로 변경하고 수강신청 가능하게 만들기
        course.open(1L);
        courseRepository.save(course);

        Long courseId = course.getId();

        // 2. 동시성 테스트 준비
        int threadCount = 100; // 100명의 학생이 동시에 클릭
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 3. 100개의 스레드가 동시에 enroll(수강신청) 메서드 호출
        for (int i = 0; i < threadCount; i++) {
            Long studentId = (long) i;
            executorService.submit(() -> {
                try {
                    enrollmentService.enroll(courseId, studentId);
                } catch (Exception e) {
                    // 정원이 초과되어 발생하는 예외(IllegalStateException) 등은 무시
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 100개의 요청이 모두 끝날 때까지 메인 스레드가 대기

        // 4. 검증 (Assertion) : 100명이 동시에 요청했지만 수강 인원은 정확히 10명이어야 함
        Course resultCourse = courseRepository.findById(courseId).orElseThrow();

        System.out.println("최종 수강 확정 인원: " + resultCourse.getCurrentEnrollment());
        assertEquals(10, resultCourse.getCurrentEnrollment());
    }
}
