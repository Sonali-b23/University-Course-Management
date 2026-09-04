package in.at.main.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String title;
	private String description;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public Course(long id, String title, String description) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
	}

	/**
	 * Used when creating a brand new course: the id is intentionally left at
	 * its default (0) so the database assigns it via @GeneratedValue -- the
	 * client never supplies an id for a new course. This is what closes the
	 * old bug where a manually-typed id that happened to match an existing
	 * course silently overwrote it.
	 */
	public Course(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public Course() {
		super();
	}

	@Override
	public String toString() {
		return "Course [id=" + id + ", title=" + title + ", description=" + description + "]";
	}

}
