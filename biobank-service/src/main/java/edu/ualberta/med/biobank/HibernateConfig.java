package edu.ualberta.med.biobank;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;
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
public class HibernateConfig {
    @Bean
    public SessionFactory sessionFactory() {
        LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(datasource());
        builder.addPackage("edu.ualberta.med.biobank.model");
        // TODO: improve speed by using explicit classes
        builder.scanPackages("edu.ualberta.med.biobank.model",
            "edu.ualberta.med.biobank.model.center",
            "edu.ualberta.med.biobank.model.envers",
            "edu.ualberta.med.biobank.model.report",
            "edu.ualberta.med.biobank.model.security",
            "edu.ualberta.med.biobank.model.study");

        builder
            .setProperty("hibernate.show_sql", "true")
            .setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory")
            .setProperty("hibernate.cache.use_query_cache", "true")
            .setProperty("hibernate.cache.use_second_level_cache", "true")
            .setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");

        return builder.buildSessionFactory();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new HibernateTransactionManager(sessionFactory());
    }

    @Bean
    public DataSource datasource() {
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
        builder.setType(EmbeddedDatabaseType.HSQL);
        builder.addDefaultScripts();
        return builder.build();
    }

    @Bean
    public ExecutingUser executingUser() {
        return null;
    }
}
