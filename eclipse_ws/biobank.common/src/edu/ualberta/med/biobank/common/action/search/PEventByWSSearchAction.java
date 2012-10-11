package edu.ualberta.med.biobank.common.action.search;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventReadPermissionByCenter;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class PEventByWSSearchAction implements
    Action<ListResult<Integer>> {

    @SuppressWarnings("nls")
    protected static final String PEVENT_BASE_QRY =
        "SELECT id FROM " + ProcessingEvent.class.getName()
            + " WHERE worksheet=? AND center.id=?";

    private static final long serialVersionUID = 1L;
    private String worksheet;

    private Integer centerId;

    public PEventByWSSearchAction(String worksheet, Center center) {
        this.worksheet = worksheet;
        this.centerId = center.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        Center center = context.load(Center.class, centerId);
        return new ProcessingEventReadPermissionByCenter(center)
            .isAllowed(context);
    }

    @Override
    public ListResult<Integer> run(ActionContext context)
        throws ActionException {
        Query q =
            context.getSession().createQuery(PEVENT_BASE_QRY);
        q.setParameter(0, worksheet);
        q.setParameter(1, centerId);
        @SuppressWarnings("unchecked")
        List<Integer> rows = q.list();
        return new ListResult<Integer>(rows);
    }
}
