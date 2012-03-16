package edu.ualberta.med.biobank.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.ConstraintValidatorFactory;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.hibernate.EntityMode;
import org.hibernate.FlushMode;
import org.hibernate.annotations.common.util.StringHelper;
import org.hibernate.cfg.beanvalidation.HibernateTraversableResolver;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.event.AbstractPreDatabaseOperationEvent;
import org.hibernate.event.EventSource;
import org.hibernate.event.PreCollectionRecreateEvent;
import org.hibernate.event.PreCollectionRecreateEventListener;
import org.hibernate.event.PreCollectionRemoveEvent;
import org.hibernate.event.PreCollectionRemoveEventListener;
import org.hibernate.event.PreCollectionUpdateEvent;
import org.hibernate.event.PreCollectionUpdateEventListener;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

import edu.ualberta.med.biobank.validator.engine.LocalizedConstraintViolation;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PreInsert;
import edu.ualberta.med.biobank.validator.group.PreUpdate;
import edu.ualberta.med.biobank.validator.messageinterpolator.OgnlMessageInterpolator;

public class BeanValidationHandler implements PreInsertEventListener,
    PreUpdateEventListener, PreDeleteEventListener,
    PreCollectionUpdateEventListener, PreCollectionRecreateEventListener,
    PreCollectionRemoveEventListener {
    private static final long serialVersionUID = 1L;

    private final ValidatorFactory factory;
    private final MessageInterpolator messageInterpolator =
        new OgnlMessageInterpolator();

    // TODO: I really hope this doesn't hold onto TONS of objects. Investigate!
    private ConcurrentHashMap<EntityPersister, Set<String>> associationsPerEntityPersister =
        new ConcurrentHashMap<EntityPersister, Set<String>>();

    public BeanValidationHandler() {
        factory = Validation.buildDefaultValidatorFactory();
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        validate(event, new Class<?>[] { PreInsert.class, Default.class });
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        validate(event, new Class<?>[] { PreUpdate.class, Default.class });
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        validate(event, new Class<?>[] { PreDelete.class });
        return false;
    }

    @Override
    public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
        Object entity = event.getAffectedOwnerOrNull();
        String role = event.getCollection().getRole();
        String propertyName = StringHelper.unqualify(role);

        // event.get

        System.out.println("Update. Role: " + event.getCollection().getRole());
    }

    @Override
    public void onPreRemoveCollection(PreCollectionRemoveEvent event) {
        System.out.println("Remove. Role: " + event.getCollection().getRole());
    }

    @Override
    public void onPreRecreateCollection(PreCollectionRecreateEvent event) {
        System.out
            .println("Recreate. Role: " + event.getCollection().getRole());
    }

    private void validate(Object entity, String property) {

    }

    private void validate(AbstractPreDatabaseOperationEvent event,
        Class<?>[] groups) {
        EntityPersister persister = event.getPersister();
        EventSource session = event.getSession();
        Validator validator = getValidator(persister, session);
        validate(validator, event.getEntity(), session, groups);
    }

    private Validator getValidator(EntityPersister persister,
        EventSource session) {
        SessionFactoryImplementor sessionFactory = session.getFactory();

        TraversableResolver tr = new HibernateTraversableResolver(
            persister, associationsPerEntityPersister, sessionFactory);

        ConstraintValidatorFactory validatorFactory =
            new EventSourceAwareConstraintValidatorFactory(session);

        Validator validator = factory.usingContext()
            .traversableResolver(tr)
            .constraintValidatorFactory(validatorFactory)
            .messageInterpolator(messageInterpolator)
            .getValidator();

        return validator;
    }

    private <T> void validate(Validator validator, T object,
        EventSource session, Class<?>[] groups) {
        EntityMode mode = session.getEntityMode();

        if (object == null || mode != EntityMode.POJO) return;
        if (groups.length == 0) return;

        FlushMode oldMode = session.getFlushMode();
        try {
            // If the session is used to query data, then another flush could be
            // triggered which will prompt validation (again) and throw us into
            // an infinite loop. Avoid this.
            session.setFlushMode(FlushMode.MANUAL);

            final Set<ConstraintViolation<T>> constraintViolations =
                validator.validate(object, groups);
            handleViolations(constraintViolations, groups);
        } finally {
            session.setFlushMode(oldMode);
        }
    }

    private <T> void handleViolations(
        Set<ConstraintViolation<T>> constraintViolations, Class<?>[] groups) {
        if (constraintViolations.isEmpty()) return;

        // TODO: include the bean being validated and the type of
        // action that was trying to be performed?
        // TODO: put some of this code in another separate class
        // that can be called directly (not through a listener)
        // through a specific action.
        // TODO: add a tag for validations to be performed on the
        // server (only) and not locally...
        Set<ConstraintViolation<?>> localizedViolations =
            new HashSet<ConstraintViolation<?>>(
                constraintViolations.size());

        StringBuilder builder = new StringBuilder();
        builder.append("validation failed for groups: ");
        builder.append(Arrays.toString(groups));

        for (ConstraintViolation<T> violation : constraintViolations) {
            // if (log.isTraceEnabled()) {
            // log.trace(violation.toString());
            // }

            ConstraintViolation<T> localizedViolation =
                new LocalizedConstraintViolation<T>(violation);

            localizedViolations.add(localizedViolation);

            builder.append("\r\n");
            builder.append(violation.getLeafBean().getClass().getName());
            builder.append(":");
            builder.append(violation.getMessage());
        }

        throw new ConstraintViolationException(builder.toString(),
            localizedViolations);
    }
}
