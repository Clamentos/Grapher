package io.github.clamentos.grapher.auth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
@SpringBootTest
@Sql(scripts = {"/testing_schema.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application_test.properties")
@TestClassOrder(value = ClassOrderer.OrderAnnotation.class)

public class AuthServiceApplicationTests {

    private final MockMvc mockMvc;
    private final String baseUrl;
    private final Map<String, String> dtos;

    @Autowired
    public AuthServiceApplicationTests(MockMvc mockMvc) throws IOException {

        this.mockMvc = mockMvc;

        baseUrl = "http://localhost:8080";
        dtos = new HashMap<>();

        List<Path> paths = Files

            .list(Paths.get("./src/test/resources/dtos"))
            .filter(path -> Files.isDirectory(path) == false)
            .toList()
        ;

        for(Path path : paths) {

            dtos.put(path.getFileName().toString(), Files.readString(path));
        }
    }

    @Nested
    @Order(1)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class UserTests {

        private final String path = "/grapher/v1/auth-service/user";

        @Test @Order(1)
        public void registerSuccessful() throws Exception {

            var response = mockMvc

                .perform(MockMvcRequestBuilders

                    .post(baseUrl + path + "/register")
                    .contentType("application/json")
                    .content(dtos.get("registerSuccessful.json"))
                )
                .andReturn()
                .getResponse()
            ;

            Assertions.assertEquals(200, response.getStatus(), "Response status");
        }

        @Test @Order(2) // Simply repeat the previous.
        public void registerAlreadyExists() throws Exception {

            var response = mockMvc

                .perform(MockMvcRequestBuilders

                    .post(baseUrl + path + "/register")
                    .contentType("application/json")
                    .content(dtos.get("registerSuccessful.json"))
                )
                .andReturn()
                .getResponse()
            ;

            Assertions.assertEquals(409, response.getStatus(), "Response status");
        }

        @ParameterizedTest @Order(3)
        @MethodSource("argsFor_registerInvalidDto")
        public void registerInvalidDto(String dtoName) throws Exception {

            var response = mockMvc

                .perform(MockMvcRequestBuilders

                    .post(baseUrl + path + "/register")
                    .contentType("application/json")
                    .content(dtos.get(dtoName))
                )
                .andReturn()
                .getResponse()
            ;

            Assertions.assertEquals(400, response.getStatus(), "Response status");
        }

        @Test @Order(4)
		public void loginSuccessful() throws Exception {

            var response = mockMvc
            
                .perform(MockMvcRequestBuilders

                    .post(baseUrl + path + "/login")
                    .contentType("application/json")
                    .content(dtos.get("loginSuccessful.json"))
                )
                .andReturn()
                .getResponse()
            ;

            Assertions.assertEquals(200, response.getStatus(), "Response status");
        }

        @Test @Order(5)
		public void loginUserNotFound() throws Exception {

            var response = mockMvc
            
                .perform(MockMvcRequestBuilders

                    .post(baseUrl + path + "/login")
                    .contentType("application/json")
                    .content(dtos.get("loginUserNotFound.json"))
                )
                .andReturn()
                .getResponse()
            ;

            Assertions.assertEquals(401, response.getStatus(), "Response status");
        }

        // login wrong password
        // login with invalid dto
        // login password expired
        // test too many failed logins
        // login locked user
        // test too many sessions

        // register other successful
        // register other invalid dto
        // register other already exists

        // logout successful
        // logout all
        // logout session not found

        // get user by id
        // get user by id not found

        // updates...
        // deletes...

        static Stream<Arguments> argsFor_registerInvalidDto() {

            return(Stream.of(

                Arguments.of("registerInvalidDto1.json")
                //... registerInvalidDto2
            ));
        }
    }

    // subscriptions...

    /* @Nested
    @Order(1)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class ObservabilityTests {

        private final String path = "/grapher/v1/auth-service/observability";

        // health check
        // status

        // get audits
        // get audits invalid dto
        // delete audits
        // get logs
        // get logs invalid dto
        // delete logs
    } */
}
