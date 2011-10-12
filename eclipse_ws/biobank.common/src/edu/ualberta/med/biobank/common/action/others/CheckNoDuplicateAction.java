package edu.ualberta.med.biobank.common.action.others;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.model.User;

public class CheckNoDuplicateAction implements Action<Boolean> {

    private static final long serialVersionUID = 1L;
    private Class<?> objectClass;
    private String propertyName;
    private String value;
    private Integer objectId;

    private static final String CHECK_NO_DUPLICATES = "select count(o) from {0} " //$NON-NLS-1$
        + "as o where {1}=? {2}"; //$NON-NLS-1$

    public CheckNoDuplicateAction(Class<?> objectClass, Integer objectId,
        String propertyName, String value) {
        this.objectClass = objectClass;
        this.objectId = objectId;
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Boolean doAction(Session session) throws ActionException {
        final List<Object> params = new ArrayList<Object>();
        params.add(value);
        String equalsTest = ""; //$NON-NLS-1$
        if (objectId != null) {
            equalsTest = " and id <> ?"; //$NON-NLS-1$
            params.add(objectId);
        }

        final String qryString = MessageFormat.format(CHECK_NO_DUPLICATES,
            objectClass.getName(), propertyName, equalsTest);

        Query query = session.createQuery(qryString);
        query.setParameter(0, value);
        @SuppressWarnings("unchecked")
        List<Long> res = query.list();
        if (res.size() != 1) {
            throw new ActionException("Problem in query result size"); //$NON-NLS-1$
        }
        return res.get(0) == 0;
    }

}
