package edu.ualberta.med.biobank.validator.constraint.impl;

import java.io.Serializable;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.EntityMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;

import edu.ualberta.med.biobank.validator.EventSourceAwareConstraintValidator;
import edu.ualberta.med.biobank.validator.constraint.Unique;

@SuppressWarnings("nls")
public class UniqueValidator extends
    EventSourceAwareConstraintValidator<Object>
    implements ConstraintValidator<Unique, Object> {

    private String[] properties;

    @Override
    public void initialize(Unique annotation) {
        this.properties = annotation.properties();
    }

    @Override
    public boolean isValidInEventSource(Object value,
        ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean unique = countRows(value) == 0;
        if (!unique) {
            overrideEmptyMessageTemplate(value, context);
        }
        return unique;
    }

    public static String getDefaultMessageTemplate(Class<?> klazz,
        String[] properties) {
        StringBuilder template = new StringBuilder();

        template.append("{");
        template.append(klazz.getName());
        template.append(".");
        template.append(Unique.class.getSimpleName());
        template.append("[");

        for (int i = 0, n = properties.length; i < n; i++) {
            template.append(properties[i]);
            if (i < n - 1) template.append(",");
        }

        template.append("]}");

        return template.toString();
    }

    private void overrideEmptyMessageTemplate(Object value,
        ConstraintValidatorContext context) {
        String defaultTemplate = context.getDefaultConstraintMessageTemplate();

        if (defaultTemplate.isEmpty()) {
            ClassMetadata meta = getEventSource().getSessionFactory()
                .getClassMetadata(value.getClass());

            Class<?> klazz = meta.getMappedClass(EntityMode.POJO);
            String template = getDefaultMessageTemplate(klazz, properties);

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(template)
                .addConstraintViolation();
        }
    }

    private int countRows(Object value) {
        ClassMetadata meta = getEventSource().getSessionFactory()
            .getClassMetadata(value.getClass());
        String idName = meta.getIdentifierPropertyName();
        Serializable id = meta.getIdentifier(value, getEventSource());

        DetachedCriteria criteria = DetachedCriteria.forClass(value.getClass());
        for (String property : properties) {
            criteria.add(Restrictions.eq(property,
                meta.getPropertyValue(value, property, EntityMode.POJO)));
        }

        if (id != null) {
            criteria.add(Restrictions.ne(idName, id));
        }

        criteria.setProjection(Projections.rowCount());

        List<?> results =
            criteria.getExecutableCriteria(getEventSource()).list();
        Number count = (Number) results.iterator().next();

        // Because actions are queued, it is possible for two objects to have
        // the same value for a unique field. The pre-insert/update validation
        // will succeed because they query the database, but when both actions
        // are flushed, then the database will raise an error.
        // TODO: query cache for duplicates?

        return count.intValue();
    }
}
