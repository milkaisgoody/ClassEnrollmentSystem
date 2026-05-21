package com.liveclass.classenrollmentsystem.enrollment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private Long studentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    private LocalDateTime paymentDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Enrollment(Long courseId, Long studentId, EnrollmentStatus status) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.status = status;
        this.createdAt = LocalDateTime.now(); // 대기열 순서를 위한 신청 시간 기록
    }

    public void confirm(Long requestUserId) {
        verifyStudent(requestUserId);
        this.status = EnrollmentStatus.CONFIRMED;
        this.paymentDate = LocalDateTime.now(); // 결제 시간 기록
    }

    public void cancel(Long requestUserId) {
        verifyStudent(requestUserId);

        // 결제 후 7일 이내 취소 가능 조건 체크
        if (this.status == EnrollmentStatus.CONFIRMED && this.paymentDate != null) {
            if (LocalDateTime.now().isAfter(this.paymentDate.plusDays(7))) {
                throw new IllegalStateException("결제 후 7일이 지나 취소할 수 없습니다.");
            }
        }
        this.status = EnrollmentStatus.CANCELLED;
    }

    public void promoteFromWaitlist() {
        if (this.status == EnrollmentStatus.WAITLISTED) {
            this.status = EnrollmentStatus.PENDING; // 결제 대기로 승급
        }
    }

    private void verifyStudent(Long requestUserId) {
        if (!this.studentId.equals(requestUserId)) {
            throw new IllegalArgumentException("자신의 수강 신청 내역만 제어할 수 있습니다.");
        }
    }
}
