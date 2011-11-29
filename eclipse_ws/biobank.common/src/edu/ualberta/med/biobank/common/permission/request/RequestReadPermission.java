package edu.ualberta.med.biobank.common.permission.request;

import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.peer.RequestSpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.User;

public class RequestReadPermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer requestId;

    @SuppressWarnings("nls")
    private static final String PENDING_REQUESTS = "select distinct(ra."
        + RequestSpecimenPeer.REQUEST.getName()
        + ") from "
        + RequestSpecimen.class.getName()
        + " ra where ra."
        + Property.concatNames(RequestSpecimenPeer.SPECIMEN,
            SpecimenPeer.CURRENT_CENTER) + " = ? and ra.state = "
        + RequestSpecimenState.AVAILABLE_STATE.getId() + " or ra.state = "
        + RequestSpecimenState.PULLED_STATE.getId();

    public RequestReadPermission(Integer oiId) {
        this.requestId = oiId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Request request =
            ActionUtil.sessionGet(session, Request.class, requestId);
        @SuppressWarnings("unchecked")
        List<Request> requests = session.createQuery(PENDING_REQUESTS).list();
        return requests.contains(request);
    }
}
