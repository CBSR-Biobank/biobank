package edu.ualberta.med.biobank.permission.collectionEvent;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

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
