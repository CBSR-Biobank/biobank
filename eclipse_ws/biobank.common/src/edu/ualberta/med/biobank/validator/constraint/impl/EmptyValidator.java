package edu.ualberta.med.biobank.validator.constraint.impl;

import java.text.MessageFormat;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.EntityMode;
import org.hibernate.Query;
import org.hibernate.metadata.ClassMetadata;

import edu.ualberta.med.biobank.validator.SessionAwareConstraintValidator;
import edu.ualberta.med.biobank.validator.constraint.Empty;

public class EmptyValidator extends SessionAwareConstraintValidator<Object>
    implements ConstraintValidator<Empty, Object> {
    private static final String SIZE_QUERY_TEMPLATE =
        "SELECT {0}.size FROM {1} o WHERE o = ?";

    private String property;

    @Override
    public void initialize(Empty annotation) {
        this.property = annotation.property();
    }

    @Override
    public boolean isValidInSession(Object value,
        ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean empty = getCollectionSize(value) == 0;

        if (!empty) {
            overrideEmptyMessageTemplate(value, context);
        }

        return empty;
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
            template.append(Empty.class.getSimpleName());
            template.append(".");
            template.append(property);
            template.append("}");

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(template.toString())
                .addConstraintViolation();
        }
    }

    private int getCollectionSize(Object value) {
        ClassMetadata meta = getSession().getSessionFactory()
            .getClassMetadata(value.getClass());

        // Don't check the property directy, but use HQL instead since it could
        // be enormous. UNLESS, see the following link:
        // http://stackoverflow.com/questions/2913160/hibernate-count-collection-size-without-initializing
        // BUT, we would still have a problem if the database is out of sync
        // from the local collection.

        String hql = MessageFormat.format(SIZE_QUERY_TEMPLATE,
            property, meta.getMappedClass(EntityMode.POJO).getName());

        Query query = getSession().createQuery(hql).setParameter(0, value);

        List<?> results = query.list();
        Number count = (Number) results.iterator().next();

        return count.intValue();
    }
}
