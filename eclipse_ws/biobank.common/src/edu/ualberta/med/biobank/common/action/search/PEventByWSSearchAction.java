package edu.ualberta.med.biobank.common.action.search;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class PEventByWSSearchAction implements
    Action<ListResult<Integer>> {

    @SuppressWarnings("nls")
    protected static final String PEVENT_BASE_QRY =
        "SELECT pe.id FROM "
            + ProcessingEvent.class.getName()
            + " pe"
            + " where pe.worksheet=? and pe.center.id=?";

    private static final long serialVersionUID = 1L;
    private String worksheet;

    private Integer currentCenter;

    public PEventByWSSearchAction(String worksheet,
        Integer currentCenter) {
        this.worksheet = worksheet;
        this.currentCenter = currentCenter;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
        // FIXME: ??? what to do
    }

    @Override
    public ListResult<Integer> run(ActionContext context)
        throws ActionException {
        Query q =
            context.getSession().createQuery(PEVENT_BASE_QRY);
        q.setParameter(0, worksheet);
        q.setParameter(1, currentCenter);
        @SuppressWarnings("unchecked")
        List<Integer> rows = q.list();
        return new ListResult<Integer>(rows);
    }
}
