package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;

public class SpecimenTypeReport2Editor extends ReportsEditor {
    @SuppressWarnings("nls")
    public static String ID =
        "edu.ualberta.med.biobank.editors.SampleTypeSUsageEditor";

    @Override
    protected void createOptionSection(Composite parent) {
    }

    @Override
    protected void initReport() {
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] {
            SpecimenType.NAME.format(1).toString(),
            Study.NAME.format(1).toString() };
    }

    @Override
    protected List<String> getParamNames() {
        return new ArrayList<String>();
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        return new ArrayList<Object>();
    }
}
