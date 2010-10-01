package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.TopContainerListWidget;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

public class AliquotsByStudyClinicEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.AliquotsByStudyClinicEditor";
    protected DateTimeWidget start;
    protected DateTimeWidget end;
    protected TopContainerListWidget topContainers;
    private IObservableValue listStatus = new WritableValue(Boolean.TRUE,
        Boolean.class);

    @Override
    protected int[] getColumnWidths() {
        return new int[] { 100, 100, 100 };
    }

    @Override
    protected void createOptionSection(Composite parent) {
        topContainers = new TopContainerListWidget(parent, SWT.NONE);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), listStatus, "Top Container List Empty");
        topContainers
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    listStatus.setValue(topContainers.getEnabled());
                }
            });
        topContainers.adaptToToolkit(toolkit, true);
        start = widgetCreator.createDateTimeWidget(parent,
            "Start Date (Linked)", null, null, null, SWT.DATE);
        end = widgetCreator.createDateTimeWidget(parent, "End Date (Linked)",
            null, null, null, SWT.DATE);
    }

    @Override
    protected List<Object> getParams() {
        List<Object> params = new ArrayList<Object>();
        params.add(topContainers.getSelectedContainers());
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        return params;
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { "Study", "Clinic", "Total" };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> paramNames = new ArrayList<String>();
        paramNames.add("Top Containers");
        paramNames.add("Start Date (Linked)");
        paramNames.add("End Date (Linked)");
        return paramNames;
    }

}
