package in.at.main.service;

import java.util.List;

import in.at.main.dto.CourseRequest;
import in.at.main.entity.Course;

public interface CourseService {
	public List<Course> getCourses();
	public Course getCourse(long courseId);
	public Course addCourse(CourseRequest request);
	public Course updateCourse(long courseId, CourseRequest request);
	public void deleteCourse(long courseId);
}
