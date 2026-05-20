package com.liveclass.classenrollmentsystem.course.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long creatorId; //test

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer currentEnrollment = 0; //초기 수강 인원 0으로 설정

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status;

    @Builder
    public Course(Long creatorId, String title, String description, Integer price, Integer capacity, LocalDate startDate, LocalDate endDate) {
        this.creatorId = creatorId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.capacity = capacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = CourseStatus.DRAFT; // 최초 생성 시 상태는 무조건 DRAFT
    }

    // --- 비즈니스 로직 (상태 전이) ---
    public void open(Long requestUserId) {
        verifyCreator(requestUserId);
        this.status = CourseStatus.OPEN;
    }

    public void close(Long requestUserId) {
        verifyCreator(requestUserId);
        this.status = CourseStatus.CLOSED;
    }

    private void verifyCreator(Long requestUserId) {
        if (!this.creatorId.equals(requestUserId)) {
            throw new IllegalArgumentException("해당 강의의 상태를 변경할 권한이 없습니다.");
        }
    }
}
