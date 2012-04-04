package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.patient.PatientGetSimpleCollectionEventInfosAction.SimpleCEventInfo;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventCreatePermission;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.views.CollectionView;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class CollectionEventAddHandler extends LogoutSensitiveHandler {

    private static BgcLogger logger = BgcLogger
        .getLogger(CollectionEventAddHandler.class.getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            PatientAdapter patientAdapter = CollectionView.getCurrentPatient();
            SimpleCEventInfo cevent = new SimpleCEventInfo();
            cevent.cevent = new CollectionEvent();
            cevent.cevent.setPatient(patientAdapter.getPatient());
            CollectionEventAdapter adapter = new CollectionEventAdapter(null,
                cevent);
            adapter.openEntryForm();
        } catch (Exception exp) {
            logger.error("Error while opening the collection event entry form", //$NON-NLS-1$
                exp);
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        try {
            allowed =
                SessionManager.getAppService().isAllowed(
                    new CollectionEventCreatePermission(CollectionView
                        .getCurrentPatient().getId()));
            return SessionManager.getInstance().getSession() != null &&
                allowed;
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(Messages.HandlerPermission_error,
                Messages.HandlerPermission_message);
            return false;
        }
    }
}