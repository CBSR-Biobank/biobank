package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;

public class PatientReport1Editor extends ReportsEditor {

    protected DateTimeWidget start;
    protected DateTimeWidget end;

    public static String ID = "edu.ualberta.med.biobank.editors.PatientVisitSummaryEditor"; //$NON-NLS-1$

    @Override
    protected void createOptionSection(Composite parent) {
        start = widgetCreator.createDateTimeWidget(parent,
            Messages.PatientVisitSummaryEditor_start_label, null, null, null,
            SWT.DATE);
        end = widgetCreator.createDateTimeWidget(parent,
            Messages.PatientVisitSummaryEditor_end_label, null, null, null,
            SWT.DATE);
    }

    @Override
    protected void initReport() {
        List<Object> params = new ArrayList<Object>();
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        report.setParams(params);
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { Messages.PatientVisitSummaryEditor_study_label,
            Messages.PatientVisitSummaryEditor_clinic_label,
            Messages.PatientVisitSummaryEditor_1visit_label,
            Messages.PatientVisitSummaryEditor_2visits_label,
            Messages.PatientVisitSummaryEditor_3visits_label,
            Messages.PatientVisitSummaryEditor_4visits_label,
            Messages.PatientVisitSummaryEditor_5visits_label,
            Messages.PatientVisitSummaryEditor_visits_total_label,
            Messages.PatientVisitSummaryEditor_patient_total_label };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> param = new ArrayList<String>();
        param.add(Messages.PatientVisitSummaryEditor_start_label);
        param.add(Messages.PatientVisitSummaryEditor_end_label);
        return param;
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        List<Object> params = new ArrayList<Object>();
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        return params;
    }

    @Override
    protected void onReset() throws Exception {
        start.setDate(null);
        end.setDate(null);
        super.onReset();
    }

}
