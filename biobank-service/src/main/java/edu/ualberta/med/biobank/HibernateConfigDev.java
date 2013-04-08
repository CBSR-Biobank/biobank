package edu.ualberta.med.biobank;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import edu.ualberta.med.biobank.dao.StudyDao;
import edu.ualberta.med.biobank.dao.UserDao;
import edu.ualberta.med.biobank.dao.hibernate.StudyDaoHibernate;
import edu.ualberta.med.biobank.dao.hibernate.UserDaoHibernate;
import edu.ualberta.med.biobank.model.context.ExecutingUser;

/**
 * 
 * @author Jonathan Ferland
 * @see http ://www.petrikainulainen.net/programming/spring-framework/spring-data-
 *      jpa-tutorial-part-one-configuration/
 * @see http ://www.baeldung.com/2011/12/02/the-persistence-layer-with-spring-3-1- and-hibernate/
 */
@Configuration
@EnableTransactionManagement
@Profile("dev")
public class HibernateConfigDev {

    @Bean
    public DataSource dataSource() {
        // EmbeddedDatabaseFactoryBean bean = new EmbeddedDatabaseFactoryBean();
        // ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        // databasePopulator.addScript(new ClassPathResource("schema.sql"));
        // bean.setDatabasePopulator(databasePopulator);
        //
        // /* necessary because EmbeddedDatabaseFactoryBean is a FactoryBean */
        // bean.afterPropertiesSet();
        //
        // return bean.getObject();

        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        builder.setType(EmbeddedDatabaseType.H2);
        builder.setName("test");
        builder.addScript("classpath:h2-schema.sql");
        builder.addScript("classpath:test-data.sql");
        return builder.build();
    }

    @Bean
    public LocalSessionFactoryBean sessionFactoryBean() {
        LocalSessionFactoryBean result = new LocalSessionFactoryBean();
        result.setDataSource(dataSource());
        result.setAnnotatedPackages(new String[] { "edu.ualberta.med.biobank.model" });
        result.setPackagesToScan(new String[] { "edu.ualberta.med.biobank.model" });

        Properties properties = new Properties();
        properties.setProperty("connection.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        // properties.setProperty("hibernate.hbm2ddl.auto", "create");

        result.setHibernateProperties(properties);
        return result;
    }

    @Bean
    public SessionFactory sessionFactory() {
        return sessionFactoryBean().getObject();
    }

    @Bean
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager man = new HibernateTransactionManager();
        man.setSessionFactory(sessionFactory());
        return man;
    }

    @Bean
    public ExecutingUser executingUser() {
        return null;
    }

    // @Bean
    // public DatabasePopulator databasePopulator(DataSource dataSource) {
    // ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    // populator.setContinueOnError(true);
    // populator.setIgnoreFailedDrops(true);
    // populator.addScript(new ClassPathResource("test-data.sql"));
    // try {
    // populator.populate(dataSource.getConnection());
    // } catch (SQLException ignored) {
    // }
    // return populator;
    // }

    @Bean
    public UserDao getUserDao() {
        return new UserDaoHibernate();
    }

    @Bean
    public StudyDao getStudyDao() {
        return new StudyDaoHibernate();
    }
}
