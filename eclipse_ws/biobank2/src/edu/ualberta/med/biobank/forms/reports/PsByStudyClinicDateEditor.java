package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.util.DateGroup;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class PsByStudyClinicDateEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.PsByStudyClinicDateEditor"; //$NON-NLS-1$
    private ComboViewer dateRangeCombo;
    protected DateTimeWidget start;
    protected DateTimeWidget end;

    @Override
    protected String[] getColumnNames() {
        return new String[] {
            "Study", //$NON-NLS-1$
            "Clinic", //$NON-NLS-1$
            ((IStructuredSelection) dateRangeCombo.getSelection())
                .getFirstElement().toString(), "Total" }; //$NON-NLS-1$
    }

    @Override
    protected void createOptionSection(Composite parent) {
        dateRangeCombo = widgetCreator
            .createComboViewer(parent, "Group By", //$NON-NLS-1$
                Arrays.asList(DateGroup.values()), null,
                new BiobankLabelProvider());
        dateRangeCombo.getCombo().select(0);
        start = widgetCreator.createDateTimeWidget(parent,
            "Start Date (Linked)", null, null, null, SWT.DATE); //$NON-NLS-1$
        end = widgetCreator.createDateTimeWidget(parent, "End Date (Linked)", //$NON-NLS-1$
            null, null, null, SWT.DATE);
    }

    @Override
    protected void initReport() {
        List<Object> params = new ArrayList<Object>();
        report
            .setGroupBy(((IStructuredSelection) dateRangeCombo.getSelection())
                .getFirstElement().toString());
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        report.setParams(params);
    }

    @Override
    protected List<String> getParamNames() {
        List<String> param = new ArrayList<String>();
        param.add("Group By"); //$NON-NLS-1$
        param.add("Start Date (Linked)"); //$NON-NLS-1$
        param.add("End Date (Linked)"); //$NON-NLS-1$
        return param;
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        List<Object> params = new ArrayList<Object>();
        params.add(((IStructuredSelection) dateRangeCombo.getSelection())
            .getFirstElement().toString());
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        return params;
    }

}
