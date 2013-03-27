package config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Controller;

/**
 * The root application context.
 * 
 * Beans can also be configured by XML in root-context.xml which is imported by this context class.
 * 
 * Component scanning is also done to pickup any components other than
 * 
 * @Controllers. @Controllers will be picked up by the SpringMVC context.
 */
@Configuration
@ImportResource({ "classpath:META-INF/spring/root-context.xml",
    "classpath:META-INF/spring/security.xml" })
@Import({ config.JettyConfiguration.class })
@ComponentScan(basePackages = { "edu.ualberta.med.biobank" },
    excludeFilters = { @ComponentScan.Filter(Controller.class) })
public class RootConfiguration {

}
