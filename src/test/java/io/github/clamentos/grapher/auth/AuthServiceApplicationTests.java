package io.github.clamentos.grapher.auth;

///
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
import org.springframework.test.context.TestPropertySource;

///..
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

///
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application_test.properties")
@SpringBootTest(classes = AuthServiceApplication.class)
@AutoConfigureMockMvc
@Sql(scripts = {"/testing_schema.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)

///
class AuthServiceApplicationTests {

	///
	private final RequestBuilder builder;

	///
	@Autowired
	public AuthServiceApplicationTests(RequestBuilder builder) {

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

		builder
			.request(HttpMethod.POST, "/v1/grapher/user")
			.body("{\"username\":\"TestingUser\",\"password\":\"password123\"}")
			.expectedStatus(200)
		.perform();
	}

	///
}
