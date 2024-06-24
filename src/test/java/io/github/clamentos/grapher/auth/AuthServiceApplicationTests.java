package io.github.clamentos.grapher.auth;

///
import com.fasterxml.jackson.core.type.TypeReference;

///..
import com.fasterxml.jackson.databind.ObjectMapper;

///.
import io.github.clamentos.grapher.auth.web.dtos.AuthDto;

///.
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

///..
import org.springframework.boot.test.context.SpringBootTest;

///..
import org.springframework.http.HttpMethod;

///..
import org.springframework.mock.web.MockHttpServletResponse;

///..
import org.springframework.test.context.TestPropertySource;

///..
import org.springframework.test.context.jdbc.Sql;

///..
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

///
@AutoConfigureMockMvc
@SpringBootTest(classes = AuthServiceApplication.class)
@Sql(scripts = {"/testing_schema.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application_test.properties")

///
class AuthServiceApplicationTests {

	///
	private final ObjectMapper objectMapper;
	private final RequestBuilder builder;

	///..
	private String token;

	///
	@Autowired
	public AuthServiceApplicationTests(ObjectMapper objectMapper, RequestBuilder builder) {

		this.objectMapper = objectMapper;
		this.builder = builder;
	}

	///
	@BeforeEach
	public void resetBuilder() {

		builder.reset();
	}

	///..
	@Test @Order(1)
	public void testLogin() throws Exception {

		MockHttpServletResponse response = builder
			.request(HttpMethod.POST, "/v1/grapher/user/login")
			.addHeader("Content-Type", "application/json")
			.body("{\"username\":\"TestingUser\",\"password\":\"password123\"}")
			.expectedStatus(200)
			.perform()
			.andCheck()
		.andReturn();

		token = objectMapper.readValue(response.getContentAsString(), new TypeReference<AuthDto>() {}).getToken();
	}

	///..
	@Test @Order(2)
	public void testLoginNotFound() throws Exception {

		builder
			.request(HttpMethod.POST, "/v1/grapher/user/login")
			.addHeader("Content-Type", "application/json")
			.body("{\"username\":\"TestingUserNotFound\",\"password\":\"password123\"}")
			.expectedStatus(404)
			.perform()
		.andCheck();
	}

	///..
	@Test @Order(3)
	public void testLoginWrongPassword() throws Exception {

		builder
			.request(HttpMethod.POST, "/v1/grapher/user/login")
			.addHeader("Content-Type", "application/json")
			.body("{\"username\":\"TestingUser\",\"password\":\"password123321\"}")
			.expectedStatus(401)
			.perform()
		.andCheck();
	}

	///.
	/* @Test @Order(4)
	public void testCreateOperations() throws Exception {

		builder
			.request(HttpMethod.POST, "/v1/grapher/operation")
			.addHeader("Content-Type", "application/json")
			.body("[]")
			.expectedStatus(200)
			.perform()
		.andCheck();
	} */

	///
}
