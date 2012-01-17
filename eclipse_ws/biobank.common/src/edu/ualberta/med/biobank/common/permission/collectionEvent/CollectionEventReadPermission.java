package edu.ualberta.med.biobank.common.permission.collectionEvent;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.CollectionEvent;

public class CollectionEventReadPermission implements Permission {

    private static final long serialVersionUID = 1L;
    private Integer ceventId;

    public CollectionEventReadPermission(Integer ceventId) {
        this.ceventId = ceventId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        CollectionEvent cevent = context.load(CollectionEvent.class,
            ceventId);
        return PermissionEnum.COLLECTION_EVENT_READ
            .isAllowed(context.getUser(), cevent.getPatient().getStudy());
    }

}
