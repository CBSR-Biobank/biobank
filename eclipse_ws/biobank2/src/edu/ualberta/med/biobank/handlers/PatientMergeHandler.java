package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.views.CollectionView;

public class PatientMergeHandler extends AbstractHandler {

    private static BgcLogger logger = BgcLogger
        .getLogger(PatientMergeHandler.class.getName());

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            AdapterBase.openForm(
                new FormInput(CollectionView.getCurrentPatient()),
                "edu.ualberta.med.biobank.forms.PatientMergeForm", true);
        } catch (Exception exp) {
            logger.error("Error while opening the patient merge form", exp);
        }
        return null;
    }
}
