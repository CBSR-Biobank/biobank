package edu.ualberta.med.biobank.tools;

import java.io.File;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import edu.ualberta.med.biobank.common.util.StringUtil;

@SuppressWarnings("nls")
public class SessionProvider {

    private static final Logger log = LoggerFactory.getLogger(SessionProvider.class);

    public enum Mode {
        DEBUG,
        RUN;
    }

    private SessionFactory sessionFactory = null;

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
            System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
            InitialContext ic = new InitialContext();

            ic.createSubcontext("java:");

            String dbHost, dbName, dbUser, dbPassword;

            // system properties override db.properties file
            dbHost = System.getProperty("database.host", StringUtil.EMPTY_STRING);
            dbName = System.getProperty("database.name", StringUtil.EMPTY_STRING);
            dbUser = System.getProperty("database.username", StringUtil.EMPTY_STRING);
            dbPassword = System.getProperty("database.password", StringUtil.EMPTY_STRING);

            // attempt to read db.properties file
            String dbPropertiesFilename = System.getProperty("db.properties", "../../db.properties");
            File dbPropertiesFile = new File(dbPropertiesFilename);
            if (dbPropertiesFile.exists()) {
                Properties dbProperties = new Properties();
                dbProperties.load(new FileInputStream(dbPropertiesFile));

                if (dbHost.isEmpty()) {
                    dbHost = dbProperties.getProperty("database.host", "localhost");
                }

                if (dbName.isEmpty()) {
                    dbName = dbProperties.getProperty("database.name", "biobank");
                }
                if (dbUser.isEmpty()) {
                    dbUser = dbProperties.getProperty("database.username", "dummy");
                }
                if (dbPassword.isEmpty()) {
                    dbPassword = dbProperties.getProperty("database.password", "ozzy498");
                }
            }

            log.debug("dbName: {}, dbUser: {}", dbName, dbUser);
            String url = MessageFormat.format("jdbc:mysql://{0}:3306/{1}", dbHost, dbName);

            // Construct DataSource
            MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
            ds.setUrl(url);
            ds.setUser(dbUser);
            ds.setPassword(dbPassword);

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
