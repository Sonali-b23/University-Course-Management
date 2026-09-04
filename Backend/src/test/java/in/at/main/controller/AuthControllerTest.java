package in.at.main.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.at.main.dto.LoginRequest;
import in.at.main.dto.RegisterRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void registerCreatesAUserAndReturnsAToken() throws Exception {
		RegisterRequest request = new RegisterRequest("newstudent", "password123");

		mockMvc.perform(post("/auth/register")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.token").exists())
				.andExpect(jsonPath("$.username").value("newstudent"))
				.andExpect(jsonPath("$.role").value("USER"));
	}

	@Test
	void registeringADuplicateUsernameReturns409() throws Exception {
		RegisterRequest request = new RegisterRequest("duplicate1", "password123");

		mockMvc.perform(post("/auth/register")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)));

		mockMvc.perform(post("/auth/register")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isConflict());
	}

	@Test
	void registeringAShortPasswordFailsValidation() throws Exception {
		RegisterRequest request = new RegisterRequest("shortpw", "abc");

		mockMvc.perform(post("/auth/register")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.fieldErrors.password").exists());
	}

	@Test
	void loginWithCorrectPasswordReturnsAToken() throws Exception {
		RegisterRequest registerRequest = new RegisterRequest("loginuser", "password123");
		mockMvc.perform(post("/auth/register")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(registerRequest)));

		LoginRequest loginRequest = new LoginRequest("loginuser", "password123");
		mockMvc.perform(post("/auth/login")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}

	@Test
	void loginWithWrongPasswordReturns401() throws Exception {
		RegisterRequest registerRequest = new RegisterRequest("wrongpwuser", "password123");
		mockMvc.perform(post("/auth/register")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(registerRequest)));

		LoginRequest loginRequest = new LoginRequest("wrongpwuser", "not-the-right-password");
		mockMvc.perform(post("/auth/login")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isUnauthorized());
	}
}
