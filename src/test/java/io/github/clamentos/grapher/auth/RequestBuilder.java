package io.github.clamentos.grapher.auth;

///
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

///.
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

///.
import org.junit.jupiter.api.AssertionFailureBuilder;
import org.junit.jupiter.api.Assumptions;

///.
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;

///.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

///..
import org.springframework.http.HttpMethod;

///..
import org.springframework.mock.web.MockHttpServletResponse;

///..
import org.springframework.stereotype.Component;

///..
import org.springframework.test.web.servlet.MockMvc;

///..
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

///
@Component

///
public final class RequestBuilder {

    ///
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    ///..
    private MockHttpServletRequestBuilder builder;
    private MockHttpServletResponse response;
    private boolean skipFlag;

    ///..
    private int expectedStatus;
    private Map<String, Set<String>> expectedHeaders;
    private String expectedContent;

    ///
    @Autowired
    public RequestBuilder(MockMvc mockMvc, ObjectMapper objectMapper, @Value("${server.port}") int port) {

        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;

        baseUrl = "http://localhost:" + port;
    }

    ///
    public void reset() {

        builder = null;
        response = null;
        expectedStatus = 0;
        expectedHeaders = null;
        expectedContent = null;
    }

    ///..
    public RequestBuilder request(HttpMethod method, String path) throws TestAbortedException {

        Assumptions.assumeFalse(skipFlag , "Previous test(s) failed, aborting this test...");
        builder = MockMvcRequestBuilders.request(method, baseUrl + path);

        return(this);
    }

    ///..
    public RequestBuilder addHeader(String name, String value) {

        builder.header(name, value);
        return(this);
    }

    ///..
    public RequestBuilder addParam(String name, String value) {

        builder.param(name, value);
        return(this);
    }

    ///..
    public RequestBuilder body(String content) {

        builder.content(content);
        return(this);
    }

    ///..
    public RequestBuilder expectedStatus(int expectedStatus) {

        this.expectedStatus = expectedStatus;
        return(this);
    }

    ///..
    public RequestBuilder addExpectedHeader(String name, String... values) {

        if(expectedHeaders == null) {

            expectedHeaders = new HashMap<>();
        }

        Set<String> temp = new HashSet<>();

        for(String value : values) {

            temp.add(value);
        }

        expectedHeaders.put(name, temp);
        return(this);
    }

    ///..
    public RequestBuilder expectedBody(String expectedContent) {

        this.expectedContent = expectedContent;
        return(this);
    }

    ///..
    public RequestBuilder perform() throws Exception {

        response = mockMvc.perform(builder).andReturn().getResponse();
        return(this);
    }

    ///..
    public RequestBuilder andCheck() throws Exception {

        if(response.getStatus() != expectedStatus) {

			fail("Response status mismatch", expectedStatus, response.getStatus());
		}

		if(expectedHeaders != null && expectedHeaders.size() > 0) {

			for(Map.Entry<String, Set<String>> expectedHeader : expectedHeaders.entrySet()) {

				if(expectedHeader.getValue().contains(response.getHeader(expectedHeader.getKey())) == false) {

					fail(

						"Response header mismatch",
						expectedHeader.getValue() + " " + expectedHeader.getValue().toString(),
						expectedHeader.getKey() + " " + response.getHeader(expectedHeader.getKey())
					);
				}
			}
		}

		if(expectedContent != null) {

			JsonNode responseBody = objectMapper.readTree(response.getContentAsString());
			JsonNode expectedBody = objectMapper.readTree(expectedContent);

			if(responseBody.equals(expectedBody) == false) {

				fail("Response body mismatch", responseBody, expectedBody);
			}
		}

        return(this);
    }

    ///..
    public MockHttpServletResponse andReturn() {

        return(response);
    }

    ///.
    private void fail(String message, Object expected, Object actual) throws AssertionFailedError {

		skipFlag = true;

		AssertionFailureBuilder

			.assertionFailure()
			.message(message)
			.expected(expected)
			.actual(actual)
			.buildAndThrow()
		;
	}

    ///
}
