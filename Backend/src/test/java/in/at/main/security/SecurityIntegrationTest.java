package in.at.main.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.at.main.dto.CourseRequest;

/**
 * Exercises the *real* SecurityConfig (not a mocked-out slice) against an
 * in-memory H2 database, to verify who is actually allowed to call which
 * endpoint -- this is the part MyControllerTest deliberately doesn't cover
 * (it disables the security filter chain to isolate controller behavior).
 * Each test rolls back its DB changes (@Transactional) so they don't leak
 * into one another.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void anyoneCanBrowseCourses() throws Exception {
		mockMvc.perform(get("/courses")).andExpect(status().isOk());
	}

	@Test
	void addingACourseWithNoTokenIsRejected() throws Exception {
		CourseRequest request = new CourseRequest("Java", "Basics");

		mockMvc.perform(post("/courses")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "student1", roles = "USER")
	void addingACourseAsAPlainUserIsForbidden() throws Exception {
		CourseRequest request = new CourseRequest("Java", "Basics");

		mockMvc.perform(post("/courses")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin1", roles = "ADMIN")
	void addingACourseAsAnAdminSucceeds() throws Exception {
		CourseRequest request = new CourseRequest("Java", "Basics");

		mockMvc.perform(post("/courses")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());
	}
}
