package edu.ualberta.med.biobank.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionProvider {
    public enum Mode {
        DEBUG,
        RUN;
    }

    private final SessionFactory sessionFactory;

    public SessionProvider(Mode mode) {
        // configure() configures settings from hibernate.cfg.xml found into the
        // biobank-orm jar
        Configuration configuration = new Configuration().configure();

        if (mode == Mode.DEBUG) {
            configuration.setProperty("hibernate.show_sql", "true");
            configuration.setProperty("hibernate.format_sql", "true");
            configuration.setProperty("hibernate.use_sql_comments", "true");
        }

        sessionFactory = configuration.buildSessionFactory();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }
}
