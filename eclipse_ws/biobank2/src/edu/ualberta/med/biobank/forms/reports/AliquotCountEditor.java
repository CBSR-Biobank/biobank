package edu.ualberta.med.biobank.forms.reports;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.client.reports.AliquotCount;

public class AliquotCountEditor extends ReportsEditor<AliquotCount> {

    public static String ID = "edu.ualberta.med.biobank.editors.AliquotCountEditor";

    @Override
    protected int[] getColumnWidths() {
        return new int[] { 100, 100 };
    }

    @Override
    protected void createOptionSection(Composite parent) {
    }

    @Override
    protected List<Object> getParams() {
        return null;
    }

}
