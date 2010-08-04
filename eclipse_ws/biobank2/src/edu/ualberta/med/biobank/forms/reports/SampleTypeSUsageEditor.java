package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

public class SampleTypeSUsageEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.SampleTypeSUsageEditor";

    @Override
    protected int[] getColumnWidths() {
        return new int[] { 100, 100 };
    }

    @Override
    protected void createOptionSection(Composite parent) {
    }

    @Override
    protected List<Object> getParams() {
        return new ArrayList<Object>();
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { "Sample Type", "Study" };
    }

    @Override
    protected List<String> getParamNames() {
        return new ArrayList<String>();
    }

}
