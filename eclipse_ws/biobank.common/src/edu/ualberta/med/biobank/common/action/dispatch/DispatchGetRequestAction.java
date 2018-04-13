package edu.ualberta.med.biobank.common.action.dispatch;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchReadPermission;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;

public class DispatchGetRequestAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Tr CANNOT_FIND_DISPATCH_ERRMSG =
        bundle.tr("Cannot find a dispatch with id \"{0}\".");

    @SuppressWarnings("nls")
    // pick up request id only for dispatch in creation mode (state =0)
    private static final String DISPATCH_SQL = "SELECT REQUEST_ID,STATE "
            + "FROM dispatch"
            + " WHERE dispatch.id=?";

    private final Integer id;

    public DispatchGetRequestAction(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new DispatchReadPermission(id).isAllowed(context);
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {

	boolean result = false;

        Query query = context.getSession().createSQLQuery(DISPATCH_SQL);
        query.setParameter(0, id);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();

        if (rows.size() == 1) {
		Object[] req = rows.get(0);
            Integer state = (Integer) req[1];
            if (req[0] != null && state.intValue()==0)
		result = true;
        } else {
            throw new LocalizedException(CANNOT_FIND_DISPATCH_ERRMSG.format(id));
        }
        return new BooleanResult(result);
    }
}
