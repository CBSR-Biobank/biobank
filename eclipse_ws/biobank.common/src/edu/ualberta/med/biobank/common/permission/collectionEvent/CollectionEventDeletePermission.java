package edu.ualberta.med.biobank.common.permission.collectionEvent;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventDeletePermission implements Permission {

    private static final long serialVersionUID = 1L;
    private Integer ceventId;

    public CollectionEventDeletePermission(Integer ceventId) {
        this.ceventId = ceventId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        CollectionEvent cevent =
            new ActionContext(user, session).load(CollectionEvent.class,
                ceventId);
        return PermissionEnum.COLLECTION_EVENT_DELETE
            .isAllowed(user, cevent.getPatient().getStudy());
    }

}
