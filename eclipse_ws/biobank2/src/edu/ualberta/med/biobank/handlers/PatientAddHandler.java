package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;

public class PatientAddHandler extends AbstractHandler {

    private static BgcLogger logger = BgcLogger
        .getLogger(PatientAddHandler.class.getName());

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            SearchedPatientInfo spi = new SearchedPatientInfo();
            spi.patient = new Patient();
            PatientAdapter adapter = new PatientAdapter(null, spi);
            adapter.openEntryForm();
        } catch (Exception exp) {
            logger.error("Error while opening the patient entry form", exp);
        }
        return null;
    }
}
