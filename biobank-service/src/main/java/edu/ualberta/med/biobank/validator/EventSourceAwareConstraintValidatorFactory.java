package edu.ualberta.med.biobank.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.hibernate.event.spi.EventSource;
import org.hibernate.validator.internal.engine.ConstraintValidatorFactoryImpl;

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
public class EventSourceAwareConstraintValidatorFactory implements
    ConstraintValidatorFactory {
    private final ConstraintValidatorFactoryImpl constraintValidatorFactoryImpl;
    private final EventSource eventSource;

    public EventSourceAwareConstraintValidatorFactory(EventSource eventSource) {
        this.eventSource = eventSource;

        constraintValidatorFactoryImpl = new ConstraintValidatorFactoryImpl();
    }

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        T constraintValidator = constraintValidatorFactoryImpl.getInstance(key);
        if (constraintValidator instanceof EventSourceAwareConstraintValidator) {
            ((EventSourceAwareConstraintValidator<?>) constraintValidator)
                .setSession(eventSource);
        }
        return constraintValidator;
    }
}