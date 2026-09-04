package in.at.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * The request body for creating or updating a course. Deliberately has no
 * "id" field -- ids are always assigned by the database (see
 * entity.Course's @GeneratedValue), never accepted from the client. This is
 * the structural fix for the old "typing in an existing id silently
 * overwrites that course" bug: it's no longer possible to send one at all.
 */
public class CourseRequest {

	@NotBlank(message = "Title is required")
	@Size(max = 150, message = "Title must be at most 150 characters")
	private String title;

	@NotBlank(message = "Description is required")
	@Size(max = 2000, message = "Description must be at most 2000 characters")
	private String description;

	public CourseRequest() {
	}

	public CourseRequest(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
