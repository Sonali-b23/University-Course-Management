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
import in.at.main.dto.CourseRequest;
import in.at.main.entity.Course;
import in.at.main.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

	@Mock
	private CourseDao dao;

	@InjectMocks
	private CourseServiceImpl service;

	@Test
	void getCourseThrowsResourceNotFoundExceptionWhenMissing() {
		when(dao.findById(99L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> service.getCourse(99L));
	}

	@Test
	void addCourseNeverSetsAnIdFromClientInput() {
		CourseRequest request = new CourseRequest("Intro to Java", "Basics of Java");
		when(dao.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Course result = service.addCourse(request);

		assertEquals("Intro to Java", result.getTitle());
		assertEquals(0, result.getId(),
				"id should be left at its default (0) for the DB to auto-generate -- never client-supplied");
	}

	@Test
	void updateCourseThrowsResourceNotFoundExceptionWhenMissing() {
		when(dao.findById(5L)).thenReturn(Optional.empty());
		CourseRequest request = new CourseRequest("New title", "New description");
		assertThrows(ResourceNotFoundException.class, () -> service.updateCourse(5L, request));
	}

	@Test
	void deleteCourseThrowsResourceNotFoundExceptionWhenMissing() {
		when(dao.findById(7L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> service.deleteCourse(7L));
	}
}
