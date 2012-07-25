package edu.ualberta.med.biobank.validator.constraint.impl;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.annotations.common.util.StringHelper;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;

import edu.ualberta.med.biobank.validator.EventSourceAwareConstraintValidator;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;

@SuppressWarnings("nls")
public class NotUsedValidator extends
    EventSourceAwareConstraintValidator<Object>
    implements ConstraintValidator<NotUsed, Object> {
    private Class<?> by;
    private String property;

    @Override
    public void initialize(NotUsed annotation) {
        this.by = annotation.by();
        this.property = annotation.property();
    }

    @Override
    public boolean isValidInEventSource(Object value,
        ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean unused = countRows(value, property) == 0;

        if (!unused) {
            overrideEmptyMessageTemplate(value, context);
        }

        return unused;
    }

    private void overrideEmptyMessageTemplate(Object value,
        ConstraintValidatorContext context) {
        String defaultTemplate = context.getDefaultConstraintMessageTemplate();

        if (defaultTemplate.isEmpty()) {
            ClassMetadata meta = getEventSource().getSessionFactory()
                .getClassMetadata(value.getClass());

            StringBuilder template = new StringBuilder();

            template.append("{");
            template.append(meta.getMappedClass(EntityMode.POJO).getName());
            template.append(".");
            template.append(NotUsed.class.getSimpleName());
            template.append(".");
            template.append(by.getSimpleName());
            template.append(".");
            template.append(property);
            template.append("}");

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(template.toString())
                .addConstraintViolation();
        }
    }

    private int countRows(Object value, String property) {
        Criteria criteria = getEventSource().createCriteria(by);

        String association = StringHelper.root(property);
        while (!association.equals(property)) {
            criteria = criteria.createCriteria(association);
            property = StringHelper.unroot(property);
            association = StringHelper.root(property);
        }

        List<?> results = criteria
            .add(Restrictions.eq(property, value))
            .setProjection(Projections.rowCount())
            .list();

        Number count = (Number) results.iterator().next();
        return count.intValue();
    }
}
