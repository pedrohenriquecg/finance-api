package com.pedro.financeapi;

import com.pedro.financeapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FinanceApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void shouldCreateUser() throws Exception {
		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "name": "Pedro",
								  "email": "pedro@email.com"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value("Pedro"))
				.andExpect(jsonPath("$.email").value("pedro@email.com"));
	}

	@Test
	void shouldRejectInvalidUser() throws Exception {
		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "name": "",
								  "email": "email-invalido"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.name").value("Name is required"))
				.andExpect(jsonPath("$.email").value("Email must be valid"));
	}

	@Test
	void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
		mockMvc.perform(get("/users/999"))
				.andExpect(status().isNotFound())
				.andExpect(content().string(containsString("User not found with id: 999")));
	}
}
