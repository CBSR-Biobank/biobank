package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.common.action.cevent.GetSimplePatientCollectionEventInfosAction.SimpleCEventInfo;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.views.CollectionView;

public class CollectionEventAddHandler extends AbstractHandler {

    private static BgcLogger logger = BgcLogger
        .getLogger(CollectionEventAddHandler.class.getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            PatientAdapter patientAdapter = CollectionView.getCurrentPatient();
            SimpleCEventInfo cevent = new SimpleCEventInfo();
            cevent.cevent = new CollectionEvent();
            cevent.cevent.setPatient(patientAdapter.getPatient());
            CollectionEventAdapter adapter = new CollectionEventAdapter(
                patientAdapter, cevent);
            adapter.openEntryForm();
        } catch (Exception exp) {
            logger.error("Error while opening the collection event entry form", //$NON-NLS-1$
                exp);
        }
        return null;
    }

}