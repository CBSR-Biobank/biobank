package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;

public class AliquotInvoiceByClinicEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.AliquotInvoiceByClinicEditor";
    protected DateTimeWidget start;
    protected DateTimeWidget end;

    @Override
    protected void createOptionSection(Composite parent) {
        start = widgetCreator.createDateTimeWidget(parent,
            "Start Date (Linked)", null, null, null, SWT.DATE);
        end = widgetCreator.createDateTimeWidget(parent, "End Date (Linked)",
            null, null, null, SWT.DATE);
    }

    @Override
    protected List<Object> getPrintParams() {
        List<Object> params = new ArrayList<Object>();
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        return params;
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
        return new String[] { "Inventory ID", "Clinic", "Patient Number",
            "Link Date", "Sample Type" };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> param = new ArrayList<String>();
        param.add("Start Date (Linked)");
        param.add("End Date (Linked)");
        return param;
    }

}
