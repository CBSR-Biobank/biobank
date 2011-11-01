package edu.ualberta.med.biobank.common.permission.processingEvent;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.User;

public class ProcessingEventCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(User user, Session session) {
        // FIXME specific study or center ?
        return PermissionEnum.PROCESSING_EVENT_CREATE
            .isAllowed(user);
    }
}
