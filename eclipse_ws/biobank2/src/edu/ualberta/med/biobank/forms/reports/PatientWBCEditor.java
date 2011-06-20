package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

public class PatientWBCEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.PatientWBCEditor"; //$NON-NLS-1$

    @Override
    protected void createOptionSection(Composite parent) {
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { "Study", "Clinic", "Patient", "Date", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            "Sample Type", "Inventory ID", "Location" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Override
    protected List<String> getParamNames() {
        return new ArrayList<String>();
    }

    @Override
    protected void initReport() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
