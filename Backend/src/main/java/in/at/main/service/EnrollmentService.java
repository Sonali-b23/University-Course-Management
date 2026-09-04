package in.at.main.service;

import java.util.List;

import in.at.main.dto.EnrolledStudentResponse;
import in.at.main.dto.EnrollmentResponse;

public interface EnrollmentService {
	EnrollmentResponse enroll(String username, long courseId);

	void drop(String username, long courseId);

	List<EnrollmentResponse> getMyCourses(String username);

	List<EnrolledStudentResponse> getEnrolledStudents(long courseId);
}
