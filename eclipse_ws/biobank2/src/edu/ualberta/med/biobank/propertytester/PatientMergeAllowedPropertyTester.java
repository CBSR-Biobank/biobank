package edu.ualberta.med.biobank.propertytester;

import org.eclipse.core.expressions.PropertyTester;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.patient.PatientMergePermission;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.views.CollectionView;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientMergeAllowedPropertyTester extends PropertyTester {

    @SuppressWarnings("nls")
    public static final String CAN_MERGE = "canMerge";

    private static BgcLogger log = BgcLogger
        .getLogger(AdapterBasePropertyTester.class.getName());

    @SuppressWarnings("nls")
    @Override
    public boolean test(Object receiver, String property, Object[] args,
        Object expectedValue) {

        if (!SessionManager.getInstance().isConnected()
            || (CollectionView.getCurrentPatient() == null)) return false;

        boolean allowed = false;
        try {
            allowed = SessionManager.getAppService().isAllowed(
                new PatientMergePermission(CollectionView
                    .getCurrentPatient().getId(), null));
        } catch (ApplicationException e) {
            log.error("Problem testing menus enablement", e);
        }
        return allowed;
    }

}
