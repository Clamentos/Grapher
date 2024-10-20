package io.github.clamentos.grapher.auth;

///
import com.fasterxml.jackson.core.type.TypeReference;

///..
import com.fasterxml.jackson.databind.ObjectMapper;

///.
import io.github.clamentos.grapher.auth.error.ErrorCode;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuthDto;
import io.github.clamentos.grapher.auth.web.dtos.ErrorDto;
import io.github.clamentos.grapher.auth.web.dtos.UserDto;
import io.github.clamentos.grapher.auth.web.dtos.UsernamePasswordDto;

///.
import java.io.IOException;

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
import org.springframework.http.HttpStatus;

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

// TODO: validate response payloads...
// TODO: more tests

///
public class AuthServiceApplicationTests {

    ///
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    ///..
    private final int maxSessionsPerUser;

    ///..
    private final String baseUrl;
    private final ApplicationApis applicationApis;
    private final Map<String, List<String>> sessionIds;

    ///..
    private final TypeReference<AuthDto> authDtoType;
    private final TypeReference<ErrorDto> errorDtoType;
    // private final TypeReference<SubscriptionDto> subscriptionDtoType;
    private final TypeReference<UserDto> userDtoType;
    private final TypeReference<UsernamePasswordDto> usernamePasswordDtoType;

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

        baseUrl = "http://localhost:8080";
        applicationApis = new ApplicationApis(mockMvc);
        sessionIds = new HashMap<>();

        authDtoType = new TypeReference<AuthDto>() {};
        errorDtoType = new TypeReference<ErrorDto>() {};
        // subscriptionDtoType = new TypeReference<SubscriptionDto>() {};
        userDtoType = new TypeReference<UserDto>() {};
        usernamePasswordDtoType = new TypeReference<UsernamePasswordDto>() {};
    }

    ///
    public void checkErrorDto(ErrorDto errorDto, String expectedPath, ErrorCode expectedErrorCode, String... expectedArgs)
    throws Exception {

        Assertions.assertTrue(errorDto != null);
        Assertions.assertEquals(expectedPath, errorDto.getUrl());

        if(expectedErrorCode != null) Assertions.assertEquals(expectedErrorCode, errorDto.getErrorCode());
        else Assertions.assertTrue(errorDto.getErrorCode() != null);

        if(expectedArgs != null) {

            List<String> arguments = errorDto.getMessageArguments();

            Assertions.assertTrue(arguments != null);
            Assertions.assertEquals(expectedArgs.length, arguments.size());

            for(int i = 0; i < expectedArgs.length; i++) {

                Assertions.assertEquals(expectedArgs[i], arguments.get(i));
            }
        }
    }

    ///..
    public void checkErrorDto(ErrorDto errorDto, String path) throws Exception {

        checkErrorDto(errorDto, path, null, (String[])null);
    }

    ///..
    public AuthDto checkAuthDto(AuthDto authDto, String expectedUsername) throws Exception {

        Assertions.assertTrue(authDto != null);
        Assertions.assertTrue(authDto.getSessionId() != null);
        Assertions.assertTrue(authDto.getSessionId().equals("") == false);
        Assertions.assertTrue(authDto.getUserDetails() != null);
        Assertions.assertEquals(expectedUsername, authDto.getUserDetails().getUsername());

        return(authDto);
    }

    ///
    @Nested
    @Order(1)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
	class UserTests {

        ///..
        private final String url = baseUrl + "/grapher/v1/auth-service/user";

        private final Map<String, String> bodies = Map.ofEntries(

            Map.entry("registerSuccessful", "{\"username\":\"TestUser\",\"password\":\"Password123?!\",\"email\":\"TestUser@nonexistent.com\",\"about\":\"I'm a testing user !\"}"),

            Map.entry("registerInvalidDto_0", "{\"username\":null,\"password\":\"Password123?!\"}"),
            Map.entry("registerInvalidDto_1", "{\"username\":\"TestUser\",\"password\":null}"),
            Map.entry("registerInvalidDto_2", "{\"username\":\"TestUserNotExists\",\"password\":\"pwd1\"}"),

            Map.entry("loginSuccessful", "{\"username\":\"TestUser\",\"password\":\"Password123?!\"}"),
            Map.entry("loginUserNotFound", "{\"username\":\"TestUserNotExists\",\"password\":\"Password123?!\"}"),
            Map.entry("loginWrongPassword", "{\"username\":\"TestUser\",\"password\":\"Password124?!\"}"),

            Map.entry("loginInvalidDto_0", "{\"username\":null,\"password\":\"Password124?!\"}"),

            Map.entry("registerOtherSuccessful", "{\"username\":\"TestUser2\",\"password\":\"Password123?!\",\"email\":\"TestUser2@nonexistent.com\",\"about\":\"I'm a testing user 2 !\"}"),

            Map.entry("searchUsers", "{\"pageNumber\":0,\"pageSize\":10,\"usernameLike\":\"Test\"}")
        );

        ///..
        @Test @Order(1)
        public void registerSuccessful() throws Exception {

            MockHttpServletResponse response = applicationApis.register(url, null, bodies.get("registerSuccessful"));
            Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        }

        ///..
        @Test @Order(2)
        public void registerAlreadyExists() throws Exception {

            String body = bodies.get("registerSuccessful");
            MockHttpServletResponse response = applicationApis.register(url, null, body);
            Assertions.assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());

            checkErrorDto(

                objectMapper.readValue(response.getContentAsString(), errorDtoType),
                "/grapher/v1/auth-service/user/register",
                ErrorCode.USER_ALREADY_EXISTS,
                objectMapper.readValue(body, userDtoType).getUsername()
            );
        }

        ///..
        @ParameterizedTest @Order(3) @ValueSource(ints = {0, 1, 2})
        public void registerInvalidDto(int index) throws Exception {

            MockHttpServletResponse response = applicationApis.register(url, null, bodies.get("registerInvalidDto_" + index));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

            checkErrorDto(

                objectMapper.readValue(response.getContentAsString(), errorDtoType),
                "/grapher/v1/auth-service/user/register"
            );
        }

        ///..
        @Test @Order(4)
		public void loginSuccessful() throws Exception {

            String body = bodies.get("loginSuccessful");
            MockHttpServletResponse response = applicationApis.login(url, body);
            Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
            UsernamePasswordDto credentials = objectMapper.readValue(body, usernamePasswordDtoType);
            AuthDto dto = checkAuthDto(objectMapper.readValue(response.getContentAsString(), authDtoType), credentials.getUsername());
            sessionIds.computeIfAbsent(dto.getUserDetails().getUsername(), k -> new ArrayList<>()).add(dto.getSessionId());
        }

        ///..
        @Test @Order(5)
		public void loginTooManySessions() throws Exception {

            MockHttpServletResponse response = null;

            for(int i = 0; i < maxSessionsPerUser - 1; i++) {

                response = applicationApis.login(url, bodies.get("loginSuccessful"));
                Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
                AuthDto dto = objectMapper.readValue(response.getContentAsString(), authDtoType);
                sessionIds.computeIfAbsent(dto.getUserDetails().getUsername(), k -> new ArrayList<>()).add(dto.getSessionId());
            }

            response = applicationApis.login(url, bodies.get("loginSuccessful"));
            Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        }

        ///..
        @Test @Order(6)
		public void loginUserNotFound() throws Exception {

            String body = bodies.get("loginUserNotFound");
            MockHttpServletResponse response = applicationApis.login(url, body);
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

            checkErrorDto(

                objectMapper.readValue(response.getContentAsString(), errorDtoType),
                "/grapher/v1/auth-service/user/login",
                ErrorCode.USER_NOT_FOUND,
                objectMapper.readValue(body, usernamePasswordDtoType).getUsername()
            );
        }

        ///..
        @Test @Order(7)
		public void loginWrongPassword() throws Exception {

            String body = bodies.get("loginWrongPassword");
            MockHttpServletResponse response = applicationApis.login(url, body);
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

            checkErrorDto(

                objectMapper.readValue(response.getContentAsString(), errorDtoType),
                "/grapher/v1/auth-service/user/login",
                ErrorCode.WRONG_PASSWORD
            );
        }

        ///..
        @ParameterizedTest @Order(8) @ValueSource(ints = {0})
        public void loginInvalidDto(int index) throws Exception {

            MockHttpServletResponse response = applicationApis.login(url, bodies.get("loginInvalidDto_" + index));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

            checkErrorDto(

                objectMapper.readValue(response.getContentAsString(), errorDtoType),
                "/grapher/v1/auth-service/user/login"
            );
        }

        ///..
        @Test @Order(9)
        public void logoutSuccessful() throws Exception {

            MockHttpServletResponse response = applicationApis.logout(url, sessionIds.get("TestUser").get(0));
            Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
            tempSessionId = sessionIds.get("TestUser").get(0);
            sessionIds.get("TestUser").remove(0);
        }

        ///..
        @Test @Order(10)
        public void logoutAllSuccessful() throws Exception {

            var response = applicationApis.logoutAll(url, sessionIds.get("TestUser").get(0));
            Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
            sessionIds.remove("TestUser");
        }

        ///..
        @Test @Order(11)
        public void logoutSessionNotFound() throws Exception {

            var response = applicationApis.logout(url, tempSessionId);
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        }

        ///..
        @Test @Order(12)
		public void loginFinal() throws Exception {

            var response = applicationApis.login(url, "{\"username\":\"TestAdminUser\",\"password\":\"Password123?!\"}");
            Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
            AuthDto dto = objectMapper.readValue(response.getContentAsString(), authDtoType);
            sessionIds.computeIfAbsent(dto.getUserDetails().getUsername(), k -> new ArrayList<>()).add(dto.getSessionId());

            var response2 = applicationApis.login(url, bodies.get("loginSuccessful"));
            Assertions.assertEquals(HttpStatus.OK.value(), response2.getStatus());
            AuthDto dto2 = objectMapper.readValue(response2.getContentAsString(), authDtoType);
            sessionIds.computeIfAbsent(dto2.getUserDetails().getUsername(), k -> new ArrayList<>()).add(dto2.getSessionId());
        }

        ///..
        // login password expired
        // test too many failed logins
        // login locked user
        // test forgot password

        ///..
        @Test @Order(13)
        public void registerOtherSuccessful() throws Exception {

            var response = applicationApis.register(url, sessionIds.get("TestAdminUser").get(0), bodies.get("registerOtherSuccessful"));
            Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        }

        ///..
        @Test @Order(14)
        public void registerOtherForbidden() throws Exception {

            var response = applicationApis.register(url, sessionIds.get("TestUser").get(0), bodies.get("registerOtherSuccessful"));
            Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        }

        ///..
        @Test @Order(15)
		public void getSelf() throws Exception {

            // "TestUser" will always have id = 2.
            var response = applicationApis.getById(url, sessionIds.get("TestUser").get(0), 2L);
            Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        }

        ///..
        @Test @Order(16)
		public void getUserNotFound() throws Exception {

            var response = applicationApis.getById(url, sessionIds.get("TestUser").get(0), 99999L);
            Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        }

        ///..
        @Test @Order(17)
		public void getUserOtherPrivileged() throws Exception {

            // "TestAdminUser" will always have id = 1.
            var response = applicationApis.getById(url, sessionIds.get("TestAdminUser").get(0), 1);
            Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        }

        ///..
        @Test @Order(18)
		public void getUserOtherUnprivileged() throws Exception {

            // The dto returned has some information hidden away.
            var response = applicationApis.getById(url, sessionIds.get("TestUser").get(0), 1);
            Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        }

        ///..
        @Test @Order(19)
		public void searchUsers() throws Exception {

            var response = applicationApis.getAllByFilter(url, sessionIds.get("TestAdminUser").get(0), bodies.get("searchUsers"));
            Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        }

        ///..
        @Test @Order(20)
		public void searchUsersDenied() throws Exception {

            var response = applicationApis.getAllByFilter(url, sessionIds.get("TestUser").get(0), bodies.get("searchUsers"));
            Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        }

        ///..
        // updates...

        ///..
        @Test @Order(21)
        public void deleteOthersForbidden() throws Exception {

            // id 3 is always the "TestUser2".
            var response = applicationApis.deleteUser(url, sessionIds.get("TestUser").get(0), 3);
            Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        }

        ///..
        @Test @Order(22)
        public void deleteOthersSuccessful() throws Exception {

            var response = applicationApis.deleteUser(url, sessionIds.get("TestAdminUser").get(0), 3);
            Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        }

        ///..
        @Test @Order(23)
        public void deleteOthersNotFound() throws Exception {

            var response = applicationApis.deleteUser(url, sessionIds.get("TestAdminUser").get(0), 3);
            Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        }

        ///..
    }

    ///
    // subscriptions...

    ///
    @Nested
    @Order(2)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class ObservabilityTests {

        ///..
        private final String path = "/grapher/v1/auth-service/observability";

        ///..
        @Test @Order(1)
		public void healthCheck() throws Exception {

            var response = mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + path)).andReturn().getResponse();
            Assertions.assertEquals(200, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(2)
		public void getStatus() throws Exception {

            var response = applicationApis.getStatus(baseUrl + path, sessionIds.get("TestAdminUser").get(0));
            Assertions.assertEquals(200, response.getStatus(), "Response status");
        }

        ///..
        @Test @Order(3)
		public void getStatusForbidden() throws Exception {

            var response = applicationApis.getStatus(baseUrl + path, sessionIds.get("TestUser").get(0));
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
