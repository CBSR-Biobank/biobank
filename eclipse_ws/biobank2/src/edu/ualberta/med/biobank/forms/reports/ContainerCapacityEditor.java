package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

public class ContainerCapacityEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.ContainerCapacityEditor";

    @Override
    protected void createOptionSection(Composite parent) {
        //
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { "Container", "Capacity", "# In Use", "% In Use" };
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

    @Override
    protected void onReset() throws Exception {
    }

}
