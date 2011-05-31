package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.BiobankSearchAction;
import edu.ualberta.med.biobank.common.wrappers.BiobankSessionActionException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;

public class CheckCollectionIsEmpty<E> extends BiobankSearchAction<E> {
    private static final long serialVersionUID = 1L;

    private static final String COUNT_HQL = "SELECT m.{0}.size FROM {1} m WHERE m = ?";

    private static final String CANNOT_COUNT = "There was an error while counting the {0} of {1}.";
    private static final String NOT_EMPTY = "{0} has one or more {1}.";

    private final Property<?, E> property;
    private final String modelString;

    public CheckCollectionIsEmpty(ModelWrapper<E> wrapper,
        Property<? extends Collection<?>, E> property) {
        super(wrapper);
        this.property = property;
        this.modelString = wrapper.toString();
    }

    @Override
    public Object doAction(Session session)
        throws BiobankSessionActionException {

        String hql = MessageFormat.format(COUNT_HQL, property.getName(),
            getModelClass().getName());
        Query query = session.createQuery(hql);
        query.setParameter(0, getModel());

        List<?> results = query.list();
        Long count = Check.getCountFromResult(results);

        String elementClassName = property.getElementClass().getSimpleName();

        if (count == null) {
            String msg = MessageFormat.format(CANNOT_COUNT, elementClassName,
                modelString);
            throw new BiobankSessionActionException(msg);
        } else if (count > 1) {
            String msg = MessageFormat.format(NOT_EMPTY, modelString,
                elementClassName);
            throw new BiobankSessionActionException(msg);
        }

        return null;
    }
}