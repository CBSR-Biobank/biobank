package edu.ualberta.med.biobank.gui.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import edu.ualberta.med.biobank.common.action.security.UserPermissionsGetAction.UserCreatePermissions;

public class LoginPermissionSessionState extends AbstractSourceProvider {

    @SuppressWarnings("nls")
    public final static String LOGIN_STATE_SOURCE_NAME =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.loginState";

    @SuppressWarnings("nls")
    public static final String CLINIC_CREATE_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.clinicCreatePermission";

    @SuppressWarnings("nls")
    public static final String COLLECTION_EVENT_CREATE_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.collectionEventCreatePermission";

    @SuppressWarnings("nls")
    public static final String CONTAINER_CREATE_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.containerCreatePermission";

    @SuppressWarnings("nls")
    public static final String CONTAINER_TYPE_CREATE_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.containerTypeCreatePermission";

    @SuppressWarnings("nls")
    public static final String DISPATCH_CREATE_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.dispatchCreatePermission";

    @SuppressWarnings("nls")
    public static final String GLOBAL_ADMIN_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.globalAdminPermission";

    @SuppressWarnings("nls")
    public static final String ORIGIN_INFO_UPDATE_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.originInfoUpdatePermission";

    @SuppressWarnings("nls")
    public static final String PATIENT_CREATE_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.patientCreatePermission";

    @SuppressWarnings("nls")
    public static final String PATIENT_MERGE_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.patientMergePermission";

    @SuppressWarnings("nls")
    public static final String PROCESSING_EVENT_CREATE_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.processingEventCreatePermission";

    @SuppressWarnings("nls")
    public static final String RESEARCH_GROUP_CREATE_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.researchGroupCreatePermission";

    @SuppressWarnings("nls")
    public static final String SITE_CREATE_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.siteCreatePermission";

    @SuppressWarnings("nls")
    public static final String SPECIMEN_ASSIGN_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.specimenAssignPermission";

    @SuppressWarnings("nls")
    public static final String SPECIMEN_LINK_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.specimenLinkPermission";

    @SuppressWarnings("nls")
    public static final String SPECIMEN_TYPE_CREATE_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.specimenTypeCreatePermission";

    @SuppressWarnings("nls")
    public static final String STUDY_CREATE_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.studyCreatePermission";

    @SuppressWarnings("nls")
    public static final String USER_MANAGER_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.userManagerPermission";

    @SuppressWarnings("nls")
    public static final String LABEL_PRINTING_PERMISSION =
        "edu.ualberta.med.biobank.gui.common.sourceprovider.labelPrintingPermission";

    private boolean loggedIn;

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { LOGIN_STATE_SOURCE_NAME,
            LABEL_PRINTING_PERMISSION };
    }

    @Override
    public void dispose() {
    }

    @Override
    public Map<String, Boolean> getCurrentState() {
        Map<String, Boolean> currentStateMap = new HashMap<String, Boolean>(1);
        currentStateMap.put(LOGIN_STATE_SOURCE_NAME, loggedIn);
        return currentStateMap;
    }

    public void setLoggedInState(boolean loggedIn) {
        if (this.loggedIn == loggedIn) return; // no change

        this.loggedIn = loggedIn;
        fireSourceChanged(ISources.WORKBENCH, LOGIN_STATE_SOURCE_NAME, loggedIn);
    }

    public void setUserCreatePermissions(
        UserCreatePermissions userCreatePermissions) {

        fireSourceChanged(ISources.WORKBENCH, CLINIC_CREATE_PERMISSION,
            userCreatePermissions.isClinicCreatePermission());

        fireSourceChanged(ISources.WORKBENCH,
            COLLECTION_EVENT_CREATE_PERMISSION,
            userCreatePermissions.isCollectionEventCreatePermission());

        fireSourceChanged(ISources.WORKBENCH, CONTAINER_CREATE_PERMISSION,
            userCreatePermissions.isContainerCreatePermission());

        fireSourceChanged(ISources.WORKBENCH, CONTAINER_TYPE_CREATE_PERMISSION,
            userCreatePermissions.isContainerTypeCreatePermission());

        fireSourceChanged(ISources.WORKBENCH, DISPATCH_CREATE_PERMISSION,
            userCreatePermissions.isDispatchCreatePermission());

        fireSourceChanged(ISources.WORKBENCH, GLOBAL_ADMIN_PERMISSION,
            userCreatePermissions.isGlobalAdminPermission());

        fireSourceChanged(ISources.WORKBENCH, ORIGIN_INFO_UPDATE_PERMISSION,
            userCreatePermissions.isOriginInfoUpdatePermission());

        fireSourceChanged(ISources.WORKBENCH, PATIENT_CREATE_PERMISSION,
            userCreatePermissions.isPatientCreatePermission());

        fireSourceChanged(ISources.WORKBENCH, PATIENT_MERGE_PERMISSION,
            userCreatePermissions.isPatientMergePermission());

        fireSourceChanged(ISources.WORKBENCH,
            PROCESSING_EVENT_CREATE_PERMISSION,
            userCreatePermissions.isProcessingEventCreatePermission());

        fireSourceChanged(ISources.WORKBENCH, RESEARCH_GROUP_CREATE_PERMISSION,
            userCreatePermissions.isResearchGroupCreatePermission());

        fireSourceChanged(ISources.WORKBENCH, SITE_CREATE_PERMISSION,
            userCreatePermissions.isSiteCreatePermission());

        fireSourceChanged(ISources.WORKBENCH, SPECIMEN_ASSIGN_PERMISSION,
            userCreatePermissions.isSpecimenAssignPermission());

        fireSourceChanged(ISources.WORKBENCH, SPECIMEN_LINK_PERMISSION,
            userCreatePermissions.isSpecimenLinkPermission());

        fireSourceChanged(ISources.WORKBENCH, SPECIMEN_TYPE_CREATE_PERMISSION,
            userCreatePermissions.isSpecimenTypeCreatePermission());

        fireSourceChanged(ISources.WORKBENCH, STUDY_CREATE_PERMISSION,
            userCreatePermissions.isStudyCreatePermission());

        fireSourceChanged(ISources.WORKBENCH, USER_MANAGER_PERMISSION,
            userCreatePermissions.isUserManagerPermission());

        fireSourceChanged(ISources.WORKBENCH, LABEL_PRINTING_PERMISSION,
            userCreatePermissions.isLabelPrintingPermission());

    }

}
