package edu.ualberta.med.biobank.model;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactoryBean;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import edu.ualberta.med.biobank.model.context.ExecutingUser;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {
	@Bean
	public SessionFactory sessionFactory() {
		return new LocalSessionFactoryBuilder(datasource())
				// TODO: improve speed by using explicit classes
				.scanPackages("edu.ualberta.med.biobank.model",
							  "edu.ualberta.med.biobank.model.center",
							  "edu.ualberta.med.biobank.model.envers",
							  "edu.ualberta.med.biobank.model.report",
							  "edu.ualberta.med.biobank.model.security",
							  "edu.ualberta.med.biobank.model.study")
				.buildSessionFactory();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new HibernateTransactionManager(sessionFactory());
	}

	@Bean
	public DataSource datasource() {
		EmbeddedDatabaseFactoryBean bean = new EmbeddedDatabaseFactoryBean();
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.addScript(new ClassPathResource(
				"hibernate/config/java/schema.sql"));
		bean.setDatabasePopulator(databasePopulator);
		
		/* necessary because EmbeddedDatabaseFactoryBean is a FactoryBean */
		bean.afterPropertiesSet();
		
		return bean.getObject();
	}
	
	@Bean
	public ExecutingUser executingUser() {
		return null;
	}
}
