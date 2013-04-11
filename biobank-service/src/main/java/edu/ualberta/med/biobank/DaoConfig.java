package edu.ualberta.med.biobank;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.ualberta.med.biobank.dao.CollectionEventDao;
import edu.ualberta.med.biobank.dao.CollectionEventTypeDao;
import edu.ualberta.med.biobank.dao.PatientDao;
import edu.ualberta.med.biobank.dao.StudyDao;
import edu.ualberta.med.biobank.dao.UserDao;
import edu.ualberta.med.biobank.dao.hibernate.CollectionEventDaoHibernate;
import edu.ualberta.med.biobank.dao.hibernate.CollectionEventTypeDaoHibernate;
import edu.ualberta.med.biobank.dao.hibernate.PatientDaoHibernate;
import edu.ualberta.med.biobank.dao.hibernate.StudyDaoHibernate;
import edu.ualberta.med.biobank.dao.hibernate.UserDaoHibernate;

@Configuration
public class DaoConfig {

    @Bean
    public UserDao getUserDao() {
        return new UserDaoHibernate();
    }

    @Bean
    public StudyDao getStudyDao() {
        return new StudyDaoHibernate();
    }

    @Bean
    public PatientDao getPatientDao() {
        return new PatientDaoHibernate();
    }

    @Bean
    public CollectionEventDao getCollectionEventDao() {
        return new CollectionEventDaoHibernate();
    }

    @Bean
    public CollectionEventTypeDao getCollectionEventTypeDao() {
        return new CollectionEventTypeDaoHibernate();
    }

}
