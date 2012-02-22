package edu.ualberta.med.biobank.common.action.request;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Request;

public class RequestRetrievalAction implements Action<ListResult<Request>> {

    private static final String REQUEST_HQL =
        "select r from "
            + Request.class.getName()
            + " r inner join fetch r.requestSpecimenCollection rs where rs.specimen.currentCenter.id=?";

    private static final long serialVersionUID = 5306372891238576571L;

    private Integer centerId;

    public RequestRetrievalAction(Integer centerId) {
        this.centerId = centerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.REQUEST_PROCESS.isAllowed(context.getUser(),
            (Center) context.getSession().load(Center.class, centerId));
    }

    @Override
    public ListResult<Request> run(ActionContext context)
        throws ActionException {
        Query q = context.getSession().createQuery(REQUEST_HQL);
        q.setParameter(0, centerId);
        return new ListResult<Request>(q.list());
    }

}
