package edu.ualberta.med.biobank.server.orm;

import gov.nih.nci.system.dao.orm.SessionFactoryBean;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

public class BiobankSessionFactoryBean extends SessionFactoryBean {
    @Override
    protected void postProcessConfiguration(Configuration config)
        throws HibernateException {
        // config.setProperty("constraint-validator-factory", value)
        // TODO: set appropriate listeners for validation
        // TODO: set new

        // config.

        // ValidatorFactory validatorFactory = config
        // .constraintValidatorFactory(
        // new SessionAwareConstraintValidatorFactory())
        // .buildValidatorFactory();
    }
}
