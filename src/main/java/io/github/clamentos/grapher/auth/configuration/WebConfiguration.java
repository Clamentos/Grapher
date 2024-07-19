package io.github.clamentos.grapher.auth.configuration;

///
import io.github.clamentos.grapher.auth.business.services.SessionService;

///..
import io.github.clamentos.grapher.auth.messaging.Subscriber;

///..
import io.github.clamentos.grapher.auth.web.interceptors.RequestInterceptor;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.boot.LazyInitializationExcludeFilter;

///..
import org.springframework.context.annotation.Bean;
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
    private final SessionService service;

    ///
    @Autowired
    public WebConfiguration(SessionService service) {

        this.service = service;
    }

    ///
    @Bean
    public static LazyInitializationExcludeFilter lazyInitExcludeFilter() {

        return(LazyInitializationExcludeFilter.forBeanTypes(Subscriber.class));
    }

    ///
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(new RequestInterceptor(service)).addPathPatterns("/**");
	}

    ///
}
