package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

public class ClinicReport1Editor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.FvLPatientVisitsEditor"; //$NON-NLS-1$

    @Override
    protected void createOptionSection(Composite parent) {
        //
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { Messages.FvLPatientVisitsEditor_study_label,
            Messages.FvLPatientVisitsEditor_center_label,
            Messages.FvLPatientVisitsEditor_first_spec_time_label,
            Messages.FvLPatientVisitsEditor_last_spec_time_label };
    }

    @Override
    protected List<String> getParamNames() {
        return new ArrayList<String>();
    }

    @Override
    protected void initReport() throws Exception {
        //
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        return new ArrayList<Object>();
    }

}
