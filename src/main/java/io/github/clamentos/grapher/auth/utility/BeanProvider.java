package io.github.clamentos.grapher.auth.utility;

///
import org.springframework.beans.BeansException;

///..
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

///..
import org.springframework.context.annotation.Configuration;

///
/**
 * <h3>Bean Provider</h3>
 * Exposes static methods to get Spring beans from non Spring managed classes.
*/

///
@Configuration

///
public class BeanProvider implements ApplicationContextAware {

    ///
    private static ApplicationContext applicationContext;

    ///
    /**
     * {@inheritDoc}
     * @implNote Sets the application context into {@code this}.
     * This method is called by Spring and should not be called from anywhere else.
    */
    @Override
    @SuppressWarnings("static-access")
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;
    }

    ///
    /**
     * Gets the Spring bean with the specified class.
     * @param beanType : The desired bean class.
     * @return The possibly {@code null} matching bean from the Spring application context.
    */
    public static <E> E getBean(Class<E> beanType) {

        if(applicationContext != null) return(applicationContext.getBean(beanType));
        return(null);
    }

    ///
}
