package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;

public class PatientVisitSummaryEditor extends ReportsEditor {

    protected DateTimeWidget start;
    protected DateTimeWidget end;

    public static String ID = "edu.ualberta.med.biobank.editors.PatientVisitSummaryEditor";

    @Override
    protected void createOptionSection(Composite parent) {
        start = widgetCreator.createDateTimeWidget(parent,
            "Start Date (Time Drawn)", null, null, null, SWT.DATE);
        end = widgetCreator.createDateTimeWidget(parent,
            "End Date (Time Drawn)", null, null, null, SWT.DATE);
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
        return new String[] { "Study", "Clinic", "1 Visit", "2 Visit",
            "3 Visit", "4 Visit", "5+ Visits", "Total Visits", "Total Patients" };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> param = new ArrayList<String>();
        param.add("Start Date (Time Drawn)");
        param.add("End Date (Time Drawn)");
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
