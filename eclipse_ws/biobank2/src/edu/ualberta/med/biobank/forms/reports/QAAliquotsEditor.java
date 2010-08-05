package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.TopContainerListWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class QAAliquotsEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.QAAliquotsEditor";

    DateTimeWidget start;
    DateTimeWidget end;
    ComboViewer sampleType;
    IObservableValue numAliquots;
    TopContainerListWidget topContainers;

    @Override
    protected int[] getColumnWidths() {
        return new int[] { 100, 100, 100, 100, 100 };
    }

    @Override
    protected void createOptionSection(Composite parent) throws Exception {
        start = widgetCreator.createDateTimeWidget(parent,
            "Start Date (Linked)", null, null, null);
        end = widgetCreator.createDateTimeWidget(parent, "End Date (Linked)",
            null, null, null);
        widgetCreator.createLabel(parent, "Top Containers");
        topContainers = new TopContainerListWidget(parent, SWT.NONE);
        sampleType = createSampleTypeComboOption("Sample Type", parent);
        createValidatedIntegerText("# Aliquots", parent);
    }

    @Override
    protected List<Object> getParams() throws Exception {
        List<Object> params = new ArrayList<Object>();
        if (start.getDate() == null)
            params.add(new Date(0));
        else
            params.add(start.getDate());
        if (end.getDate() == null)
            params.add(new Date());
        else
            params.add(end.getDate());
        params.add(((SampleTypeWrapper) ((IStructuredSelection) sampleType
            .getSelection()).getFirstElement()).getNameShort());
        params.add(topContainers.getSelectedContainers());
        params.add(Integer.parseInt((String) numAliquots.getValue()));
        return params;
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        List<Object> params = getParams();
        Object comboInfo = params.get(2);
        params.set(2, params.get(3));
        params.set(3, comboInfo);
        return params;
    }

    protected ComboViewer createSampleTypeComboOption(String labelText,
        Composite parent) throws ApplicationException {
        Collection<SampleTypeWrapper> sampleTypeWrappers = SessionManager
            .getInstance().getCurrentSite().getAllSampleTypeCollection(true);
        ComboViewer widget = widgetCreator
            .createComboViewerWithNoSelectionValidator(parent, labelText,
                sampleTypeWrappers, null, "No selection");
        widget.setLabelProvider(new BiobankLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((SampleTypeWrapper) element).getNameShort();
            }
        });

        widget.getCombo().select(0);
        return widget;
    }

    protected BiobankText createValidatedIntegerText(String labelText,
        Composite parent) {
        numAliquots = new WritableValue("", String.class);
        BiobankText widget = (BiobankText) widgetCreator
            .createBoundWidgetWithLabel(parent, BiobankText.class, SWT.BORDER,
                labelText, new String[0], numAliquots,
                new IntegerNumberValidator("Enter a valid integer.", false));
        return widget;
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { "Location", "Inventory ID", "Patient",
            "Date Processed", "Sample Type" };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> paramNames = new ArrayList<String>();
        paramNames.add("Start Date (Linked)");
        paramNames.add("End Date (Linked)");
        paramNames.add("Top Container");
        paramNames.add("Sample Type");
        paramNames.add("# Aliquots");
        return paramNames;
    }

}
