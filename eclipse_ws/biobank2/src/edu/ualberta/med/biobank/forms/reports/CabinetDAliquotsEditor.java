package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.util.DateGroup;

public class CabinetDAliquotsEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.CabinetDAliquotsEditor";
    private ComboViewer dateRangeCombo;

    @Override
    protected int[] getColumnWidths() {
        return new int[] { 100, 100, 100, 100 };
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] {
            "Study",
            "Clinic",
            ((IStructuredSelection) dateRangeCombo.getSelection())
                .getFirstElement().toString(), "Total" };
    }

    @Override
    protected void createOptionSection(Composite parent) {
        dateRangeCombo = widgetCreator.createComboViewer(parent, "Group By",
            Arrays.asList(DateGroup.values()), null);
        dateRangeCombo.getCombo().select(0);
    }

    @Override
    protected List<Object> getParams() {
        List<Object> params = new ArrayList<Object>();
        params.add(((IStructuredSelection) dateRangeCombo.getSelection())
            .getFirstElement().toString());
        return params;
    }

    @Override
    protected List<String> getParamNames() {
        List<String> param = new ArrayList<String>();
        param.add("Group By");
        return param;
    }

}
