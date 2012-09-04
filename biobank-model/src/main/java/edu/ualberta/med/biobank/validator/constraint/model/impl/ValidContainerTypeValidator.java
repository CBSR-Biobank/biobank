package edu.ualberta.med.biobank.validator.constraint.model.impl;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.ParentContainer;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Vessel;
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

        isValid &= checkChildrenTypes(ct, context);

        if (oldCt != null) {
            isValid &= checkChanges(ct, oldCt, context);
            isValid &= checkRemovedChildContainerTypes(ct, oldCt, context);
            isValid &= checkRemovedVessels(ct, oldCt, context);
        }

        return isValid;
    }

    private boolean checkChildrenTypes(ContainerType ct,
        ConstraintValidatorContext context) {
        // if either set is initialised we must load the other one to be sure,
        // otherwise assume this check passed before and still does
        if (Hibernate.isInitialized(ct.getChildContainerTypes()) ||
            Hibernate.isInitialized(ct.getChildVessels())) {
            if (!ct.getChildContainerTypes().isEmpty()
                && !ct.getChildVessels().isEmpty()) {
                context
                    .buildConstraintViolationWithTemplate(MULTIPLE_CHILD_TYPES)
                    .addNode("childContainerTypes")
                    .addNode("childVessels")
                    .addConstraintViolation();
                return false;
            }
        }
        return true;
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

        isValid &= NullUtil.eq(ct.isTopLevel(), oldCt.isTopLevel());
        isValid &= NullUtil.eq(ct.getSchema(), oldCt.getSchema());

        if (!isValid) {
            context.buildConstraintViolationWithTemplate(ILLEGAL_CHANGE)
                .addNode("topLevel")
                .addNode("schema")
                .addConstraintViolation();
        }

        return isValid;
    }

    private boolean isUsed(ContainerType ct) {
        return isUsed(ct, ParentContainer.class, "container.containerType");
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

    private boolean checkRemovedChildContainerTypes(ContainerType ct,
        ContainerType oldCt, ConstraintValidatorContext context) {
        Set<ContainerType> removed = new HashSet<ContainerType>();
        removed.addAll(oldCt.getChildContainerTypes());
        removed.removeAll(ct.getChildContainerTypes());

        if (removed.isEmpty()) return true;

        List<?> results = getEventSource()
            .createCriteria(Container.class)
            .add(Restrictions.in("containerType", removed))
            .createCriteria("parent")
            .createCriteria("container")
            .add(Restrictions.eq("containerType", ct))
            .setProjection(Projections.rowCount())
            .list();

        Number count = (Number) results.iterator().next();
        boolean isValid = count.intValue() == 0;

        if (!isValid) {
            context.buildConstraintViolationWithTemplate(
                REMOVED_CONTAINER_TYPES_IN_USE)
                .addNode("childContainerTypes")
                .addConstraintViolation();
        }

        return isValid;
    }

    private boolean checkRemovedVessels(ContainerType ct,
        ContainerType oldCt, ConstraintValidatorContext context) {
        Set<Vessel> removed = new HashSet<Vessel>();
        removed.addAll(oldCt.getChildVessels());
        removed.removeAll(ct.getChildVessels());

        if (removed.isEmpty()) return true;

        List<?> results = getEventSource()
            .createCriteria(Specimen.class)
            .add(Restrictions.in("vessel", removed))
            .createCriteria("parent")
            .createCriteria("container")
            .add(Restrictions.eq("containerType", ct))
            .setProjection(Projections.rowCount())
            .list();

        Number count = (Number) results.iterator().next();
        boolean isValid = count.intValue() == 0;

        if (!isValid) {
            context.buildConstraintViolationWithTemplate(
                REMOVED_VESSELS_IN_USE)
                .addNode("childVessels")
                .addConstraintViolation();
        }

        return isValid;
    }
}
