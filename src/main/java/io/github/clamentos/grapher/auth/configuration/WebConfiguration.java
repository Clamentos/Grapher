package io.github.clamentos.grapher.auth.configuration;

///
import io.github.clamentos.grapher.auth.business.services.TokenService;

///..
import io.github.clamentos.grapher.auth.web.interceptors.RequestInterceptor;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.context.annotation.Configuration;

///..
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

///
@Configuration
@EnableWebMvc

///
public class WebConfiguration implements WebMvcConfigurer {

    ///
    private final TokenService service;

    ///
    @Autowired
    public WebConfiguration(TokenService service) {

        this.service = service;
    }

    ///
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(new RequestInterceptor(service)).addPathPatterns("/**");
	}

    ///
}
