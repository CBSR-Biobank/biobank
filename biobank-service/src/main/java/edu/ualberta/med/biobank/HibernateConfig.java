package edu.ualberta.med.biobank;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
public class HibernateConfig {

    @Value("${jdbc.driverClassName}")
    private String driverClassName;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;
    @Value("${hibernate.show_sql}")
    private String hibernateShowSql;
    @Value("${hibernate.hbm2ddl.auto}")
    private String hibernateHbm2ddlAuto;

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

        // EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        // builder.setType(EmbeddedDatabaseType.H2);
        // builder.setName("test");
        // return builder.build();

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactoryBean() {
        LocalSessionFactoryBean result = new LocalSessionFactoryBean();
        result.setDataSource(dataSource());
        result.setAnnotatedPackages(new String[] { "edu.ualberta.med.biobank.model" });
        result.setPackagesToScan(new String[] { "edu.ualberta.med.biobank.model" });

        Properties properties = new Properties();
        properties.setProperty("connection.url", "jdbc:mysql://localhost:3306/biobank");
        // properties.setProperty("connection.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");

        properties.setProperty("hibernate.dialect", hibernateDialect);
        properties.setProperty("hibernate.show_sql", hibernateShowSql);
        properties.setProperty("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);

        result.setHibernateProperties(properties);
        return result;
    }

    // @Bean
    // public SessionFactory sessionFactory() {
    // LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(datasource());
    // builder.addPackage("edu.ualberta.med.biobank.model");
    // // TODO: improve speed by using explicit classes
    // builder.scanPackages("edu.ualberta.med.biobank.model",
    // "edu.ualberta.med.biobank.model.center",
    // "edu.ualberta.med.biobank.model.envers",
    // "edu.ualberta.med.biobank.model.report",
    // "edu.ualberta.med.biobank.model.security",
    // "edu.ualberta.med.biobank.model.study");
    //
    // builder
    // .setProperty("hibernate.show_sql", "true")
    // .setProperty("hibernate.cache.region.factory_class",
    // "org.hibernate.cache.ehcache.EhCacheRegionFactory")
    // .setProperty("hibernate.cache.use_query_cache", "true")
    // .setProperty("hibernate.cache.use_second_level_cache", "true")
    // .setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
    //
    // return builder.buildSessionFactory();
    // }

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

    @Bean
    public DatabasePopulator databasePopulator(DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setContinueOnError(true);
        populator.setIgnoreFailedDrops(true);
        populator.addScript(new ClassPathResource("test-data.sql"));
        try {
            populator.populate(dataSource.getConnection());
        } catch (SQLException ignored) {
        }
        return populator;
    }
}
