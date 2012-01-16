package edu.ualberta.med.biobank.common.permission.dispatch;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.User;

public class DispatchDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer shipmentId;

    public DispatchDeletePermission(Integer shipId) {
        this.shipmentId = shipId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Dispatch ship =
            new ActionContext(user, session).load(Dispatch.class, shipmentId);
        if (DispatchState.getState(ship.getState()).equals(
            DispatchState.CREATION))
            return PermissionEnum.DISPATCH_DELETE.isAllowed(user,
                ship.getSenderCenter());
        return false;
    }
}
