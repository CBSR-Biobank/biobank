package edu.ualberta.med.biobank;

import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SessionSecurityHelper {

    public static final String PRINT_LABEL_KEY_DESC = "print-labels"; //$NON-NLS-1$
    public static final String CLINIC_SHIPMENT_KEY_DESC = "cs-OriginInfo"; //$NON-NLS-1$
    public static final String DISPATCH_RECEIVE_KEY_DESC = "receive-Dispatch"; //$NON-NLS-1$
    public static final String DISPATCH_SEND_KEY_DESC = "send-Dispatch"; //$NON-NLS-1$
    public static final String SPECIMEN_ASSIGN_KEY_DESC = "specimen-assign"; //$NON-NLS-1$
    public static final String SPECIMEN_LINK_KEY_DESC = "specimen-link"; //$NON-NLS-1$
    public static final String LOGGING_KEY_DESC = "logging"; //$NON-NLS-1$
    public static final String REPORTS_KEY_DESC = "reports"; //$NON-NLS-1$
    public static final String REQUEST_ASK_KEY_DESC = "ask-request"; //$NON-NLS-1$
    public static final String REQUEST_RECEIVE_DESC = "receive-request"; //$NON-NLS-1$

    public static Map<String, PrivilegeWrapper> privileges;

    private static Map<String, PrivilegeWrapper> getPrivileges(
        BiobankApplicationService appService) throws ApplicationException {
        if (privileges == null) {
            privileges = new HashMap<String, PrivilegeWrapper>();
            for (PrivilegeWrapper p : PrivilegeWrapper
                .getAllPrivileges(appService)) {
                privileges.put(p.getName(), p);
            }
        }
        return privileges;
    }

    public static PrivilegeWrapper getReadPrivilege(
        BiobankApplicationService appService) throws ApplicationException {
        return getPrivileges(appService).get("Read"); //$NON-NLS-1$
    }

    public static PrivilegeWrapper getUpdatePrivilege(
        BiobankApplicationService appService) throws ApplicationException {
        return getPrivileges(appService).get("Update"); //$NON-NLS-1$
    }

    public static PrivilegeWrapper getDeletePrivilege(
        BiobankApplicationService appService) throws ApplicationException {
        return getPrivileges(appService).get("Delete"); //$NON-NLS-1$
    }

    public static PrivilegeWrapper getCreatePrivilege(
        BiobankApplicationService appService) throws ApplicationException {
        return getPrivileges(appService).get("Create"); //$NON-NLS-1$
    }

    public static boolean canCreate(BiobankApplicationService appService,
        UserWrapper user, Class<?> clazz) {
        try {
            return user.hasPrivilegeOnClassObject(
                getCreatePrivilege(appService), clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canDelete(BiobankApplicationService appService,
        UserWrapper user, Class<?> clazz) {
        try {
            return user.hasPrivilegeOnClassObject(
                getDeletePrivilege(appService), clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canDelete(BiobankApplicationService appService,
        UserWrapper user, ModelWrapper<?> wrapper) {
        try {
            return user.hasPrivilegeOnKeyDesc(getDeletePrivilege(appService),
                wrapper.getWrappedClass().getSimpleName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canView(BiobankApplicationService appService,
        UserWrapper user, Class<?> clazz) {
        try {
            return user.hasPrivilegeOnClassObject(getReadPrivilege(appService),
                clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canAccess(BiobankApplicationService appService,
        UserWrapper user, String... keyDesc) {
        try {
            return user.hasPrivilegesOnKeyDesc(getReadPrivilege(appService),
                keyDesc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canUpdate(BiobankApplicationService appService,
        UserWrapper user, Class<?> clazz) {
        try {
            return user.hasPrivilegeOnClassObject(
                getUpdatePrivilege(appService), clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canUpdate(BiobankApplicationService appService,
        UserWrapper user, ModelWrapper<?> wrapper) {
        try {
            return user.hasPrivilegeOnKeyDesc(getUpdatePrivilege(appService),
                wrapper.getWrappedClass().getSimpleName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
