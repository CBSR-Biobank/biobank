package edu.ualberta.med.biobank.action.security;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.security.UserPermissionsGetAction.UserCreatePermissions;
import edu.ualberta.med.biobank.permission.GlobalAdminPermission;
import edu.ualberta.med.biobank.permission.clinic.ClinicCreatePermission;
import edu.ualberta.med.biobank.permission.container.ContainerCreatePermission;
import edu.ualberta.med.biobank.permission.containerType.ContainerTypeCreatePermission;
import edu.ualberta.med.biobank.permission.dispatch.DispatchCreatePermission;
import edu.ualberta.med.biobank.permission.labelPrinting.LabelPrintingPermission;
import edu.ualberta.med.biobank.permission.patient.PatientCreatePermission;
import edu.ualberta.med.biobank.permission.processingEvent.ProcessingEventCreatePermission;
import edu.ualberta.med.biobank.permission.researchGroup.ResearchGroupCreatePermission;
import edu.ualberta.med.biobank.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.permission.shipment.OriginInfoUpdatePermission;
import edu.ualberta.med.biobank.permission.site.SiteCreatePermission;
import edu.ualberta.med.biobank.permission.specimen.SpecimenAssignPermission;
import edu.ualberta.med.biobank.permission.specimen.SpecimenLinkPermission;
import edu.ualberta.med.biobank.permission.specimenType.SpecimenTypeCreatePermission;
import edu.ualberta.med.biobank.permission.study.StudyCreatePermission;

/**
 * Note: center ID can be null and the permissions that require a working center
 * will be left as false.
 * 
 * @author loyola
 * 
 */
public class UserPermissionsGetAction implements Action<UserCreatePermissions> {
    private static final long serialVersionUID = 1L;

    private final Integer centerId;

    public UserPermissionsGetAction(Integer id) {
        centerId = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @Override
    public UserCreatePermissions run(ActionContext context)
        throws ActionException {
        UserCreatePermissions p = new UserCreatePermissions();

        p.clinicCreatePermission =
            new ClinicCreatePermission().isAllowed(context);
        p.globalAdminPermission =
            new GlobalAdminPermission().isAllowed(context);
        p.researchGroupCreatePermission =
            new ResearchGroupCreatePermission().isAllowed(context);
        p.siteCreatePermission = new SiteCreatePermission().isAllowed(context);
        p.specimenTypeCreatePermission =
            new SpecimenTypeCreatePermission().isAllowed(context);
        p.studyCreatePermission =
            new StudyCreatePermission().isAllowed(context);
        p.userManagerPermission =
            new UserManagerPermission().isAllowed(context);
        p.labelPrintingPermission = new LabelPrintingPermission()
            .isAllowed(context);
        p.patientCreatePermission =
            new PatientCreatePermission(null).isAllowed(context);

        if (centerId != null) {
            p.containerCreatePermission =
                new ContainerCreatePermission(centerId).isAllowed(context);
            p.containerTypeCreatePermission =
                new ContainerTypeCreatePermission(centerId).isAllowed(context);
            p.dispatchCreatePermission =
                new DispatchCreatePermission(centerId).isAllowed(context);
            p.originInfoUpdatePermission =
                new OriginInfoUpdatePermission(centerId).isAllowed(context);
            p.processingEventCreatePermission =
                new ProcessingEventCreatePermission(centerId)
                    .isAllowed(context);
            p.specimenAssignPermission =
                new SpecimenAssignPermission(centerId).isAllowed(context);
            p.specimenLinkPermission =
                new SpecimenLinkPermission(centerId, null).isAllowed(context);
        }

        return p;
    }

    public static class UserCreatePermissions implements ActionResult {
        private static final long serialVersionUID = 1L;

        private boolean clinicCreatePermission;
        private boolean containerCreatePermission;
        private boolean containerTypeCreatePermission;
        private boolean dispatchCreatePermission;
        private boolean globalAdminPermission;
        private boolean originInfoUpdatePermission;
        private boolean patientCreatePermission;
        private boolean patientMergePermission;
        private boolean processingEventCreatePermission;
        private boolean researchGroupCreatePermission;
        private boolean siteCreatePermission;
        private boolean specimenAssignPermission;
        private boolean specimenLinkPermission;
        private boolean specimenTypeCreatePermission;
        private boolean studyCreatePermission;
        private boolean userManagerPermission;
        private boolean labelPrintingPermission;

        public static long getSerialversionuid() {
            return serialVersionUID;
        }

        public boolean isClinicCreatePermission() {
            return clinicCreatePermission;
        }

        public boolean isContainerCreatePermission() {
            return containerCreatePermission;
        }

        public boolean isContainerTypeCreatePermission() {
            return containerTypeCreatePermission;
        }

        public boolean isDispatchCreatePermission() {
            return dispatchCreatePermission;
        }

        public boolean isGlobalAdminPermission() {
            return globalAdminPermission;
        }

        public boolean isOriginInfoUpdatePermission() {
            return originInfoUpdatePermission;
        }

        public boolean isPatientCreatePermission() {
            return patientCreatePermission;
        }

        public boolean isPatientMergePermission() {
            return patientMergePermission;
        }

        public boolean isProcessingEventCreatePermission() {
            return processingEventCreatePermission;
        }

        public boolean isResearchGroupCreatePermission() {
            return researchGroupCreatePermission;
        }

        public boolean isSiteCreatePermission() {
            return siteCreatePermission;
        }

        public boolean isSpecimenAssignPermission() {
            return specimenAssignPermission;
        }

        public boolean isSpecimenLinkPermission() {
            return specimenLinkPermission;
        }

        public boolean isSpecimenTypeCreatePermission() {
            return specimenTypeCreatePermission;
        }

        public boolean isStudyCreatePermission() {
            return studyCreatePermission;
        }

        public boolean isUserManagerPermission() {
            return userManagerPermission;
        }

        public boolean isLabelPrintingPermission() {
            return labelPrintingPermission;
        }
    }

}
