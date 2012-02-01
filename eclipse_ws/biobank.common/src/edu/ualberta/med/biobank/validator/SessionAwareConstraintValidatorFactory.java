package edu.ualberta.med.biobank.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.hibernate.SessionFactory;
import org.hibernate.validator.engine.ConstraintValidatorFactoryImpl;

/**
 * 
 * 
 * @author jferland
 * @see http://www.lunatech-research.com/archives/2008/05/09/bean-validation-java-ee
 * @see https://community.jboss.org/wiki/AccessingtheHibernateSessionwithinaConstraintValidator
 * @see http://stackoverflow.com/questions/4613055/hibernate-unique-key-validation
 */
public class SessionAwareConstraintValidatorFactory implements
    ConstraintValidatorFactory {
    private ConstraintValidatorFactoryImpl constraintValidatorFactoryImpl =
        new ConstraintValidatorFactoryImpl();
    private SessionFactory sessionFactory;

    public SessionAwareConstraintValidatorFactory() {
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        T constraintValidator = constraintValidatorFactoryImpl.getInstance(key);
        if (constraintValidator instanceof SessionAwareConstraintValidator) {
            ((SessionAwareConstraintValidator<?>) constraintValidator)
                .setSessionFactory(sessionFactory);
        }
        return constraintValidator;
    }
}