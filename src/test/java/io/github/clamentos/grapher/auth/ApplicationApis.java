package io.github.clamentos.grapher.auth;

///
import org.springframework.mock.web.MockHttpServletResponse;

///..
import org.springframework.test.web.servlet.MockMvc;

///..
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

///.
import lombok.AllArgsConstructor;

///
@AllArgsConstructor

///
public final class ApplicationApis {

    ///
    private final MockMvc mockMvc;

    ///
    public MockHttpServletResponse register(String baseUrl, String sessionId, String json) throws Exception {

        var builder = MockMvcRequestBuilders.post(baseUrl + "/register").contentType("application/json").content(json);

        if(sessionId != null) builder.header("Authorization", sessionId);
        return(mockMvc.perform(builder).andReturn().getResponse());
    }

    ///..
    public MockHttpServletResponse login(String baseUrl, String json) throws Exception {

        var builder = MockMvcRequestBuilders.post(baseUrl + "/login").contentType("application/json").content(json);
        return(mockMvc.perform(builder).andReturn().getResponse());
    }

    ///..
    public MockHttpServletResponse logout(String baseUrl, String sessionId) throws Exception {

        var builder = MockMvcRequestBuilders.delete(baseUrl + "/logout").header("Cookie", "sessionIdCookie=" + sessionId);
        return(mockMvc.perform(builder).andReturn().getResponse());
    }

    ///..
    public MockHttpServletResponse logoutAll(String baseUrl, String sessionId) throws Exception {

        var builder = MockMvcRequestBuilders.delete(baseUrl + "/logout/all").header("Cookie", "sessionIdCookie=" + sessionId);
        return(mockMvc.perform(builder).andReturn().getResponse());
    }

    ///..
    public MockHttpServletResponse getById(String baseUrl, String sessionId, long id) throws Exception {

        var builder = MockMvcRequestBuilders.get(baseUrl + "/" + id).header("Cookie", "sessionIdCookie=" + sessionId);
        return(mockMvc.perform(builder).andReturn().getResponse());
    }

    ///..
    public MockHttpServletResponse getAllByFilter(String baseUrl, String sessionId, String json) throws Exception {

        var builder = MockMvcRequestBuilders

            .get(baseUrl + "/search")
            .header("Cookie", "sessionIdCookie=" + sessionId)
            .contentType("application/json")
            .content(json)
        ;

        return(mockMvc.perform(builder).andReturn().getResponse());
    }

    ///..
    public MockHttpServletResponse deleteUser(String baseUrl, String sessionId, long id) throws Exception {

        var builder = MockMvcRequestBuilders.delete(baseUrl + "/" + id).header("Cookie", "sessionIdCookie=" + sessionId);
        return(mockMvc.perform(builder).andReturn().getResponse());
    }

    ///..
    public MockHttpServletResponse getStatus(String baseUrl, String sessionId) throws Exception {

        var builder = MockMvcRequestBuilders.get(baseUrl + "/status").header("Cookie", "sessionIdCookie=" + sessionId);
        return(mockMvc.perform(builder).andReturn().getResponse());
    }

    ///
}
