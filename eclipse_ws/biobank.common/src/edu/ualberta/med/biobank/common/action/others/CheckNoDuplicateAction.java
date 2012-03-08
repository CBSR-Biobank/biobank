package edu.ualberta.med.biobank.common.action.others;

import java.text.MessageFormat;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;

public class CheckNoDuplicateAction implements Action<BooleanResult> {

    private static final long serialVersionUID = 1L;
    private Class<?> objectClass;
    private String propertyName;
    private String value;
    private Integer objectId;

    private static final String CHECK_NO_DUPLICATES =
        "select count(o) from {0} " //$NON-NLS-1$
            + "as o where {1}=? {2}"; //$NON-NLS-1$

    public CheckNoDuplicateAction(Class<?> objectClass, Integer objectId,
        String propertyName, String value) {
        this.objectClass = objectClass;
        this.objectId = objectId;
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        String equalsTest = ""; //$NON-NLS-1$
        if (objectId != null) {
            equalsTest = " and id <> ?"; //$NON-NLS-1$
        }

        final String qryString = MessageFormat.format(CHECK_NO_DUPLICATES,
            objectClass.getName(), propertyName, equalsTest);

        Query query = context.getSession().createQuery(qryString);
        query.setParameter(0, value);
        if (objectId != null) {
            query.setParameter(1, objectId);
        }
        @SuppressWarnings("unchecked")
        List<Long> res = query.list();
        if (res.size() != 1) {
            throw new ActionException("Problem in query result size"); //$NON-NLS-1$
        }
        return new BooleanResult(res.get(0) == 0);
    }

}
