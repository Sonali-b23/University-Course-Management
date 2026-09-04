package in.at.main.dto;

import jakarta.validation.constraints.NotNull;

/**
 * The request body for enrolling in a course. courseId is boxed (Long, not
 * long) specifically so a missing field fails @NotNull validation with a
 * clean 400 instead of silently defaulting to 0.
 */
public class EnrollmentRequest {

	@NotNull(message = "courseId is required")
	private Long courseId;

	public EnrollmentRequest() {
	}

	public EnrollmentRequest(Long courseId) {
		this.courseId = courseId;
	}

	public Long getCourseId() {
		return courseId;
	}

	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}
}
