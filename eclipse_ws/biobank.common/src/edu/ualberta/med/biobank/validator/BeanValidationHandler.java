package edu.ualberta.med.biobank.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.ConstraintValidatorFactory;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.TraversableResolver;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.hibernate.EntityMode;
import org.hibernate.cfg.beanvalidation.HibernateTraversableResolver;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.event.AbstractPreDatabaseOperationEvent;
import org.hibernate.event.EventSource;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

import edu.ualberta.med.biobank.model.group.Delete;
import edu.ualberta.med.biobank.model.group.Insert;
import edu.ualberta.med.biobank.model.group.Update;

public class BeanValidationHandler implements PreInsertEventListener,
    PreUpdateEventListener, PreDeleteEventListener {
    private static final long serialVersionUID = 1L;

    private final ValidatorFactory factory;
    private ConcurrentHashMap<EntityPersister, Set<String>> associationsPerEntityPersister =
        new ConcurrentHashMap<EntityPersister, Set<String>>();

    public BeanValidationHandler() {
        factory = Validation.buildDefaultValidatorFactory();
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        validate(event, new Class<?>[] { Default.class });
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        validate(event, new Class<?>[] { Default.class });
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        validate(event, new Class<?>[] { Default.class });
        return false;
    }

    private void validate(AbstractPreDatabaseOperationEvent event,
        Class<?>[] groups) {
        // TODO: write something to track event source (Session) and only
        // validate once? Not sure this is a good idea if validators affect each
        // other, or the same object is saved multiple times... can't guarantee
        // that won't happen.
        validate(event.getEntity(), event, groups);
    }

    private <T> void validate(T object,
        AbstractPreDatabaseOperationEvent event, Class<?>[] groups) {

        EntityMode mode = event.getSession().getEntityMode();

        if (object == null || mode != EntityMode.POJO) {
            return;
        }

        EntityPersister persister = event.getPersister();
        EventSource session = event.getSession();
        SessionFactoryImplementor sessionFactory = session.getFactory();

        TraversableResolver tr = new HibernateTraversableResolver(
            persister, associationsPerEntityPersister, sessionFactory);

        ConstraintValidatorFactory validatorFactory =
            new SessionAwareConstraintValidatorFactory(session);

        Validator validator = factory.usingContext()
            .traversableResolver(tr)
            .constraintValidatorFactory(validatorFactory)
            .getValidator();

        if (groups.length > 0) {
            final Set<ConstraintViolation<T>> constraintViolations =
                validator.validate(object, groups);
            if (constraintViolations.size() > 0) {
                //
                // TODO: ensure translatable ConstraintViolation-s?
                //
                Set<ConstraintViolation<?>> propagatedViolations =
                    new HashSet<ConstraintViolation<?>>(
                        constraintViolations.size());
                Set<String> classNames = new HashSet<String>();
                for (ConstraintViolation<?> violation : constraintViolations) {
                    // if (log.isTraceEnabled()) {
                    // log.trace(violation.toString());
                    // }
                    propagatedViolations.add(violation);
                    classNames
                        .add(violation.getLeafBean().getClass().getName());
                }
                StringBuilder builder = new StringBuilder();
                builder.append("validation failed for classes ");
                builder.append(classNames);
                builder.append(" for groups ");
                builder.append(Arrays.toString(groups));
                throw new ConstraintViolationException(
                    builder.toString(), propagatedViolations);
            }
        }
    }
}
