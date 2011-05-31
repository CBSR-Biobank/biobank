package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.BiobankCheck;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.CollectionNotEmptyException;

public class CheckCollectionIsEmpty<E> extends BiobankCheck<E> {
    private static final long serialVersionUID = 1L;
    private static final String EXCEPTION_MESSAGE = "{0} {1} has one or more {2}.";
    private static final String COUNT_HQL = "SELECT m.{0}.size FROM {1} m WHERE m = ?";

    private final Property<?, E> property;
    private final String modelString;

    public CheckCollectionIsEmpty(ModelWrapper<E> wrapper,
        Property<? extends Collection<?>, E> property) {
        super(wrapper);
        this.property = property;
        this.modelString = wrapper.toString();
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {

        String hql = MessageFormat.format(COUNT_HQL, property.getName(),
            getModelClass().getName());
        Query query = session.createQuery(hql);
        query.setParameter(0, getModel());

        List<?> results = query.list();
        Long count = Check.getCountFromResult(results);

        if (count == null || count > 0) {
            String modelClass = Format.modelClass(getModelClass());
            String propertyName = Format.propertyName(property);

            String msg = MessageFormat.format(EXCEPTION_MESSAGE, modelClass,
                modelString, propertyName);

            throw new CollectionNotEmptyException(msg);
        }

        return null;
    }
}