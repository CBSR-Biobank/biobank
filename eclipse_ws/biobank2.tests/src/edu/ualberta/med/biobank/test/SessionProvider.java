package edu.ualberta.med.biobank.test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import com.mchange.v2.c3p0.mbean.C3P0PooledDataSource;

public class SessionProvider {
    public enum Mode {
        DEBUG,
        RUN;
    }

    private final SessionFactory sessionFactory;

    public SessionProvider(Mode mode) {
        setupDatasource();

        // configure() configures settings from hibernate.cfg.xml found into the
        // biobank-orm jar
        Configuration configuration = new Configuration().configure();

        configuration.setProperty(Environment.DRIVER, "com.mysql.jdbc.Driver");

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

    private void setupDatasource() {
        // rcarver - setup the jndi context and the datasource
        try {
            // Create initial context
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.fscontext.RefFSContextFactory");
            System.setProperty(Context.PROVIDER_URL,
                "file:///");
            InitialContext ic = new InitialContext();

            ic.createSubcontext("java:");

            // Construct DataSource
            C3P0PooledDataSource ds = new C3P0PooledDataSource();
            ds.setJdbcUrl("jdbc:mysql://localhost:3306/biobank");
            ds.setUser("dummy");
            ds.setPassword("ozzy498");

            /**
             * ZERO because when hibernate calls `result =
             * conn.prepareStatement( sql );` (in AbstractBatcher) a
             * PreparedStatement is returned by c3p0 that already has batches in
             * it, so, if it was executed immediately, it would already insert
             * rows, even though we never told it to add any, see this bug:
             * http://sourceforge.net/tracker/?func=detail&aid=3019762&group_id=
             * 25357&atid=383690 Possible fixes are for us to call clearBatch()
             * on the ps, or to use another connection pooler
             */
            ds.setMaxStatements(0);

            ic.bind("java:/biobank", ds);
        } catch (NamingException ex) {
            ex.printStackTrace();
        }
    }
}
