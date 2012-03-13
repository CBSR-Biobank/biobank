package edu.ualberta.med.biobank.validator.constraint.impl;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.EntityMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;

import edu.ualberta.med.biobank.validator.SessionAwareConstraintValidator;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;

public class NotUsedValidator extends SessionAwareConstraintValidator<Object>
    implements ConstraintValidator<NotUsed, Object> {
    private Class<?> by;
    private String property;

    @Override
    public void initialize(NotUsed annotation) {
        this.by = annotation.by();
        this.property = annotation.property();
    }

    @Override
    public boolean isValidInSession(Object value,
        ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean unused = countRows(value) == 0;

        if (!unused) {
            overrideEmptyMessageTemplate(value, context);
        }

        return unused;
    }

    private void overrideEmptyMessageTemplate(Object value,
        ConstraintValidatorContext context) {
        String defaultTemplate = context.getDefaultConstraintMessageTemplate();

        if (defaultTemplate.isEmpty()) {
            ClassMetadata meta = getSession().getSessionFactory()
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

    private int countRows(Object value) {
        ClassMetadata meta = getSession().getSessionFactory()
            .getClassMetadata(by);

        List<?> results = getSession()
            .createCriteria(meta.getMappedClass(EntityMode.POJO))
            .add(Restrictions.eq(property, value))
            .setProjection(Projections.rowCount())
            .list();

        Number count = (Number) results.iterator().next();

        return count.intValue();
    }
}
