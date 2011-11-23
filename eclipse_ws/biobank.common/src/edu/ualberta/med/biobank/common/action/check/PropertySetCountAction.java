package edu.ualberta.med.biobank.common.action.check;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.CountResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.IBiobankModel;
import edu.ualberta.med.biobank.model.User;

public class PropertySetCountAction implements Action<CountResult> {
    private static final long serialVersionUID = 1L;
    private static final String BINDING = "?";
    private static final String DELIMITER = ", ";
    private static final String HQL =
        "SELECT COUNT(*) FROM {0} o WHERE ({1}) = ({2}) AND id != ?"; //$NON-NLS-1$
    private final Serializable modelId;
    private final Class<?> modelClass;
    private final List<PropertyValue> propertyValues;

    public <E extends IBiobankModel> PropertySetCountAction(E model,
        Class<E> modelClass, Collection<PropertyValue> propertyValues) {
        this.modelId = model.getId();
        this.modelClass = modelClass;
        this.propertyValues = new ArrayList<PropertyValue>(propertyValues);
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    @Override
    public CountResult run(User user, Session session) throws ActionException {
        Query query = getQuery(session);
        Long count = HibernateUtil.getCountFromQuery(query);
        return new CountResult(count);
    }

    private Query getQuery(Session session) {
        String mcName = modelClass.getName();
        String propertyNames = getPropertyNames();
        String bindings = getBindings();

        String hql = MessageFormat.format(HQL, mcName, propertyNames, bindings);

        Query query = session.createQuery(hql);
        setParameters(query);

        return query;
    }

    private String getPropertyNames() {
        List<String> paths = PropertyValue.getPaths(propertyValues);
        return StringUtil.join(paths, DELIMITER);
    }

    private String getBindings() {
        return StringUtil.repeat(BINDING, propertyValues.size(), DELIMITER);
    }

    private void setParameters(Query query) {
        int i = 0;
        for (PropertyValue propertyValue : propertyValues) {
            query.setParameter(i++, propertyValue.getValue());
        }
        query.setParameter(i, modelId);
    }
}
