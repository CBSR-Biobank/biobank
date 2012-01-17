package edu.ualberta.med.biobank.common.action.constraint;

import java.io.Serializable;
import java.util.List;

import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;

import edu.ualberta.med.biobank.common.action.ActionContext;

public class UniqueValidator implements IConstraintValidator<Object> {
    private final String[] properties;

    public UniqueValidator(String... properties) {
        this.properties = properties;
    }

    @Override
    public void validate(Object object, ActionContext context) {
        countRows(object, context.getSession());
    }

    private int countRows(Object object, Session session) {
        SessionFactory sessionFactory = session.getSessionFactory();
        ClassMetadata meta = sessionFactory.getClassMetadata(object.getClass());

        DetachedCriteria criteria =
            DetachedCriteria.forClass(object.getClass());

        for (String property : properties) {
            Object value =
                meta.getPropertyValue(object, property, EntityMode.POJO);
            criteria.add(Restrictions.eq(property, value));
        }

        // don't count self
        String idName = meta.getIdentifierPropertyName();
        Serializable id =
            meta.getIdentifier(object, (SessionImplementor) session);

        criteria.add(Restrictions.ne(idName, id))
            .setProjection(Projections.rowCount());

        List<?> results = criteria.getExecutableCriteria(session).list();
        Number count = (Number) results.iterator().next();

        return count.intValue();
    }
}
