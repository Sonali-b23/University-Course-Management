package in.at.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import in.at.main.dao.CourseDao;
import in.at.main.dao.EnrollmentDao;
import in.at.main.dao.UserDao;
import in.at.main.entity.Course;
import in.at.main.entity.Enrollment;
import in.at.main.entity.Role;
import in.at.main.entity.User;
import in.at.main.exception.AlreadyEnrolledException;
import in.at.main.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

	@Mock
	private EnrollmentDao enrollmentDao;

	@Mock
	private UserDao userDao;

	@Mock
	private CourseDao courseDao;

	@InjectMocks
	private EnrollmentServiceImpl service;

	@Test
	void enrollingTwiceThrowsAlreadyEnrolledException() {
		when(enrollmentDao.existsByUser_UsernameAndCourse_Id("alice", 1L)).thenReturn(true);

		assertThrows(AlreadyEnrolledException.class, () -> service.enroll("alice", 1L));
	}

	@Test
	void enrollingInAMissingCourseThrowsResourceNotFoundException() {
		when(enrollmentDao.existsByUser_UsernameAndCourse_Id("alice", 99L)).thenReturn(false);
		when(userDao.findByUsername("alice")).thenReturn(Optional.of(new User("alice", "hash", Role.USER)));
		when(courseDao.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.enroll("alice", 99L));
	}

	@Test
	void enrollingSucceedsWhenNotAlreadyEnrolled() {
		User user = new User("alice", "hash", Role.USER);
		Course course = new Course(1L, "Java", "Basics");
		when(enrollmentDao.existsByUser_UsernameAndCourse_Id("alice", 1L)).thenReturn(false);
		when(userDao.findByUsername("alice")).thenReturn(Optional.of(user));
		when(courseDao.findById(1L)).thenReturn(Optional.of(course));
		when(enrollmentDao.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

		var response = service.enroll("alice", 1L);

		assertEquals("Java", response.getCourseTitle());
		assertEquals(1L, response.getCourseId());
	}

	@Test
	void droppingACourseYouAreNotEnrolledInThrowsResourceNotFoundException() {
		when(enrollmentDao.findByUser_UsernameAndCourse_Id("alice", 1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.drop("alice", 1L));
	}

	@Test
	void gettingEnrolledStudentsForAMissingCourseThrowsResourceNotFoundException() {
		when(courseDao.existsById(1L)).thenReturn(false);

		assertThrows(ResourceNotFoundException.class, () -> service.getEnrolledStudents(1L));
	}
}
