package config;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Configure the embedded Jetty server and the SpringMVC dispatcher servlet.
 */
@Configuration
public class JettyConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public WebAppContext jettyWebAppContext() throws IOException {

        WebAppContext ctx = new WebAppContext();
        ctx.setContextPath("/");
        ctx.setWar(new ClassPathResource("webapp").getURI().toString());
        ctx.addEventListener(new WebInitializer(applicationContext));

        return ctx;
    }

    /**
     * Jetty Server bean.
     * 
     * Instantiate the Jetty server.
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server jettyServer() throws IOException {

        /* Create the server. */
        Server server = new Server(8080);
        server.setHandler(jettyWebAppContext());
        return server;
    }

}