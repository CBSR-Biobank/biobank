package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class InvoicingReportEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.InvoicingReportEditor";
    protected DateTimeWidget start;
    protected DateTimeWidget end;

    @Override
    protected int[] getColumnWidths() {
        return new int[] { 100, 100, 100, 100, 100 };
    }

    @Override
    protected void createOptionSection(Composite parent) {
        start = widgetCreator.createDateTimeWidget(parent,
            "Start Date (Processed/Linked)", null, null, null);
        end = widgetCreator.createDateTimeWidget(parent,
            "End Date (Processed/Linked)", null, null, null);
    }

    @Override
    protected List<Object> getParams() {
        List<Object> params = new ArrayList<Object>();
        if (start.getDate() == null)
            params.add(new Date(0));
        else
            params.add(start.getDate());
        if (end.getDate() == null)
            params.add(new Date());
        else
            params.add(end.getDate());
        return params;
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { "Study", "Clinic", "Total Visits", "Sample Type",
            "Total Aliquots" };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> param = new ArrayList<String>();
        param.add("Start Date (Processed/Linked)");
        param.add("End Date (Processed/Linked)");
        return param;
    }

}
