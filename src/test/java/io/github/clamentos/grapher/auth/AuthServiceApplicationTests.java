package io.github.clamentos.grapher.auth;

///
import com.fasterxml.jackson.core.type.TypeReference;

///..
import com.fasterxml.jackson.databind.ObjectMapper;

///.
import io.github.clamentos.grapher.auth.parameters.operation.CreateOperationInvalidDto;

///..
import io.github.clamentos.grapher.auth.parameters.user.LoginInvalidDto;
import io.github.clamentos.grapher.auth.parameters.user.ReadUsersParameters;
import io.github.clamentos.grapher.auth.parameters.user.RegisterInvalidDto;
import io.github.clamentos.grapher.auth.parameters.user.UpdateUserInvalidDto;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuthDto;

///.
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

///..
import org.junit.jupiter.params.ParameterizedTest;

///..
import org.junit.jupiter.params.provider.EnumSource;

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
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

///
@AutoConfigureMockMvc
@SpringBootTest
@Sql(scripts = {"/testing_schema.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application_test.properties")
@TestClassOrder(value = ClassOrderer.OrderAnnotation.class)

///
class AuthServiceApplicationTests {

	///
	private final ObjectMapper objectMapper;
	private final RequestBuilder builder;

	///..
	private String sessionId;

	///
	@Autowired
	public AuthServiceApplicationTests(ObjectMapper objectMapper, RequestBuilder builder) {

		this.objectMapper = objectMapper;
		this.builder = builder;
	}

	///
	@Nested @Order(1)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class UserTests {

		@Test @Order(1)
		public void loginSuccessful() throws Exception {

			MockHttpServletResponse response = builder
				.request(HttpMethod.POST, "/v1/grapher/user/login")
				.addHeader("Content-Type", "application/json")
				.body("{\"username\":\"TestingUser\",\"password\":\"password123\"}")
				.expectedStatus(200)
				.perform()
				.andCheck()
			.andReturn();

			sessionId = objectMapper.readValue(response.getContentAsString(), new TypeReference<AuthDto>() {}).getSessionId();
		}

		@Test @Order(2)
		public void loginUserNotFound() throws Exception {

			builder
				.request(HttpMethod.POST, "/v1/grapher/user/login")
				.addHeader("Content-Type", "application/json")
				.body("{\"username\":\"TestingUserNotFound\",\"password\":\"password123\"}")
				.expectedStatus(404)
				.perform()
			.andCheck();
		}

		@Test @Order(3)
		public void loginWrongPassword() throws Exception {

			builder
				.request(HttpMethod.POST, "/v1/grapher/user/login")
				.addHeader("Content-Type", "application/json")
				.body("{\"username\":\"TestingUser\",\"password\":\"password123321\"}")
				.expectedStatus(401)
				.perform()
			.andCheck();
		}

		@ParameterizedTest @Order(4)
		@EnumSource(LoginInvalidDto.class)
		public void loginInvalidDto(LoginInvalidDto dto) throws Exception {

			builder
				.request(HttpMethod.POST, "/v1/grapher/user/login")
				.addHeader("Content-Type", "application/json")
				.body(dto.getDto())
				.expectedStatus(400)
				.perform()
			.andCheck();
		}

		@Test @Order(5)
		public void registerSuccessful() throws Exception {

			builder
				.request(HttpMethod.POST, "/v1/grapher/user/register")
				.addHeader("Content-Type", "application/json")
				.body("{\"username\":\"TestingUser2\",\"password\":\"password123\",\"email\":\"testinguser2@nonexistent.com\"}")
				.expectedStatus(200)
				.perform()
			.andCheck();
		}

		@Test @Order(6)
		public void registerAlreadyExists() throws Exception {

			builder
				.request(HttpMethod.POST, "/v1/grapher/user/register")
				.addHeader("Content-Type", "application/json")
				.body("{\"username\":\"TestingUser\",\"password\":\"password123\",\"email\":\"testinguser@nonexistent.com\"}")
				.expectedStatus(409)
				.perform()
			.andCheck();
		}

		@ParameterizedTest @Order(7)
		@EnumSource(RegisterInvalidDto.class)
		public void registerInvalidDto(RegisterInvalidDto dto) throws Exception {

			builder
				.request(HttpMethod.POST, "/v1/grapher/user/register")
				.addHeader("Content-Type", "application/json")
				.body(dto.getDto())
				.expectedStatus(400)
				.perform()
			.andCheck();
		}

		@ParameterizedTest @Order(8)
		@EnumSource(ReadUsersParameters.class)
		public void readUsers(ReadUsersParameters parameters) throws Exception {

			builder
				.request(HttpMethod.GET, "/v1/grapher/user")
				.addHeader("Authorization", sessionId)
				.addParam("username", parameters.getUsername())
				.addParam("email", parameters.getEmail())
				.addParam("createdAtRange", parameters.getCreatedAtRange())
				.addParam("updatedAtRange", parameters.getUpdatedAtRange())
				.addParam("operations", parameters.getOperations())
				.expectedStatus(200)
				.perform()
			.andCheck();
		}

		@Test @Order(9)
		public void readUserDetails() throws Exception {

			builder
				.request(HttpMethod.GET, "/v1/grapher/user/1")
				.addHeader("Authorization", sessionId)
				.expectedStatus(200)
				.perform()
			.andCheck();
		}

		@Test @Order(10)
		public void readUserDetailsNotFound() throws Exception {

			builder
				.request(HttpMethod.GET, "/v1/grapher/user/99999")
				.addHeader("Authorization", sessionId)
				.expectedStatus(404)
				.perform()
			.andCheck();
		}

		@Test @Order(11)
		public void updateUserSuccessful() throws Exception {

			builder
				.request(HttpMethod.PATCH, "/v1/grapher/user")
				.addHeader("Authorization", sessionId)
				.addHeader("Content-Type", "application/json")
				.body("{\"id\":2,\"email\":\"testinguserpawnmod@nonexistent.com\"}")
				.expectedStatus(200)
				.perform()
			.andCheck();
		}

		@Test @Order(12)
		public void updateUserNotFound() throws Exception {

			builder
				.request(HttpMethod.PATCH, "/v1/grapher/user")
				.addHeader("Authorization", sessionId)
				.addHeader("Content-Type", "application/json")
				.body("{\"id\":99999,\"email\":\"testingusernotfound@nonexistent.com\"}")
				.expectedStatus(404)
				.perform()
			.andCheck();
		}

		@ParameterizedTest @Order(13)
		@EnumSource(UpdateUserInvalidDto.class)
		public void updateUserInvalidDto(UpdateUserInvalidDto dto) throws Exception {

			builder
				.request(HttpMethod.PATCH, "/v1/grapher/user")
				.addHeader("Authorization", sessionId)
				.addHeader("Content-Type", "application/json")
				.body(dto.getDto())
				.expectedStatus(400)
				.perform()
			.andCheck();
		}

		@Test @Order(14)
		public void deleteUserSuccessful() throws Exception {

			builder
				.request(HttpMethod.DELETE, "/v1/grapher/user/2")
				.addHeader("Authorization", sessionId)
				.expectedStatus(200)
				.perform()
			.andCheck();
		}

		@Test @Order(15)
		public void deleteUserNotFound() throws Exception {

			builder
				.request(HttpMethod.DELETE, "/v1/grapher/user/99999")
				.addHeader("Authorization", sessionId)
				.expectedStatus(404)
				.perform()
			.andCheck();
		}

		@Test @Order(16)
		public void logoutSuccessful() throws Exception {

			builder
				.request(HttpMethod.DELETE, "/v1/grapher/user/logout")
				.addHeader("Authorization", sessionId)
				.expectedStatus(200)
				.perform()
			.andCheck();
		}

		@Test @Order(17)
		public void readUserDetailsLoggedOut() throws Exception {

			builder
				.request(HttpMethod.GET, "/v1/grapher/user/1")
				.addHeader("Authorization", sessionId)
				.expectedStatus(401)
				.perform()
			.andCheck();

			MockHttpServletResponse response = builder
				.request(HttpMethod.POST, "/v1/grapher/user/login")
				.addHeader("Content-Type", "application/json")
				.body("{\"username\":\"TestingUser\",\"password\":\"password123\"}")
				.expectedStatus(200)
				.perform()
				.andCheck()
			.andReturn();

			sessionId = objectMapper.readValue(response.getContentAsString(), new TypeReference<AuthDto>(){}).getSessionId();
		}
	};

	///..
	@Nested @Order(2)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class OperationTests {

		@Test @Order(1)
		public void createOperationsSuccessful() throws Exception {

			builder
				.request(HttpMethod.POST, "/v1/grapher/operation")
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", sessionId)
				.body("[\"TEST-OP-1\",\"TEST-OP-2\",\"TEST-OP-3\",\"TEST-OP-4\",\"TEST-OP-5\",\"TEST-OP-6\"]")
				.expectedStatus(200)
				.perform()
			.andCheck();
		}

		@Test @Order(2)
		public void createOperationsAlreadyExist() throws Exception {

			builder
				.request(HttpMethod.POST, "/v1/grapher/operation")
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", sessionId)
				.body("[\"TEST-OP-7\",\"TEST-OP-8\",\"TEST-OP-9\",\"TEST-OP-10\",\"TEST-OP-11\",\"TEST-OP-1\"]")
				.expectedStatus(409)
				.perform()
			.andCheck();
		}

		@ParameterizedTest @Order(3)
		@EnumSource(CreateOperationInvalidDto.class)
		public void createOperationsInvalidDto(CreateOperationInvalidDto dto) throws Exception {

			builder
				.request(HttpMethod.POST, "/v1/grapher/operation")
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", sessionId)
				.body(dto.getDto())
				.expectedStatus(400)
				.perform()
			.andCheck();
		}

		@Test @Order(4)
		public void readOperations() throws Exception {

			builder
				.request(HttpMethod.GET, "/v1/grapher/operation")
				.addHeader("Authorization", sessionId)
				.expectedStatus(200)
				.perform()
			.andCheck();
		}

		@Test @Order(5)
		public void updateOperationsSuccessful() throws Exception {

			builder
				.request(HttpMethod.PATCH, "/v1/grapher/operation")
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", sessionId)
				.body("[{\"id\":16,\"name\":\"TEST-OP-1-MOD\"},{\"id\":17,\"name\":\"TEST-OP-2-MOD\"}]")
				.expectedStatus(200)
				.perform()
			.andCheck();
		}

		@Test @Order(6)
		public void updateOperationsNotFound() throws Exception {

			builder
				.request(HttpMethod.PATCH, "/v1/grapher/operation")
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", sessionId)
				.body("[{\"id\":999,\"name\":\"TEST-OP-NOT-EXISTS-MOD\"}]")
				.expectedStatus(404)
				.perform()
			.andCheck();
		}

		@Test @Order(7)
		public void updateOperationsAlreadyExists() throws Exception {

			builder
				.request(HttpMethod.PATCH, "/v1/grapher/operation")
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", sessionId)
				.body("[{\"id\":17,\"name\":\"TEST-OP-1-MOD\"}]")
				.expectedStatus(409)
				.perform()
			.andCheck();
		}

		@Test @Order(8)
		public void deleteOperationsSuccessful() throws Exception {

			builder
				.request(HttpMethod.DELETE, "/v1/grapher/operation")
				.addHeader("Authorization", sessionId)
				.addParam("ids", "16").addParam("ids", "17").addParam("ids", "18")
				.expectedStatus(200)
				.perform()
			.andCheck();
		}

		@Test @Order(9)
		public void deleteOperationsNotFound() throws Exception {

			builder
				.request(HttpMethod.DELETE, "/v1/grapher/operation")
				.addHeader("Authorization", sessionId)
				.addParam("ids", "999")
				.expectedStatus(404)
				.perform()
			.andCheck();
		}
	}

	///
}
