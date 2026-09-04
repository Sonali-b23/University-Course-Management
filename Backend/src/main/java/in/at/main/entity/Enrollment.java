package in.at.main.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Links a User to a Course they've registered for. A unique constraint on
 * (user_id, course_id) makes double-enrolling structurally impossible at the
 * database level, not just checked in application code -- the service layer
 * also checks first (see EnrollmentServiceImpl) so a repeat attempt gets a
 * clean 409 instead of a raw constraint-violation error.
 */
@Entity
@Table(name = "enrollments", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "course_id" }))
public class Enrollment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	@Column(nullable = false)
	private Instant enrolledAt;

	public Enrollment() {
	}

	public Enrollment(User user, Course course) {
		this.user = user;
		this.course = course;
		this.enrolledAt = Instant.now();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Instant getEnrolledAt() {
		return enrolledAt;
	}

	public void setEnrolledAt(Instant enrolledAt) {
		this.enrolledAt = enrolledAt;
	}
}
