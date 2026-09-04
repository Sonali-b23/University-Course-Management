package in.at.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.at.main.dto.CourseRequest;
import in.at.main.entity.Course;
import in.at.main.service.CourseService;
import jakarta.validation.Valid;

// CORS is configured centrally in security.SecurityConfig's
// CorsConfigurationSource bean, not per-controller here, so it applies
// consistently across every endpoint (including /auth/**).
@RestController
public class MyController {

	@Autowired
	private CourseService service;

	@GetMapping("/home")
	public String home() {
		return "Welcome to courses application";
	}

	@GetMapping("/courses")
	public List<Course> getCourses() {
		return this.service.getCourses();
	}

	@GetMapping("/course/{courseId}")
	public Course getCourse(@PathVariable long courseId) {
		return this.service.getCourse(courseId);
	}

	@PostMapping(path = "/courses", consumes = "application/json")
	public ResponseEntity<Course> addCourse(@Valid @RequestBody CourseRequest request) {
		Course created = this.service.addCourse(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/courses/{courseId}")
	public Course updateCourse(@PathVariable long courseId, @Valid @RequestBody CourseRequest request) {
		return this.service.updateCourse(courseId, request);
	}

	@DeleteMapping("/courses/{courseId}")
	public ResponseEntity<Void> deleteCourse(@PathVariable long courseId) {
		this.service.deleteCourse(courseId);
		return ResponseEntity.noContent().build();
	}
}
