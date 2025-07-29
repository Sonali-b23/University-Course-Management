package in.at.main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.at.main.entity.Course;
import in.at.main.service.CourseService;

@RestController
@CrossOrigin
public class MyController {
	
	@Autowired
	private CourseService service;
	
	
	@GetMapping("/home")
	public String home()
	{
		return "Welcome to courses application";
	}
	
	@GetMapping("/courses")
	public List<Course> getCourses()
	{
		return this.service.getCourses();
		
	}
	
	@GetMapping("/course/{courseId}")
	public Course getCourse(@PathVariable String courseId)
	{
		return this.service.getCourse(Long.parseLong(courseId));
	}
	
	@PostMapping(path="/courses", consumes="application/json")
	public Course addCourse(@RequestBody Course course)
	{
		return this.service.addCourse(course);
	}
	
	@PutMapping("/courses")
	public Course updateCourse(@RequestBody Course course)
	{
		return this.service.updateCourse(course);
	}
	
	@DeleteMapping("/courses/{courseId}")
	public ResponseEntity<HttpStatus> deleteCourse(@PathVariable String courseId)
	{
		try {
			this.service.deleteCourse(Long.parseLong(courseId));
			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch(Exception e)
		{
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
