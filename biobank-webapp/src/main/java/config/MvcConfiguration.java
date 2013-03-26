package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * The SpringMVC application context.
 * 
 * This is the annotation variation of configuring the SpringMVC application context. An XML
 * configuration is imported so XML based configuration can still be used.
 * 
 * Any @Controller classes will be picked up by component scanning. All other components are ignored
 * as they will be picked up by the root application context.
 */
@EnableWebMvc
@Configuration
@ComponentScan(useDefaultFilters = false, basePackages = { "edu.ualberta.med.biobank.mvc" },
    includeFilters = { @ComponentScan.Filter(Controller.class) })
@ImportResource("classpath:META-INF/spring/servlet-context.xml")
public class MvcConfiguration extends WebMvcConfigurerAdapter {

    final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        /*
         * Server static resources from the src/main/resources/webapp/resources directory. Perhaps
         * rename resources to static.
         */
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
        registry.addResourceHandler("/img/**").addResourceLocations("/img/");
        registry.addResourceHandler("/js/**").addResourceLocations("/js/");

        /*
         * Favicon mapping.
         */
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/resources/favicon.ico");

    }

    @Bean
    public ServletContextTemplateResolver thymeleafTemplateResolver() {
        ServletContextTemplateResolver resolver = new ServletContextTemplateResolver();
        resolver.setPrefix("/WEB-INF/thymeleaf/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setCacheable(true);
        return resolver;
    }

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(thymeleafTemplateResolver());
        return engine;
    }

    @Bean
    public ThymeleafViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(thymeleafTemplateEngine());
        return resolver;
    }

}
