package in.at.main.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.at.main.dao.CourseDao;
import in.at.main.entity.Course;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseDao dao;

    public CourseServiceImpl() {
        // Constructor body remains unchanged as the commented code is untouched.
    }

    @Override
    public List<Course> getCourses() {
        return dao.findAll();
    }

    @Override
    public Course getCourse(long courseId) {
        return dao.findById(courseId)
                  .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
    }

    @Override
    @Transactional
    public Course addCourse(Course course) {
        dao.save(course);
        return course;
    }

    @Override
    @Transactional
    public Course updateCourse(Course course) {
        Course existingCourse = dao.findById(course.getId())
                                   .orElseThrow(() -> new RuntimeException("Course not found with ID: " + course.getId()));
        existingCourse.setTitle(course.getTitle());
        existingCourse.setDescription(course.getDescription());
        return dao.save(existingCourse);
    }

    @Override
    @Transactional
    public void deleteCourse(long courseId) {
        Course entity = dao.findById(courseId)
                           .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        dao.delete(entity);
    }
}
