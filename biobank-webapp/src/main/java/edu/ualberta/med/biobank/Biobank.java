package edu.ualberta.med.biobank;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

public class Biobank {
    private static final Logger log = LoggerFactory.getLogger(Biobank.class);

    public static void main(final String[] arguments) throws Exception {
        final DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath:biobank-spring.xml");

        final ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(dispatcherServlet), "/*");
        context.setErrorHandler(null); // use Spring exception handler(s)

        final Server server = new Server(8080);
        server.setHandler(context);
        server.start();
        server.join();
    }

}
