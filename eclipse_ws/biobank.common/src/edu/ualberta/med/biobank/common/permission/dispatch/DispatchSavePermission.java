package edu.ualberta.med.biobank.common.permission.dispatch;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.User;

public class DispatchSavePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer dispatchId;

    public DispatchSavePermission(Integer id) {
        this.dispatchId = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Dispatch dispatch = new SessionUtil(session).get(Dispatch.class, dispatchId, new Dispatch());
        return PermissionEnum.DISPATCH_UPDATE.isAllowed(user,
            dispatch.getReceiverCenter()) || PermissionEnum.DISPATCH_UPDATE.isAllowed(user, dispatch.getSenderCenter());
    }

}
