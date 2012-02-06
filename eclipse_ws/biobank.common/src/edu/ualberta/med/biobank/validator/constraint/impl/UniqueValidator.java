package edu.ualberta.med.biobank.validator.constraint.impl;

import java.io.Serializable;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.EntityMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;

import edu.ualberta.med.biobank.validator.SessionAwareConstraintValidator;
import edu.ualberta.med.biobank.validator.constraint.Unique;

public class UniqueValidator extends SessionAwareConstraintValidator<Object>
    implements ConstraintValidator<Unique, Object> {

    private String[] properties;

    @Override
    public void initialize(Unique annotation) {
        this.properties = annotation.properties();
    }

    @Override
    public boolean isValidInSession(Object value,
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

    private void overrideEmptyMessageTemplate(Object value,
        ConstraintValidatorContext context) {
        String defaultTemplate = context.getDefaultConstraintMessageTemplate();

        if (defaultTemplate.isEmpty()) {
            ClassMetadata meta = getSession().getSessionFactory()
                .getClassMetadata(value.getClass());

            StringBuilder template = new StringBuilder();

            template.append("{");
            template.append(meta.getMappedClass(EntityMode.POJO).getName());
            template.append(".Unique[");

            for (int i = 0, n = properties.length; i < n; i++) {
                template.append(properties[i]);
                if (i < n - 1) template.append(",");
            }

            template.append("]}");

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(template.toString())
                .addConstraintViolation();
        }
    }

    private int countRows(Object value) {
        ClassMetadata meta = getSession().getSessionFactory()
            .getClassMetadata(value.getClass());
        String idName = meta.getIdentifierPropertyName();
        Serializable id = meta.getIdentifier(value,
            (SessionImplementor) getSession());

        DetachedCriteria criteria = DetachedCriteria.forClass(value.getClass());
        for (String property : properties) {
            criteria.add(Restrictions.eq(property,
                meta.getPropertyValue(value, property, EntityMode.POJO)));
        }
        criteria.add(Restrictions.ne(idName, id)).setProjection(
            Projections.rowCount());

        List<?> results = criteria.getExecutableCriteria(getSession()).list();
        Number count = (Number) results.iterator().next();

        // Because actions are queued, it is possible for two objects to have
        // the same value for a unique field. The pre-insert/update validation
        // will succeed because they query the database, but when both actions
        // are flushed, then the database will raise an error.
        // TODO: query cache for duplicates?

        return count.intValue();
    }
}
