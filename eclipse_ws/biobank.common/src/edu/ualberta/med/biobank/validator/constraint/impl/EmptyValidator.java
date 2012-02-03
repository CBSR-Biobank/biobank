package edu.ualberta.med.biobank.validator.constraint.impl;

import java.io.Serializable;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;

import edu.ualberta.med.biobank.validator.SessionAwareConstraintValidator;
import edu.ualberta.med.biobank.validator.constraint.Empty;

public class EmptyValidator extends SessionAwareConstraintValidator<Object>
    implements ConstraintValidator<Empty, Object> {
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
        return countRows(value) == 0;
    }

    private int countRows(Object value) {
        ClassMetadata meta = getSession().getSessionFactory()
            .getClassMetadata(value.getClass());

        // TODO: not sure if it's a good idea to check the property directly if
        // it is initialized. If so, uncomment the following code:
        // if (Hibernate.isPropertyInitialized(value, property)) {
        // Object propertyValue =
        // meta.getPropertyValue(value, property, EntityMode.POJO);
        //
        // if (propertyValue instanceof Collection) {
        // int size = ((Collection<?>) propertyValue).size();
        // return size;
        // } else {
        // // TODO: throw an exception or log a warning?
        // }
        // }

        String idName = meta.getIdentifierPropertyName();
        Serializable id = meta.getIdentifier(value,
            (SessionImplementor) getSession());

        DetachedCriteria criteria = DetachedCriteria.forClass(value.getClass());
        criteria.add(Restrictions.eq(idName, id))
            .setProjection(Projections.count(property));

        List<?> results = criteria.getExecutableCriteria(getSession()).list();
        Number count = (Number) results.iterator().next();

        return count.intValue();
    }
}
