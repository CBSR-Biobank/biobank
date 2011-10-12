package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;

public class CollectionEventAddHandler extends AbstractHandler {

    private static BgcLogger logger = BgcLogger
        .getLogger(CollectionEventAddHandler.class.getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // FIXME
        // try {
        // PatientAdapter patientAdapter = CollectionView.getCurrentPatient();
        // CollectionEvent ce = new CollectionEvent();
        // ce.setPatient(patientAdapter.getPatient());
        // CollectionEventInfo ceventInfo = new CollectionEventInfo();
        // ceventInfo.cevent = ce;
        // CollectionEventAdapter adapter = new CollectionEventAdapter(
        // patientAdapter, ceventInfo);
        // adapter.openEntryForm();
        // } catch (Exception exp) {
        //            logger.error("Error while opening the collection event entry form", //$NON-NLS-1$
        // exp);
        // }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return SessionManager.canCreate(CollectionEventWrapper.class);
    }
}