package config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class WebInitializer implements WebApplicationInitializer,
    ServletContextListener {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private final GenericWebApplicationContext rootWebApplicationContext;

    public WebInitializer(ApplicationContext applicationContext) {
        rootWebApplicationContext = new GenericWebApplicationContext();
        rootWebApplicationContext.setParent(applicationContext);
        rootWebApplicationContext.refresh();
    }

    @Override
    public void onStartup(ServletContext servletContext)
        throws ServletException {

        servletContext.setAttribute(
            WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
            rootWebApplicationContext);

        addSpringDispatcherServlet(servletContext);
        addSpringSecurityFilter(servletContext);
    }

    private void addSpringDispatcherServlet(ServletContext sc) {
        AnnotationConfigWebApplicationContext mvcApplicationContext =
            new AnnotationConfigWebApplicationContext();
        mvcApplicationContext.register(MvcConfiguration.class);
        ServletRegistration.Dynamic dispatcherServlet =
            sc.addServlet(
                "spring-dispatcher",
                new DispatcherServlet(mvcApplicationContext));
        dispatcherServlet.setLoadOnStartup(1);
        dispatcherServlet.addMapping("/");
    }

    private void addSpringSecurityFilter(ServletContext sc) {
        sc.addFilter("springSecurityFilterChain",
            new DelegatingFilterProxy())
            .addMappingForUrlPatterns(null, false, "/*");
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            onStartup(servletContextEvent.getServletContext());
        } catch (ServletException e) {
            logger.error("Failed to initialized web application", e);
            System.exit(1);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}