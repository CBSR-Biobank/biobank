package edu.ualberta.med.biobank.common.action.others;

import java.text.MessageFormat;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Tr;

public class CheckNoDuplicateAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Tr UNEXPECTED_RESULTS =
        bundle.tr("Expected a single query result, but got \"{0}\".");

    private final Class<?> objectClass;
    private final String propertyName;
    private final String value;
    private final Integer objectId;

    @SuppressWarnings("nls")
    private static final String CHECK_NO_DUPLICATES =
        "select count(o) from {0} "
            + "as o where {1}=? {2}";

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

    @SuppressWarnings("nls")
    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        String equalsTest = "";
        if (objectId != null) {
            equalsTest = " and id <> ?";
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
            throw new ActionException(UNEXPECTED_RESULTS.format(res.size()));
        }
        return new BooleanResult(res.get(0) == 0);
    }
}
