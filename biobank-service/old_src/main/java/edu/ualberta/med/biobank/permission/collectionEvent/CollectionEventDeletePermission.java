package edu.ualberta.med.biobank.permission.collectionEvent;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.study.CollectionEvent;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class CollectionEventDeletePermission implements Permission {

    private static final long serialVersionUID = 1L;
    private Integer ceventId;

    public CollectionEventDeletePermission(Integer ceventId) {
        this.ceventId = ceventId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        CollectionEvent cevent = context.load(CollectionEvent.class,
            ceventId);
        return PermissionEnum.COLLECTION_EVENT_DELETE
            .isAllowed(context.getUser(), cevent.getPatient().getStudy());
    }

}
