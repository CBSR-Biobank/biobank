package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.base.PermissionBaseWrapper;
import edu.ualberta.med.biobank.model.Permission;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PermissionWrapper extends PermissionBaseWrapper {

    public PermissionWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public PermissionWrapper(WritableApplicationService appService,
        Permission wrappedObject) {
        super(appService, wrappedObject);
    }

    @SuppressWarnings("nls")
    private static final String ALL_PERMISSIONS_QRY = "from "
        + Permission.class.getName();

    public static List<PermissionWrapper> getAllPermissions(
        WritableApplicationService appService) throws ApplicationException {
        List<PermissionWrapper> wrappers = new ArrayList<PermissionWrapper>();
        HQLCriteria c = new HQLCriteria(ALL_PERMISSIONS_QRY);
        List<Permission> permissions = appService.query(c);
        for (Permission perm : permissions)
            wrappers.add(new PermissionWrapper(appService, perm));
        return wrappers;
    }
}
