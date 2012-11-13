package edu.ualberta.med.biobank.validator.constraint.model.impl;

import java.sql.Connection;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.HibernateException;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.model.center.Container;
import edu.ualberta.med.biobank.model.center.ContainerType;
import edu.ualberta.med.biobank.model.util.NullUtil;
import edu.ualberta.med.biobank.validator.EventSourceAwareConstraintValidator;
import edu.ualberta.med.biobank.validator.constraint.model.ValidContainerType;

@SuppressWarnings("nls")
public class ValidContainerTypeValidator
    extends EventSourceAwareConstraintValidator<Object>
    implements ConstraintValidator<ValidContainerType, Object> {
    public static final String MULTIPLE_CHILD_TYPES =
        "{edu.ualberta.med.biobank.model.ContainerType.ValidContainerType.multipleChildTypes}";
    public static final String ILLEGAL_CHANGE =
        "{edu.ualberta.med.biobank.model.ContainerType.ValidContainerType.illegalChange}";
    public static final String REMOVED_CONTAINER_TYPES_IN_USE =
        "{edu.ualberta.med.biobank.model.ContainerType.ValidContainerType.illegalChildContainerTypeRemove}";
    public static final String REMOVED_VESSELS_IN_USE =
        "{edu.ualberta.med.biobank.model.ContainerType.ValidContainerType.illegalSpecimenTypeRemove}";

    @Override
    public void initialize(ValidContainerType annotation) {
    }

    @Override
    public boolean isValidInEventSource(Object value,
        ConstraintValidatorContext context) {
        if (value == null) return true;
        if (!(value instanceof ContainerType)) return false;

        context.disableDefaultConstraintViolation();

        ContainerType ct = (ContainerType) value;
        ContainerType oldCt = getOldContainerTypeOrNull(ct);

        boolean isValid = true;

        if (oldCt != null) {
            isValid &= checkChanges(ct, oldCt, context);
        }

        return isValid;
    }

    private ContainerType getOldContainerTypeOrNull(ContainerType ct) {
        if (ct.isNew()) return null;

        ContainerType oldCt = null;

        // Get the old value in the same transaction in case that transaction
        // has not been committed yet
        Connection conn = getEventSource().connection();
        StatelessSession newSession = getEventSource().getSessionFactory()
            .openStatelessSession(conn);

        try {
            oldCt = (ContainerType) newSession
                .createCriteria(ContainerType.class)
                .add(Restrictions.idEq(ct.getId()))
                .uniqueResult();
        } catch (HibernateException e) {
        }

        return oldCt;
    }

    private boolean checkChanges(ContainerType ct, ContainerType oldCt,
        ConstraintValidatorContext context) {
        if (!isUsed(ct)) return true;

        boolean isValid = true;

        isValid &= NullUtil.eq(ct.getSchema(), oldCt.getSchema());

        if (!isValid) {
            context.buildConstraintViolationWithTemplate(ILLEGAL_CHANGE)
                .addNode("schema")
                .addConstraintViolation();
        }

        return isValid;
    }

    private boolean isUsed(ContainerType ct) {
        return isUsed(ct, Container.class, "container.containerType");
    }

    private boolean isUsed(ContainerType ct, Class<?> by, String property) {
        List<?> results = getEventSource()
            .createCriteria(by)
            .add(Restrictions.eq(property, ct))
            .setProjection(Projections.rowCount())
            .list();

        Number count = (Number) results.iterator().next();
        boolean isUsed = count.intValue() != 0;
        return isUsed;
    }
}
