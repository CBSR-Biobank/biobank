package edu.ualberta.med.biobank.propertytester;

import org.eclipse.core.expressions.PropertyTester;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventCreatePermission;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.views.CollectionView;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class CollectionEventCanCreatePropertyTester extends PropertyTester {

    @SuppressWarnings("nls")
    public static final String CAN_CREATE = "canCreate";

    private static BgcLogger log = BgcLogger
        .getLogger(AdapterBasePropertyTester.class.getName());

    @SuppressWarnings("nls")
    @Override
    public boolean test(Object receiver, String property, Object[] args,
        Object expectedValue) {

        if (!SessionManager.getInstance().isConnected()
            || (CollectionView.getCurrent() == null)
            || (CollectionView.getCurrentPatient() == null)) return false;

        // System.out.println("CollectionEventCanCreatePropertyTester: "
        // + property);

        boolean allowed = false;
        try {
            allowed = SessionManager.getAppService().isAllowed(
                new CollectionEventCreatePermission(CollectionView
                    .getCurrentPatient().getId()));
        } catch (ApplicationException e) {
            log.error("Problem testing menus enablement", e);
        }
        return allowed;
    }

}
