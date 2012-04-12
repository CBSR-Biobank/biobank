package edu.ualberta.med.biobank.common.action.request;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Request;

public class RequestRetrievalAction implements Action<ListResult<Request>> {

    @SuppressWarnings("nls")
    private static final String REQUEST_HQL =
        "SELECT r FROM " + Request.class.getName() + " r"
            + " INNER JOIN FETCH r.requestSpecimens rs"
            + " INNER JOIN FETCH r.researchGroup rg"
            + " WHERE rs.specimen.currentCenter.id=?";

    private static final long serialVersionUID = 5306372891238576571L;

    private Integer centerId;

    public RequestRetrievalAction(Integer centerId) {
        this.centerId = centerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.REQUEST_READ.isAllowed(context.getUser(),
            (Center) context.getSession().load(Center.class, centerId));
    }

    @Override
    public ListResult<Request> run(ActionContext context)
        throws ActionException {
        ArrayList<Request> requests = new ArrayList<Request>();

        Query query = context.getSession().createQuery(REQUEST_HQL);
        query.setParameter(0, centerId);

        @SuppressWarnings("unchecked")
        List<Request> results = query.list();
        if (results != null) {
            requests.addAll(results);
        }

        return new ListResult<Request>(requests);
    }

}
