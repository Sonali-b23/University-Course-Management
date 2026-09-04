package in.at.main.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.at.main.dao.CourseDao;
import in.at.main.dto.EnrollmentRequest;
import in.at.main.dto.RegisterRequest;
import in.at.main.entity.Course;

/**
 * Exercises course registration end to end against the real SecurityConfig
 * and an in-memory H2 database. Enrollment.enroll() looks the acting user up
 * by username (see EnrollmentServiceImpl), so tests that need a real
 * enrollment first register that user via /auth/register (creating the
 * actual User row) and then use @WithMockUser / the "user(...)" request
 * post-processor with the *same* username to simulate that user's
 * authenticated request -- @WithMockUser only populates the security
 * context, it doesn't touch the database on its own.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EnrollmentIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CourseDao courseDao;

	private void registerUser(String username) throws Exception {
		mockMvc.perform(post("/auth/register")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(new RegisterRequest(username, "password123"))));
	}

	@Test
	void enrollingWithoutATokenIsRejected() throws Exception {
		Course course = courseDao.save(new Course("Java", "Basics"));

		mockMvc.perform(post("/enrollments")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(new EnrollmentRequest(course.getId()))))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "student2", roles = "USER")
	void enrollingInACourseSucceeds() throws Exception {
		registerUser("student2");
		Course course = courseDao.save(new Course("Java", "Basics"));

		mockMvc.perform(post("/enrollments")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(new EnrollmentRequest(course.getId()))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.courseTitle").value("Java"));
	}

	@Test
	@WithMockUser(username = "student3", roles = "USER")
	void enrollingTwiceInTheSameCourseReturns409() throws Exception {
		registerUser("student3");
		Course course = courseDao.save(new Course("Java", "Basics"));
		String body = objectMapper.writeValueAsString(new EnrollmentRequest(course.getId()));

		mockMvc.perform(post("/enrollments").contentType("application/json").content(body));

		mockMvc.perform(post("/enrollments").contentType("application/json").content(body))
				.andExpect(status().isConflict());
	}

	@Test
	@WithMockUser(username = "student4", roles = "USER")
	void droppingACourseYouAreNotEnrolledInReturns404() throws Exception {
		Course course = courseDao.save(new Course("Java", "Basics"));

		mockMvc.perform(delete("/enrollments/" + course.getId()))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "student5", roles = "USER")
	void enrollingThenDroppingRemovesTheEnrollment() throws Exception {
		registerUser("student5");
		Course course = courseDao.save(new Course("Java", "Basics"));

		mockMvc.perform(post("/enrollments")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(new EnrollmentRequest(course.getId()))));

		mockMvc.perform(delete("/enrollments/" + course.getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/enrollments/me"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	@WithMockUser(username = "student6", roles = "USER")
	void myCoursesListsWhatYouAreEnrolledIn() throws Exception {
		registerUser("student6");
		Course course = courseDao.save(new Course("Java", "Basics"));

		mockMvc.perform(post("/enrollments")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(new EnrollmentRequest(course.getId()))));

		mockMvc.perform(get("/enrollments/me"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].courseTitle").value("Java"));
	}

	@Test
	@WithMockUser(username = "student7", roles = "USER")
	void plainUserCannotViewCourseRoster() throws Exception {
		Course course = courseDao.save(new Course("Java", "Basics"));

		mockMvc.perform(get("/courses/" + course.getId() + "/enrollments"))
				.andExpect(status().isForbidden());
	}

	@Test
	void adminCanViewCourseRoster() throws Exception {
		registerUser("student8");
		Course course = courseDao.save(new Course("Java", "Basics"));

		mockMvc.perform(post("/enrollments")
						.with(user("student8").roles("USER"))
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(new EnrollmentRequest(course.getId()))))
				.andExpect(status().isCreated());

		// Reuses the default admin account AdminSeeder creates on startup
		// (see config.AdminSeeder) rather than registering a new one.
		mockMvc.perform(get("/courses/" + course.getId() + "/enrollments")
						.with(user("admin").roles("ADMIN")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].username").value("student8"));
	}
}
