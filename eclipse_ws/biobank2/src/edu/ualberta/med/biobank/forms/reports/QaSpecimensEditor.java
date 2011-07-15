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

    public static String ID = "edu.ualberta.med.biobank.editors.QAAliquotsEditor"; //$NON-NLS-1$

    private DateTimeWidget start;
    private DateTimeWidget end;
    private ComboViewer typesViewer;
    private IObservableValue numSpecimens;

    private IObservableValue listStatus = new WritableValue(Boolean.TRUE,
        Boolean.class);
    private TopContainerListWidget topContainers;
    private BgcBaseText numSpecimensText;

    @Override
    protected void createOptionSection(Composite parent) throws Exception {
        start = widgetCreator.createDateTimeWidget(parent,
            Messages.QAAliquotsEditor_start_label, null, null, null, SWT.DATE);
        end = widgetCreator.createDateTimeWidget(parent,
            Messages.QAAliquotsEditor_end_label, null, null, null, SWT.DATE);
        topContainers = new TopContainerListWidget(parent, toolkit);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), listStatus,
            Messages.QAAliquotsEditor_top_validation_msg);
        topContainers.addSelectionChangedListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                listStatus.setValue(topContainers.getEnabled());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        typesViewer = createSpecimenTypeComboOption(
            Messages.QAAliquotsEditor_specType_label, parent);
        createValidatedIntegerText(Messages.QAAliquotsEditor_nb_specs_label,
            parent);
    }

    @Override
    protected void initReport() throws Exception {
        List<Object> params = new ArrayList<Object>();
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        params.add(((SpecimenTypeWrapper) ((IStructuredSelection) typesViewer
            .getSelection()).getFirstElement()).getNameShort());
        report.setContainerList(ReportsEditor
            .containerIdsToString(topContainers.getSelectedContainerIds()));
        params.add(Integer.parseInt((String) numSpecimens.getValue()));
        report.setParams(params);
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        List<Object> params = new ArrayList<Object>();
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        params.add(topContainers.getSelectedContainerNames());
        params.add(((SpecimenTypeWrapper) ((IStructuredSelection) typesViewer
            .getSelection()).getFirstElement()).getNameShort());
        params.add(Integer.parseInt((String) numSpecimens.getValue()));
        return params;
    }

    protected ComboViewer createSpecimenTypeComboOption(String labelText,
        Composite parent) throws ApplicationException {
        Collection<SpecimenTypeWrapper> allSpecTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(SessionManager.getAppService(), true);
        ComboViewer widget = widgetCreator.createComboViewer(parent, labelText,
            allSpecTypes, null, Messages.QAAliquotsEditor_types_validation_msg,
            null, new BiobankLabelProvider());
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
        numSpecimens = new WritableValue("", String.class); //$NON-NLS-1$
        BgcBaseText widget = (BgcBaseText) widgetCreator
            .createBoundWidgetWithLabel(parent, BgcBaseText.class, SWT.BORDER,
                labelText, new String[0], numSpecimens,
                new IntegerNumberValidator(
                    Messages.QAAliquotsEditor_specNb_validation_msg, false));
        return widget;
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { Messages.QAAliquotsEditor_location_label,
            Messages.QAAliquotsEditor_inventoryid_label,
            Messages.QAAliquotsEditor_patient_label,
            Messages.QAAliquotsEditor_processed_label,
            Messages.QAAliquotsEditor_specType_label };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> paramNames = new ArrayList<String>();
        paramNames.add(Messages.QAAliquotsEditor_start_label);
        paramNames.add(Messages.QAAliquotsEditor_end_label);
        paramNames.add(Messages.QAAliquotsEditor_top_label);
        paramNames.add(Messages.QAAliquotsEditor_specType_label);
        paramNames.add(Messages.QAAliquotsEditor_nb_specs_label);
        return paramNames;
    }

    @Override
    protected void onReset() throws Exception {
        start.setDate(null);
        end.setDate(null);
        topContainers.reset();
        typesViewer.getCombo().deselectAll();
        numSpecimensText.setText(""); //$NON-NLS-1$
        super.onReset();
    }

}
