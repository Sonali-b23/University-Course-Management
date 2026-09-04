package in.at.main.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.at.main.dao.CourseDao;
import in.at.main.dto.CourseRequest;
import in.at.main.entity.Course;
import in.at.main.exception.ResourceNotFoundException;

@Service
public class CourseServiceImpl implements CourseService {

	@Autowired
	private CourseDao dao;

	@Override
	public List<Course> getCourses() {
		return dao.findAll();
	}

	@Override
	public Course getCourse(long courseId) {
		return dao.findById(courseId)
				.orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
	}

	@Override
	@Transactional
	public Course addCourse(CourseRequest request) {
		Course course = new Course(request.getTitle(), request.getDescription());
		return dao.save(course);
	}

	@Override
	@Transactional
	public Course updateCourse(long courseId, CourseRequest request) {
		Course existingCourse = dao.findById(courseId)
				.orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
		existingCourse.setTitle(request.getTitle());
		existingCourse.setDescription(request.getDescription());
		return dao.save(existingCourse);
	}

	@Override
	@Transactional
	public void deleteCourse(long courseId) {
		Course entity = dao.findById(courseId)
				.orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
		dao.delete(entity);
	}
}
