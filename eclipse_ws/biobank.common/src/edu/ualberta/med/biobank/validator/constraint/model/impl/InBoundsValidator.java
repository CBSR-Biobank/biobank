package edu.ualberta.med.biobank.validator.constraint.model.impl;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.persister.entity.EntityPersister;

import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.validator.EventSourceAwareConstraintValidator;
import edu.ualberta.med.biobank.validator.constraint.model.InBounds;

public class InBoundsValidator
    extends EventSourceAwareConstraintValidator<Object>
    implements ConstraintValidator<InBounds, Object> {
    private static final String MULTIPLE_CHILD_TYPES =
        "{edu.ualberta.med.biobank.model.ContainerType.ValidContainerType.multipleChildTypes}";
    private static final String OVER_CAPACITY =
        "{edu.ualberta.med.biobank.model.ContainerType.ValidContainerType.overCapacity}";
    private static final String ILLEGAL_CHANGE =
        "{edu.ualberta.med.biobank.model.ContainerType.ValidContainerType.illegalChange}";

    @Override
    public void initialize(InBounds annotation) {
    }

    @Override
    public boolean isValidInEventSource(Object value,
        ConstraintValidatorContext context) {
        if (value == null) return true;
        if (!(value instanceof ContainerType)) return false;

        // TODO: note that this type of validation should probably be enforced
        // through foreign key constraints. Then the capacity and labeling
        // scheme can be changed?

        context.disableDefaultConstraintViolation();

        ContainerType containerType = (ContainerType) value;

        boolean isValid = true;

        isValid &= checkCapacity(containerType, context);
        isValid &= checkChanges(containerType, context);
        isValid &= checkChildrenTypes(containerType, context);
        isValid &= checkRemovedChildContainerTypes(containerType, context);
        isValid &= checkRemovedSpecimenTypes(containerType, context);

        return isValid;
    }

    private boolean checkChildrenTypes(ContainerType ct,
        ConstraintValidatorContext context) {
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
            }
        }
        return true;
    }

    private boolean checkCapacity(ContainerType ct,
        ConstraintValidatorContext context) {
        if (!ct.getChildLabelingScheme().canLabel(ct.getCapacity())) {
            context.buildConstraintViolationWithTemplate(OVER_CAPACITY)
                .addNode("childLabelingScheme")
                // TODO: any way to mark rowCapacity and colCapacity?
                .addNode("capacity")
                .addConstraintViolation();
        }
        return true;
    }

    private boolean checkChanges(ContainerType ct,
        ConstraintValidatorContext context) {
        if (ct.isNew()) return true;
        if (!isUsed(ct)) return true;

        EntityPersister persister = getEventSource().
            getEntityPersister(ContainerType.class.getName(), ct);

        Integer id = ct.getId();
        Object[] data = persister.getDatabaseSnapshot(id, getEventSource());
        String[] propertyNames = persister.getPropertyNames();

        boolean isValid = true;

        for (int i = 0, n = data.length; i < n && isValid; i++) {
            Object value = data[i];
            String propertyName = propertyNames[i];

            if ("capacity".equals(propertyName)) {
                isValid &= !nullSafeEquals(value, ct.getCapacity());
            } else if ("topLevel".equals(propertyName)) {
                isValid &= !nullSafeEquals(value, ct.getTopLevel());
            } else if ("childLabelingScheme".equals(propertyName)) {
                isValid &= !nullSafeEquals(value, ct.getChildLabelingScheme());
            }
        }

        if (!isValid) {
            context.buildConstraintViolationWithTemplate(ILLEGAL_CHANGE)
                .addNode("capacity")
                .addNode("topLevel")
                .addNode("childLabelingScheme")
                .addConstraintViolation();
        }

        return isValid;
    }

    private static boolean nullSafeEquals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    private boolean isUsed(ContainerType ct) {
        return isUsed(ct, SpecimenPosition.class, "container")
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
        ConstraintValidatorContext context) {
        // TODO: check this?
        return true;
    }

    private boolean checkRemovedSpecimenTypes(ContainerType ct,
        ConstraintValidatorContext context) {
        // TODO: check this?
        return true;
    }
}
