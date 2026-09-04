package in.at.main.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.at.main.dao.CourseDao;
import in.at.main.dao.EnrollmentDao;
import in.at.main.dao.UserDao;
import in.at.main.dto.EnrolledStudentResponse;
import in.at.main.dto.EnrollmentResponse;
import in.at.main.entity.Course;
import in.at.main.entity.Enrollment;
import in.at.main.entity.User;
import in.at.main.exception.AlreadyEnrolledException;
import in.at.main.exception.ResourceNotFoundException;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

	@Autowired
	private EnrollmentDao enrollmentDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CourseDao courseDao;

	@Override
	@Transactional
	public EnrollmentResponse enroll(String username, long courseId) {
		if (enrollmentDao.existsByUser_UsernameAndCourse_Id(username, courseId)) {
			throw new AlreadyEnrolledException("Already enrolled in course with ID: " + courseId);
		}

		// The username comes from the authenticated principal (see
		// EnrollmentController), so a missing User row here would mean the
		// token belongs to an account that no longer exists -- not a
		// client-correctable error, hence IllegalStateException rather than a
		// 404/409, same pattern AuthController uses for the equivalent case.
		User user = userDao.findByUsername(username)
				.orElseThrow(() -> new IllegalStateException(
						"Authenticated username has no matching user record: " + username));
		Course course = courseDao.findById(courseId)
				.orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

		Enrollment saved = enrollmentDao.save(new Enrollment(user, course));
		return toResponse(saved);
	}

	@Override
	@Transactional
	public void drop(String username, long courseId) {
		Enrollment enrollment = enrollmentDao.findByUser_UsernameAndCourse_Id(username, courseId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"You are not enrolled in course with ID: " + courseId));
		enrollmentDao.delete(enrollment);
	}

	@Override
	public List<EnrollmentResponse> getMyCourses(String username) {
		return enrollmentDao.findByUser_Username(username).stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	public List<EnrolledStudentResponse> getEnrolledStudents(long courseId) {
		if (!courseDao.existsById(courseId)) {
			throw new ResourceNotFoundException("Course not found with ID: " + courseId);
		}

		return enrollmentDao.findByCourse_Id(courseId).stream()
				.map(e -> new EnrolledStudentResponse(e.getId(), e.getUser().getUsername(), e.getEnrolledAt()))
				.toList();
	}

	private EnrollmentResponse toResponse(Enrollment e) {
		return new EnrollmentResponse(e.getId(), e.getCourse().getId(), e.getCourse().getTitle(), e.getEnrolledAt());
	}
}
