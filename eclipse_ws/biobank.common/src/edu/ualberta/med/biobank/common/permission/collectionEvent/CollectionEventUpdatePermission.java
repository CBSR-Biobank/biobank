package edu.ualberta.med.biobank.common.permission.collectionEvent;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventUpdatePermission implements Permission {

    private static final long serialVersionUID = 1L;
    private Integer ceventId;

    public CollectionEventUpdatePermission(Integer ceventId) {
        this.ceventId = ceventId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        CollectionEvent cevent = ActionUtil.sessionGet(session,
            CollectionEvent.class, ceventId);
        return PermissionEnum.COLLECTION_EVENT_UPDATE
            .isAllowed(user, cevent.getPatient().getStudy());
    }

}
