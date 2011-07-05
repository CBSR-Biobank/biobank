package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.util.DateGroup;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.TopContainerListWidget;

public class AliquotsByStudyClinicDateEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.AliquotsByStudyClinicDateEditor";
    private ComboViewer dateRangeCombo;
    protected DateTimeWidget start;
    protected DateTimeWidget end;
    protected TopContainerListWidget topContainers;
    private IObservableValue listStatus = new WritableValue(Boolean.TRUE,
        Boolean.class);

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
        dateRangeCombo = widgetCreator
            .createComboViewer(parent, "Group By",
                Arrays.asList(DateGroup.values()), null,
                new BiobankLabelProvider());
        dateRangeCombo.getCombo().select(0);
        topContainers = new TopContainerListWidget(parent, toolkit);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), listStatus, "Top Container List Empty");
        topContainers.addSelectionChangedListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                listStatus.setValue(topContainers.getEnabled());
            }
        });
        start = widgetCreator.createDateTimeWidget(parent,
            "Start Date (Linked)", null, null, null, SWT.DATE);
        end = widgetCreator.createDateTimeWidget(parent, "End Date (Linked)",
            null, null, null, SWT.DATE);

    }

    @Override
    protected void initReport() {
        List<Object> params = new ArrayList<Object>();
        report
            .setGroupBy(((IStructuredSelection) dateRangeCombo.getSelection())
                .getFirstElement().toString());
        report.setContainerList(ReportsEditor
            .containerIdsToString(topContainers.getSelectedContainerIds()));
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        report.setParams(params);
    }

    @Override
    protected List<String> getParamNames() {
        List<String> param = new ArrayList<String>();
        param.add("Group By");
        param.add("Top Container");
        param.add("Start Date (Linked)");
        param.add("End Date (Linked)");
        return param;
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        List<Object> params = new ArrayList<Object>();
        params.add(((IStructuredSelection) dateRangeCombo.getSelection())
            .getFirstElement().toString());
        params.add(topContainers.getSelectedContainerNames());
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        return params;
    }

}
