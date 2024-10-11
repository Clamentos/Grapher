package io.github.clamentos.grapher.auth;

///
import com.fasterxml.jackson.core.JsonProcessingException;

///..
import com.fasterxml.jackson.core.type.TypeReference;

///..
import com.fasterxml.jackson.databind.ObjectMapper;

///.
import io.github.clamentos.grapher.auth.error.ErrorCode;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuthDto;
import io.github.clamentos.grapher.auth.web.dtos.ErrorDto;
import io.github.clamentos.grapher.auth.web.dtos.SubscriptionDto;
import io.github.clamentos.grapher.auth.web.dtos.UserDto;

///.
import java.io.IOException;

///..
import java.nio.file.Files;
import java.nio.file.Paths;

///..
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

///.
import org.junit.jupiter.api.Assertions;
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
import org.junit.jupiter.params.provider.ValueSource;

///..
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

///..
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

///..
import org.springframework.boot.test.context.SpringBootTest;

///..
import org.springframework.mock.web.MockHttpServletResponse;

///..
import org.springframework.test.context.TestPropertySource;

///..
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

///..
import org.springframework.test.web.servlet.MockMvc;

///..
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

///
@AutoConfigureMockMvc
@SpringBootTest
@Sql(scripts = {"/testing_schema.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application_test.properties")
@TestClassOrder(value = ClassOrderer.OrderAnnotation.class)

///
public class AuthServiceApplicationTests { // TODO: validate response payloads...

    ///
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    ///..
    private final int maxSessionsPerUser;

    ///..
    private final ApplicationApis applicationApis;
    private final String baseUrl;
    private final Map<String, List<Object>> requestBodies;
    private final Map<String, List<String>> sessionIds;

    ///..
    private final TypeReference<AuthDto> authDtoType;
    private final TypeReference<ErrorDto> errorDtoType;
    private final TypeReference<SubscriptionDto> subscriptionDtoType;
    private final TypeReference<UserDto> userDtoType;

    ///..
    private String tempSessionId;

    ///
    @Autowired
    public AuthServiceApplicationTests(

        MockMvc mockMvc,
        ObjectMapper objectMapper,
        @Value("${grapher-auth.maxSessionsPerUser}") int maxSessionsPerUser

    ) throws IOException {

        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.maxSessionsPerUser = maxSessionsPerUser;

        applicationApis = new ApplicationApis(mockMvc);
        baseUrl = "http://localhost:8080";

        requestBodies = objectMapper.readValue(

            Files.readString(Paths.get("./src/test/resources/requestBodies.json")),
            new TypeReference<Map<String, List<Object>>>(){}
        );

        sessionIds = new HashMap<>();

        authDtoType = new TypeReference<AuthDto>() {};
        errorDtoType = new TypeReference<ErrorDto>() {};
        subscriptionDtoType = new TypeReference<SubscriptionDto>() {};
        userDtoType = new TypeReference<UserDto>() {};
    }

    ///
    public String getBody(String testName) throws JsonProcessingException {

        return(getBody(testName, 0));
    }

    ///..
    public String getBody(String testName, int index) throws JsonProcessingException {

        return(objectMapper.writeValueAsString(requestBodies.get(testName).get(index)));
    }

    ///
    @Nested
    @Order(1)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class UserTests {

        private final String path = "/grapher/v1/auth-service/user";

        ///..
        @Test @Order(1)
        public void registerSuccessful() throws Exception {

            var response = applicationApis.register(baseUrl + path, null, getBody("registerSuccessful"));
            Assertions.assertEquals(200, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(2)
        public void registerAlreadyExists() throws Exception {

            String body = getBody("registerSuccessful");
            var response = applicationApis.register(baseUrl + path, null, body);

            Assertions.assertEquals(409, response.getStatus(), "Response status");

            ErrorDto dto = objectMapper.readValue(response.getContentAsString(), errorDtoType);
            String username = objectMapper.readValue(body, userDtoType).getUsername();

            Assertions.assertEquals(path + "/register", dto.getUrl(), "ErrorDto.url");
            Assertions.assertEquals(ErrorCode.USER_ALREADY_EXISTS, dto.getErrorCode(), "ErrorDto.errorCode");
            Assertions.assertEquals(username, dto.getMessageArguments().get(0), "ErrorDto.parameters[0]");
        }

        ///..
        @ParameterizedTest @Order(3)
        @ValueSource(ints = {0})
        public void registerInvalidDto(int index) throws Exception {

            var response = applicationApis.register(baseUrl + path, null, getBody("registerInvalidDto", index));
            Assertions.assertEquals(400, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(4)
		public void loginSuccessful() throws Exception {

            var response = applicationApis.login(baseUrl + path, getBody("loginSuccessful"));

            Assertions.assertEquals(200, response.getStatus(), "Response status");
            AuthDto dto = objectMapper.readValue(response.getContentAsString(), authDtoType);
            sessionIds.computeIfAbsent(dto.getUserDetails().getUsername(), k -> new ArrayList<>()).add(dto.getSessionId());
        }

        ///..
        @Test @Order(5)
		public void loginTooManySessions() throws Exception {

            MockHttpServletResponse response = null;

            for(int i = 0; i < maxSessionsPerUser - 1; i++) { // -1 from already logged in, -1 to stop 1 earlier.

                response = applicationApis.login(baseUrl + path, getBody("loginSuccessful"));

                Assertions.assertEquals(200, response.getStatus(), "Response status");
                AuthDto dto = objectMapper.readValue(response.getContentAsString(), authDtoType);
                sessionIds.computeIfAbsent(dto.getUserDetails().getUsername(), k -> new ArrayList<>()).add(dto.getSessionId());
            }

            response = applicationApis.login(baseUrl + path, getBody("loginSuccessful"));
            Assertions.assertEquals(403, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(6)
		public void loginUserNotFound() throws Exception {

            var response = applicationApis.login(baseUrl + path, getBody("loginUserNotFound"));
            Assertions.assertEquals(401, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(7)
		public void loginWrongPassword() throws Exception {

            var response = applicationApis.login(baseUrl + path, getBody("loginWrongPassword"));
            Assertions.assertEquals(401, response.getStatus(), "Response status");
        }

        ///..
        @ParameterizedTest @Order(8)
        @ValueSource(ints = {0})
        public void loginInvalidDto(int index) throws Exception {

            var response = applicationApis.login(baseUrl + path, getBody("loginInvalidDto", index));
            Assertions.assertEquals(400, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(9)
        public void logoutSuccessful() throws Exception {

            var response = applicationApis.logout(baseUrl + path, sessionIds.get("TestUser").get(0));

            Assertions.assertEquals(200, response.getStatus(), "Response status");
            tempSessionId = sessionIds.get("TestUser").get(0);
            sessionIds.get("TestUser").remove(0);
        }

        ///..
        @Test @Order(10)
        public void logoutAllSuccessful() throws Exception {

            var response = applicationApis.logoutAll(baseUrl + path, sessionIds.get("TestUser").get(0));

            Assertions.assertEquals(200, response.getStatus(), "Response status");
            sessionIds.remove("TestUser");
        }

        ///..
        @Test @Order(11)
        public void logoutSessionNotFound() throws Exception {

            var response = applicationApis.logout(baseUrl + path, tempSessionId);
            Assertions.assertEquals(401, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(12)
		public void loginFinal() throws Exception {

            var response = applicationApis.login(baseUrl + path, "{\"username\":\"TestAdminUser\",\"password\":\"Password123?!\"}");
            Assertions.assertEquals(200, response.getStatus(), "Response status");
            AuthDto dto = objectMapper.readValue(response.getContentAsString(), authDtoType);
            sessionIds.computeIfAbsent(dto.getUserDetails().getUsername(), k -> new ArrayList<>()).add(dto.getSessionId());

            var response2 = applicationApis.login(baseUrl + path, getBody("loginSuccessful"));
            Assertions.assertEquals(200, response2.getStatus(), "Response status");
            AuthDto dto2 = objectMapper.readValue(response2.getContentAsString(), authDtoType);
            sessionIds.computeIfAbsent(dto2.getUserDetails().getUsername(), k -> new ArrayList<>()).add(dto2.getSessionId());

            System.out.println("dbg map " + sessionIds.toString());
        }

        ///..
        // login password expired
        // test too many failed logins
        // login locked user

        ///..
        @Test @Order(13)
        public void registerOtherSuccessful() throws Exception {

            var response = applicationApis.register(

                baseUrl + path,
                sessionIds.get("TestAdminUser").get(0),
                getBody("registerOtherSuccessful")
            );

            Assertions.assertEquals(200, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(14)
        public void registerOtherForbidden() throws Exception {

            var response = applicationApis.register(

                baseUrl + path,
                sessionIds.get("TestUser").get(0),
                getBody("registerOtherSuccessful")
            );

            Assertions.assertEquals(403, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(15)
		public void getSelf() throws Exception {

            // "TestUser" will always have id = 2.
            var response = applicationApis.getById(baseUrl + path, sessionIds.get("TestUser").get(0), 2);
            Assertions.assertEquals(200, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(16)
		public void getUserNotFound() throws Exception {

            var response = applicationApis.getById(baseUrl + path, sessionIds.get("TestUser").get(0), 999);
            Assertions.assertEquals(404, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(17)
		public void getUserOtherPrivileged() throws Exception {

            // "TestAdminUser" will always have id = 1.
            var response = applicationApis.getById(baseUrl + path, sessionIds.get("TestAdminUser").get(0), 1);
            Assertions.assertEquals(200, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(18)
		public void getUserOtherUnprivileged() throws Exception {

            var response = applicationApis.getById(baseUrl + path, sessionIds.get("TestUser").get(0), 1);
            Assertions.assertEquals(200, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(19)
		public void searchUsers() throws Exception {

            var response = applicationApis.getAllByFilter(baseUrl + path, sessionIds.get("TestAdminUser").get(0), getBody("searchUsers"));
            Assertions.assertEquals(200, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(20)
		public void searchUsersDenied() throws Exception {

            var response = applicationApis.getAllByFilter(baseUrl + path, sessionIds.get("TestUser").get(0), getBody("searchUsers"));
            Assertions.assertEquals(403, response.getStatus(), "Response status");
        }

        ///..
        // updates...

        ///..
        @Test @Order(21)
        public void deleteOthersForbidden() throws Exception {

            // id 3 is always the "TestUser2"
            var response = applicationApis.deleteUser(baseUrl + path, sessionIds.get("TestUser").get(0), 3);
            Assertions.assertEquals(403, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(22)
        public void deleteOthersSuccessful() throws Exception {

            var response = applicationApis.deleteUser(baseUrl + path, sessionIds.get("TestAdminUser").get(0), 3);
            Assertions.assertEquals(200, response.getStatus(), "Response status");
        }

        // delete not found
    }

    ///
    // subscriptions...

    ///
    @Nested
    @Order(2)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class ObservabilityTests {

        private final String path = "/grapher/v1/auth-service/observability";

        @Test @Order(1)
		public void healthCheck() throws Exception {

            var response = mockMvc

                .perform(MockMvcRequestBuilders.get(baseUrl + path))
                .andReturn()
                .getResponse()
            ;

            Assertions.assertEquals(200, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(2)
		public void getStatus() throws Exception {

            var response = mockMvc

                .perform(MockMvcRequestBuilders

                    .get(baseUrl + path + "/status")
                    .header("Authorization", sessionIds.get("TestAdminUser").get(0))
                )
                .andReturn()
                .getResponse()
            ;

            Assertions.assertEquals(200, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(3)
		public void getStatusForbidden() throws Exception {

            var response = mockMvc

                .perform(MockMvcRequestBuilders

                    .get(baseUrl + path + "/status")
                    .header("Authorization", sessionIds.get("TestUser").get(0))
                )
                .andReturn()
                .getResponse()
            ;

            Assertions.assertEquals(403, response.getStatus(), "Response status");
        }

        // get audits
        // get audits invalid dto
        // delete audits
        // get logs
        // get logs invalid dto
        // delete logs
    }

    ///
}
