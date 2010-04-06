package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.widgets.infotables.PatientInfoTable;

public class PatientEntryInfoTable extends PatientInfoTable {

    public PatientEntryInfoTable(Composite parent, List<PatientWrapper> patients) {
        super(parent, patients);
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

}
