package com.md.ReactSpringbootWithNeo4j.controllers;

import com.md.ReactSpringbootWithNeo4j.models.Course;
import com.md.ReactSpringbootWithNeo4j.objects.CourseDTO;
import com.md.ReactSpringbootWithNeo4j.services.CourseEnrollmentService;
import com.md.ReactSpringbootWithNeo4j.services.CourseService;
import com.md.ReactSpringbootWithNeo4j.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    private final CourseService courseService;

    private final LessonService lessonService;

    private final CourseEnrollmentService courseEnrollmentService;

    @Autowired
    public CourseController(CourseService courseService, LessonService lessonService, CourseEnrollmentService courseEnrollmentService) {
        this.courseService = courseService;
        this.lessonService = lessonService;
        this.courseEnrollmentService = courseEnrollmentService;
    }

    @GetMapping("/")
    public ResponseEntity<List<CourseDTO>> courseIndex(Principal principal) {
        List<CourseDTO> responseCourses = courseService.getAllCourses().stream()
                .map(course -> {
                    CourseDTO responseCourse = new CourseDTO();
                    responseCourse.setIdentifier(course.getIdentifier());
                    responseCourse.setTitle(course.getTitle());
                    responseCourse.setTeacher(course.getTeacher());
                    responseCourse.setLesson(lessonService.getAllLessonByCourseIdentifier(course.getIdentifier()));

                    if(principal != null){
                        responseCourse.setEnrolled(courseEnrollmentService.getEnrollmentStatus(principal.getName(), course.getIdentifier()));
                    }
                    return responseCourse;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseCourses);
    }


    @GetMapping("/{identifier}")
    public ResponseEntity<CourseDTO> courseDetail(@PathVariable String identifier) {
        Course course = courseService.getCourseByIdentifier(identifier);

        CourseDTO responseCourse = new CourseDTO();
        responseCourse.setIdentifier(course.getIdentifier());
        responseCourse.setTitle(course.getTitle());
        responseCourse.setTeacher(course.getTeacher());
        responseCourse.setLesson(lessonService.getAllLessonByCourseIdentifier(course.getIdentifier()));

        return ResponseEntity.ok(responseCourse);
    }

}

