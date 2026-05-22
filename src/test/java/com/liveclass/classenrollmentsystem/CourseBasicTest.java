package com.liveclass.classenrollmentsystem;

import com.liveclass.classenrollmentsystem.course.domain.Course;
import com.liveclass.classenrollmentsystem.course.domain.CourseStatus;
import com.liveclass.classenrollmentsystem.course.repository.CourseRepository;
import com.liveclass.classenrollmentsystem.course.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class CourseBasicTest { //강의 생성, 상태 변경, 권한 검증 등 Course 도메인에 대한 기본 로직 test
    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void 강의를_생성하면_기본_상태는_DRAFT이다() {

        Long creatorId = 1L;
        Course course = new Course(creatorId, "테스트 강의", "설명", 10000, 10,
                LocalDate.now(), LocalDate.now().plusDays(30));

        Course savedCourse = courseRepository.save(course);

        assertEquals(CourseStatus.DRAFT, savedCourse.getStatus());
    }

    @Test
    void 작성자_본인만_강의_상태를_OPEN으로_변경할_수_있다() {

        Long creatorId = 1L;
        Course course = new Course(creatorId, "테스트 강의", "설명", 10000, 10,
                LocalDate.now(), LocalDate.now().plusDays(30));
        Long courseId = courseRepository.save(course).getId();

        courseService.openCourse(courseId, creatorId);

        Course updatedCourse = courseRepository.findById(courseId).orElseThrow();
        assertEquals(CourseStatus.OPEN, updatedCourse.getStatus());
    }

    @Test
    void 작성자가_아닌_사람이_상태를_변경하려_하면_예외가_발생한다() {

        Long creatorId = 1L;
        Course course = new Course(creatorId, "테스트 강의", "설명", 10000, 10,
                LocalDate.now(), LocalDate.now().plusDays(30));
        Long courseId = courseRepository.save(course).getId();

        Long hackerId = 999L;

        assertThrows(IllegalArgumentException.class, () -> {
            courseService.openCourse(courseId, hackerId);
        });
    }
}
