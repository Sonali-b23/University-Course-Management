package in.at.main.dto;

import java.time.Instant;

/** What an ADMIN sees when listing who's enrolled in a course. */
public class EnrolledStudentResponse {

	private long enrollmentId;
	private String username;
	private Instant enrolledAt;

	public EnrolledStudentResponse(long enrollmentId, String username, Instant enrolledAt) {
		this.enrollmentId = enrollmentId;
		this.username = username;
		this.enrolledAt = enrolledAt;
	}

	public long getEnrollmentId() {
		return enrollmentId;
	}

	public String getUsername() {
		return username;
	}

	public Instant getEnrolledAt() {
		return enrolledAt;
	}
}
