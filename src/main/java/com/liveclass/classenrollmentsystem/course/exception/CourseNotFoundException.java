package com.liveclass.classenrollmentsystem.course.exception;

public class CourseNotFoundException extends  RuntimeException{
    public CourseNotFoundException(Long couseId){
        super("강의를 찾을 수 없습니다. ID : " +  couseId);
    }
}
