package com.liveclass.classenrollmentsystem.enrollment.domain;

public enum EnrollmentStatus {
    PENDING, //결제대기
    CONFIRMED, //결제 완료 (수강 확정)
    CANCELLED, //취소됨
    WAITLISTED //대기열 등록됨 (정원 초과 시)
}
