package edu.ualberta.med.biobank.validator.constraint.impl;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

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
        return countRows(value) == 0;
    }

    private int countRows(Object value) {
        DetachedCriteria criteria = DetachedCriteria.forClass(by);
        criteria.add(Restrictions.eq(property, value));
        criteria.setProjection(Projections.rowCount());

        List<?> results =
            criteria.getExecutableCriteria(getSession()).list();
        Number count = (Number) results.iterator().next();

        return count.intValue();
    }
}
