package in.at.main.dto;

import java.time.Instant;

/** What a student sees for one of their own enrollments. */
public class EnrollmentResponse {

	private long id;
	private long courseId;
	private String courseTitle;
	private Instant enrolledAt;

	public EnrollmentResponse(long id, long courseId, String courseTitle, Instant enrolledAt) {
		this.id = id;
		this.courseId = courseId;
		this.courseTitle = courseTitle;
		this.enrolledAt = enrolledAt;
	}

	public long getId() {
		return id;
	}

	public long getCourseId() {
		return courseId;
	}

	public String getCourseTitle() {
		return courseTitle;
	}

	public Instant getEnrolledAt() {
		return enrolledAt;
	}
}
