package edu.ualberta.med.biobank.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.hibernate.Session;
import org.hibernate.validator.engine.ConstraintValidatorFactoryImpl;

/**
 * 
 * 
 * @author jferland
 * @see http 
 *      ://www.lunatech-research.com/archives/2008/05/09/bean-validation-java-ee
 * @see https://community.jboss.org/wiki/
 *      AccessingtheHibernateSessionwithinaConstraintValidator
 * @see http
 *      ://stackoverflow.com/questions/4613055/hibernate-unique-key-validation
 */
public class SessionAwareConstraintValidatorFactory implements
    ConstraintValidatorFactory {
    private final ConstraintValidatorFactoryImpl constraintValidatorFactoryImpl;
    private final Session session;

    public SessionAwareConstraintValidatorFactory(Session session) {
        this.session = session;

        constraintValidatorFactoryImpl = new ConstraintValidatorFactoryImpl();
    }

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        T constraintValidator = constraintValidatorFactoryImpl.getInstance(key);
        if (constraintValidator instanceof SessionAwareConstraintValidator) {
            ((SessionAwareConstraintValidator<?>) constraintValidator)
                .setSession(session);
        }
        return constraintValidator;
    }
}