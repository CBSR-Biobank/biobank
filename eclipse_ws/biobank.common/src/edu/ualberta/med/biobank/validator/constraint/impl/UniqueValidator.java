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

    private String[] fields;

    @Override
    public void initialize(Unique annotation) {
        this.fields = annotation.properties();
    }

    @Override
    public boolean isValidInSession(Object value,
        ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return countRows(value) == 0;
    }

    private int countRows(Object value) {
        ClassMetadata meta =
            getSessionFactory().getClassMetadata(value.getClass());
        String idName = meta.getIdentifierPropertyName();
        Serializable id =
            meta.getIdentifier(value, (SessionImplementor) getTmpSession());

        DetachedCriteria criteria = DetachedCriteria.forClass(value.getClass());
        for (String field : fields) {
            criteria.add(Restrictions.eq(field,
                meta.getPropertyValue(value, field, EntityMode.POJO)));
        }
        criteria.add(Restrictions.ne(idName, id)).setProjection(
            Projections.rowCount());

        List<?> results =
            criteria.getExecutableCriteria(getTmpSession()).list();
        Number count = (Number) results.iterator().next();
        return count.intValue();
    }
}
