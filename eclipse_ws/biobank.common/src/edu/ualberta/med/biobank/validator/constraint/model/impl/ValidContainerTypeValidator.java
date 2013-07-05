package edu.ualberta.med.biobank.validator.constraint.model.impl;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.util.NullUtil;
import edu.ualberta.med.biobank.validator.EventSourceAwareConstraintValidator;
import edu.ualberta.med.biobank.validator.constraint.model.ValidContainerType;

@SuppressWarnings("nls")
public class ValidContainerTypeValidator
    extends EventSourceAwareConstraintValidator<Object>
    implements ConstraintValidator<ValidContainerType, Object> {
    public static final String MULTIPLE_CHILD_TYPES =
        "{edu.ualberta.med.biobank.model.ContainerType.ValidContainerType.multipleChildTypes}";
    public static final String OVER_CAPACITY =
        "{edu.ualberta.med.biobank.model.ContainerType.ValidContainerType.overCapacity}";
    public static final String ILLEGAL_CHANGE =
        "{edu.ualberta.med.biobank.model.ContainerType.ValidContainerType.illegalChange}";
    public static final String REMOVED_CT_IN_USE =
        "{edu.ualberta.med.biobank.model.ContainerType.ValidContainerType.illegalChildContainerTypeRemove}";
    public static final String REMOVED_ST_IN_USE =
        "{edu.ualberta.med.biobank.model.ContainerType.ValidContainerType.illegalSpecimenTypeRemove}";

    @Override
    public void initialize(ValidContainerType annotation) {
    }

    @Override
    public boolean isValidInEventSource(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (!(value instanceof ContainerType)) return false;

        context.disableDefaultConstraintViolation();

        ContainerType ct = (ContainerType) value;
        ContainerType oldCt = getOldContainerTypeOrNull(ct);

        boolean isValid = true;

        isValid &= checkCapacity(ct, context);
        isValid &= checkChildrenTypes(ct, context);

        if (oldCt != null) {
            isValid &= checkChanges(ct, oldCt, context);
            isValid &= checkRemovedChildContainerTypes(ct, oldCt, context);
            isValid &= checkRemovedSpecimenTypes(ct, oldCt, context);
        }

        return isValid;
    }

    private boolean checkChildrenTypes(ContainerType ct, ConstraintValidatorContext context) {
        // if either set is initialised we must load the other one to be sure,
        // otherwise assume this check passed before and still does
        if (Hibernate.isInitialized(ct.getChildContainerTypes()) ||
            Hibernate.isInitialized(ct.getSpecimenTypes())) {
            if (!ct.getChildContainerTypes().isEmpty()
                && !ct.getSpecimenTypes().isEmpty()) {
                context
                    .buildConstraintViolationWithTemplate(MULTIPLE_CHILD_TYPES)
                    .addNode("childContainerTypes")
                    .addNode("specimenTypes")
                    .addConstraintViolation();
                return false;
            }
        }
        return true;
    }

    private boolean checkCapacity(ContainerType ct,
        ConstraintValidatorContext context) {
        ContainerLabelingScheme scheme = ct.getChildLabelingScheme();
        Capacity capacity = ct.getCapacity();
        // allow other validation to handle null issues, so do extensive null
        // checking here
        if (scheme != null &&
            capacity != null &&
            capacity.getRowCapacity() != null &&
            capacity.getColCapacity() != null &&
            !scheme.canLabel(capacity)) {
            context.buildConstraintViolationWithTemplate(OVER_CAPACITY)
                .addNode("childLabelingScheme")
                // TODO: any way to mark rowCapacity and colCapacity?
                .addNode("capacity")
                .addConstraintViolation();
            return false;
        }
        return true;
    }

    private ContainerType getOldContainerTypeOrNull(ContainerType ct) {
        if (ct.isNew()) return null;

        ContainerType oldCt = null;

        // Get the old value in the same transaction in case that transaction
        // has not been committed yet
        Connection conn = getEventSource().connection();
        Session newSession = getEventSource().getSessionFactory()
            .openSession(conn);

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

        // TODO: should be able to change capacity, labeling scheme and labeling layout as long as
        // it does not cause any existing containers or specimens to have a label change. For
        // example, it is probably okay to add and remove rows, but not columns if more than one row
        // is filled (assuming labeling is done row by row)

        isValid &= NullUtil.eq(ct.getCapacity(), oldCt.getCapacity());
        isValid &= NullUtil.eq(ct.getTopLevel(), oldCt.getTopLevel());
        isValid &= NullUtil.eq(ct.getIsMicroplate(), oldCt.getIsMicroplate());
        isValid &= NullUtil.eq(ct.getChildLabelingScheme(), oldCt.getChildLabelingScheme());
        isValid &= NullUtil.eq(ct.getLabelingLayout(), oldCt.getLabelingLayout());

        if (!isValid) {
            context.buildConstraintViolationWithTemplate(ILLEGAL_CHANGE)
                .addNode("capacity")
                .addNode("topLevel")
                .addNode("isMicroplate")
                .addNode("childLabelingScheme")
                .addNode("labelingLayout")
                .addConstraintViolation();
        }

        return isValid;
    }

    private boolean isUsed(ContainerType ct) {
        return isUsed(ct, SpecimenPosition.class, "containerType")
            || isUsed(ct, ContainerPosition.class, "parentContainerType");
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
            .createCriteria(ContainerPosition.class)
            .add(Restrictions.in("containerType", removed))
            .add(Restrictions.eq("parentContainerType", ct))
            .setProjection(Projections.rowCount())
            .list();

        Number count = (Number) results.iterator().next();
        boolean isValid = count.intValue() == 0;

        if (!isValid) {
            context.buildConstraintViolationWithTemplate(
                REMOVED_CT_IN_USE)
                .addNode("childContainerTypes")
                .addConstraintViolation();
        }

        return isValid;
    }

    private boolean checkRemovedSpecimenTypes(ContainerType ct,
        ContainerType oldCt, ConstraintValidatorContext context) {
        Set<SpecimenType> removed = new HashSet<SpecimenType>();
        removed.addAll(oldCt.getSpecimenTypes());
        removed.removeAll(ct.getSpecimenTypes());

        if (removed.isEmpty()) return true;

        List<?> results = getEventSource()
            .createCriteria(SpecimenPosition.class)
            .add(Restrictions.in("specimenType", removed))
            .add(Restrictions.eq("containerType", ct))
            .setProjection(Projections.rowCount())
            .list();

        Number count = (Number) results.iterator().next();
        boolean isValid = count.intValue() == 0;

        if (!isValid) {
            context.buildConstraintViolationWithTemplate(
                REMOVED_ST_IN_USE)
                .addNode("specimenTypes")
                .addConstraintViolation();
        }

        return isValid;
    }
}
