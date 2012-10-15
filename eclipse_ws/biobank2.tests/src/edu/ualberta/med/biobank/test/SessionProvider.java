package edu.ualberta.med.biobank.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

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
                "org.apache.naming.java.javaURLContextFactory");
            System.setProperty(Context.URL_PKG_PREFIXES,
                "org.apache.naming");
            InitialContext ic = new InitialContext();

            ic.createSubcontext("java:");
            
            Properties dbProperties = new Properties();
            dbProperties.load(new FileInputStream("../../db.properties"));

            String url = MessageFormat.format("jdbc:mysql://{0}:3306/{1}",
                dbProperties.getProperty("database.host"),
                dbProperties.getProperty("database.name"));
            
            // Construct DataSource
            MysqlConnectionPoolDataSource ds =
                new MysqlConnectionPoolDataSource();
            ds.setUrl(url);
            ds.setUser(dbProperties.getProperty("database.username"));
            ds.setPassword(dbProperties.getProperty("database.password"));

            ic.bind("java:/biobank", ds);
        } catch (NamingException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
