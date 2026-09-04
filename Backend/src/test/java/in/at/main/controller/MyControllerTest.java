package in.at.main.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// Note: @MockBean is deprecated as of Spring Boot 3.4 in favor of
// org.springframework.test.context.bean.override.mockito.MockitoBean, but
// is still fully supported -- used here since this test suite could not be
// compiled/run in the environment that wrote it (see README's testing
// section), and @MockBean is the safer, longer-established choice to hand
//-verify against. Feel free to switch to @MockitoBean once you've
// confirmed the project builds.
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.at.main.dto.CourseRequest;
import in.at.main.entity.Course;
import in.at.main.exception.ResourceNotFoundException;
import in.at.main.security.CustomUserDetailsService;
import in.at.main.security.JsonAccessDeniedHandler;
import in.at.main.security.JsonAuthenticationEntryPoint;
import in.at.main.security.JwtService;
import in.at.main.service.CourseService;

// addFilters = false: this class tests MyController's own logic (mapping,
// status codes, validation) with the security filter chain disabled. Who's
// allowed to call which endpoint is a separate concern, covered against the
// real SecurityConfig by SecurityIntegrationTest instead. addFilters=false
// only stops the filter chain from *running* during requests -- @WebMvcTest
// still auto-detects the real SecurityConfig and eagerly builds its whole
// bean graph (SecurityFilterChain -> JwtAuthenticationFilter -> JwtService /
// CustomUserDetailsService, plus the entry point/access-denied handler) just
// to start the context, so those beans still need to exist. They're mocked
// here purely to satisfy that wiring -- none of them are ever exercised
// since the filters never run.
@WebMvcTest(MyController.class)
@AutoConfigureMockMvc(addFilters = false)
class MyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CourseService service;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private CustomUserDetailsService userDetailsService;

	@MockBean
	private JsonAuthenticationEntryPoint authenticationEntryPoint;

	@MockBean
	private JsonAccessDeniedHandler accessDeniedHandler;

	@Test
	void getCoursesReturnsList() throws Exception {
		when(service.getCourses()).thenReturn(List.of(new Course(1L, "Java", "Basics")));

		mockMvc.perform(get("/courses"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].title").value("Java"));
	}

	@Test
	void getCourseReturns404WhenMissing() throws Exception {
		when(service.getCourse(99L)).thenThrow(new ResourceNotFoundException("Course not found with ID: 99"));

		mockMvc.perform(get("/course/99"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Course not found with ID: 99"));
	}

	@Test
	void addCourseRejectsBlankTitle() throws Exception {
		CourseRequest invalid = new CourseRequest("", "Some description");

		mockMvc.perform(post("/courses")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(invalid)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.fieldErrors.title").exists());
	}

	@Test
	void addCourseReturns201OnSuccess() throws Exception {
		CourseRequest request = new CourseRequest("Java", "Basics");
		when(service.addCourse(any(CourseRequest.class))).thenReturn(new Course(1L, "Java", "Basics"));

		mockMvc.perform(post("/courses")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void deleteCourseReturns204() throws Exception {
		mockMvc.perform(delete("/courses/1"))
				.andExpect(status().isNoContent());
	}
}
