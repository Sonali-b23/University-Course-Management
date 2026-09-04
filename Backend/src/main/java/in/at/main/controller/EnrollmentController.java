package in.at.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.at.main.dto.EnrolledStudentResponse;
import in.at.main.dto.EnrollmentRequest;
import in.at.main.dto.EnrollmentResponse;
import in.at.main.service.EnrollmentService;
import jakarta.validation.Valid;

/**
 * Course registration: a logged-in user (any role) can enroll in and drop
 * courses, and see their own enrollments. Who's enrolled in a given course
 * is ADMIN-only (see SecurityConfig) -- a student's course list is their own
 * business, but a full roster is administrative information.
 */
@RestController
public class EnrollmentController {

	@Autowired
	private EnrollmentService service;

	@PostMapping("/enrollments")
	public ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollmentRequest request,
			Authentication authentication) {
		EnrollmentResponse response = service.enroll(authentication.getName(), request.getCourseId());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@DeleteMapping("/enrollments/{courseId}")
	public ResponseEntity<Void> drop(@PathVariable long courseId, Authentication authentication) {
		service.drop(authentication.getName(), courseId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/enrollments/me")
	public List<EnrollmentResponse> myCourses(Authentication authentication) {
		return service.getMyCourses(authentication.getName());
	}

	@GetMapping("/courses/{courseId}/enrollments")
	public List<EnrolledStudentResponse> enrolledStudents(@PathVariable long courseId) {
		return service.getEnrolledStudents(courseId);
	}
}
