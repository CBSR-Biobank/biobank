package edu.ualberta.med.biobank;

import edu.ualberta.med.biobank.common.exception.NoRightForKeyDescException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

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

    public static boolean canCreate(BiobankApplicationService appService,
        UserWrapper user, Class<?> clazz) {
        try {
            return user.hasPrivilegeOnClassObject(
                PrivilegeWrapper.getCreatePrivilege(appService), clazz);
        } catch (NoRightForKeyDescException nre) {
            // If there is no right corresponding to this class, then can create
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canDelete(BiobankApplicationService appService,
        UserWrapper user, Class<?> clazz) {
        try {
            return user.hasPrivilegeOnClassObject(
                PrivilegeWrapper.getDeletePrivilege(appService), clazz);
        } catch (NoRightForKeyDescException nre) {
            // If there is no right corresponding to this class, then can delete
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canDelete(UserWrapper user, ModelWrapper<?> wrapper) {
        return wrapper.canDelete(user);
    }

    public static boolean canView(BiobankApplicationService appService,
        UserWrapper user, Class<?> clazz) {
        try {
            return user.hasPrivilegeOnClassObject(
                PrivilegeWrapper.getReadPrivilege(appService), clazz);
        } catch (NoRightForKeyDescException nre) {
            // If there is no right corresponding to this class, then can view
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canAccess(BiobankApplicationService appService,
        UserWrapper user, String... keyDesc) {
        try {
            return user.hasPrivilegesOnKeyDesc(
                PrivilegeWrapper.getReadPrivilege(appService), keyDesc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canUpdate(BiobankApplicationService appService,
        UserWrapper user, Class<?> clazz) {
        try {
            return user.hasPrivilegeOnClassObject(
                PrivilegeWrapper.getUpdatePrivilege(appService), clazz);
        } catch (NoRightForKeyDescException nre) {
            // If there is no right corresponding to this class, then can update
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canUpdate(UserWrapper user, ModelWrapper<?> wrapper) {
        return wrapper.canUpdate(user);
    }
}
