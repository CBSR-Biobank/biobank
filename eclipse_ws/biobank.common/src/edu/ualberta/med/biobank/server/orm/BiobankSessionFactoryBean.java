package edu.ualberta.med.biobank.server.orm;

import javax.validation.ValidatorFactory;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

import edu.ualberta.med.biobank.validator.SessionAwareConstraintValidatorFactory;

import gov.nih.nci.system.dao.orm.SessionFactoryBean;

public class BiobankSessionFactoryBean extends SessionFactoryBean {
    @Override
    protected void postProcessConfiguration(Configuration config)
        throws HibernateException {
        // TODO: set appropriate listeners for validation
        // TODO: set new

        // ValidatorFactory validatorFactory = config
        // .constraintValidatorFactory(
        // new SessionAwareConstraintValidatorFactory())
        // .buildValidatorFactory();
    }
}
