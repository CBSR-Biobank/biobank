package edu.ualberta.med.biobank.test.action;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;

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

    public Session openAutoCommitSession() {
        Session session = sessionFactory.openSession();
        session.setFlushMode(FlushMode.COMMIT);

        setAutoCommit(session, true);

        return session;
    }

    public void setAutoCommit(Session session, final boolean autoCommit) {
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                connection.setAutoCommit(autoCommit);
            }
        });
    }
}
