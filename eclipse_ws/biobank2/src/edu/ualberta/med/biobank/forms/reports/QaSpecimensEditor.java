package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.TopContainerListWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class QaSpecimensEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.QAAliquotsEditor";

    DateTimeWidget start;
    DateTimeWidget end;
    ComboViewer sampleType;
    IObservableValue numAliquots;
    private IObservableValue listStatus = new WritableValue(Boolean.TRUE,
        Boolean.class);
    TopContainerListWidget topContainers;

    @Override
    protected void createOptionSection(Composite parent) throws Exception {
        start = widgetCreator.createDateTimeWidget(parent,
            "Start Date (Linked)", null, null, null, SWT.DATE);
        end = widgetCreator.createDateTimeWidget(parent, "End Date (Linked)",
            null, null, null, SWT.DATE);
        topContainers = new TopContainerListWidget(parent, toolkit);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), listStatus, "Top Container List Empty");
        topContainers.addSelectionChangedListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                listStatus.setValue(topContainers.getEnabled());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        sampleType = createSampleTypeComboOption("Sample Type", parent);
        createValidatedIntegerText("# Aliquots", parent);
    }

    @Override
    protected void initReport() throws Exception {
        List<Object> params = new ArrayList<Object>();
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        params.add(((SpecimenTypeWrapper) ((IStructuredSelection) sampleType
            .getSelection()).getFirstElement()).getNameShort());
        report.setContainerList(ReportsEditor
            .containerIdsToString(topContainers.getSelectedContainerIds()));
        params.add(Integer.parseInt((String) numAliquots.getValue()));
        report.setParams(params);
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        List<Object> params = new ArrayList<Object>();
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        params.add(topContainers.getSelectedContainerNames());
        params.add(((SpecimenTypeWrapper) ((IStructuredSelection) sampleType
            .getSelection()).getFirstElement()).getNameShort());
        params.add(Integer.parseInt((String) numAliquots.getValue()));
        return params;
    }

    protected ComboViewer createSampleTypeComboOption(String labelText,
        Composite parent) throws ApplicationException {
        Collection<SpecimenTypeWrapper> sampleTypeWrappers = SpecimenTypeWrapper
            .getAllSpecimenTypes(SessionManager.getAppService(), true);
        ComboViewer widget = widgetCreator.createComboViewer(parent, labelText,
            sampleTypeWrappers, null, "No selection", null,
            new BiobankLabelProvider());
        widget.setLabelProvider(new BiobankLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((SpecimenTypeWrapper) element).getNameShort();
            }
        });

        widget.getCombo().select(0);
        return widget;
    }

    protected BgcBaseText createValidatedIntegerText(String labelText,
        Composite parent) {
        numAliquots = new WritableValue("", String.class);
        BgcBaseText widget = (BgcBaseText) widgetCreator
            .createBoundWidgetWithLabel(parent, BgcBaseText.class, SWT.BORDER,
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
