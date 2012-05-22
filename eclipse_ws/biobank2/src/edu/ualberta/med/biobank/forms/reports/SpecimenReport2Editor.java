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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.TopContainerListWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenReport2Editor extends ReportsEditor {
    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenReport2Editor.class);

    @SuppressWarnings("nls")
    public static String ID =
        "edu.ualberta.med.biobank.editors.SpecimenReport2Editor";

    private DateTimeWidget start;
    private DateTimeWidget end;
    private ComboViewer typesViewer;
    private IObservableValue numSpecimens;

    private final IObservableValue listStatus = new WritableValue(Boolean.TRUE,
        Boolean.class);
    private TopContainerListWidget topContainers;
    private BgcBaseText numSpecimensText;

    @SuppressWarnings("nls")
    // label
    private static final String START_DATE = i18n.tr("Start Date (Linked)");
    @SuppressWarnings("nls")
    // label
    private static final String END_DATE = i18n.tr("End Date (Linked)");
    private static final String SPECIMEN_TYPE = SpecimenType.NAME.format(1)
        .toString();
    @SuppressWarnings("nls")
    // label
    private static final String NUMBER_OF_SPECIMENS = i18n.tr("# Specimens");

    @SuppressWarnings("nls")
    @Override
    protected void createOptionSection(Composite parent) throws Exception {
        start = widgetCreator.createDateTimeWidget(parent,
            START_DATE, null, null, null, SWT.DATE);
        end = widgetCreator.createDateTimeWidget(parent,
            END_DATE, null, null, null, SWT.DATE);
        topContainers = new TopContainerListWidget(parent, toolkit);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), listStatus,
            // validation error message
            i18n.tr("Top Container List Empty"));
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
            SPECIMEN_TYPE, parent);
        createValidatedIntegerText(NUMBER_OF_SPECIMENS,
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
        @SuppressWarnings("nls")
        ComboViewer widget =
            widgetCreator.createComboViewer(parent, labelText,
                allSpecTypes, null,
                // validation error message
                i18n.tr("Type(s) should be selected"),
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

    @SuppressWarnings("nls")
    protected BgcBaseText createValidatedIntegerText(String labelText,
        Composite parent) {
        numSpecimens = new WritableValue(StringUtil.EMPTY_STRING, String.class);
        BgcBaseText widget = (BgcBaseText) widgetCreator
            .createBoundWidgetWithLabel(parent, BgcBaseText.class, SWT.BORDER,
                labelText, new String[0], numSpecimens,
                new IntegerNumberValidator(
                    // validation error message
                    i18n.tr("Enter a valid integer."), false));
        return widget;
    }

    @SuppressWarnings("nls")
    @Override
    protected String[] getColumnNames() {
        return new String[] {
            // table column name
            i18n.tr("Location"),
            Specimen.PropertyName.INVENTORY_ID.toString(),
            Patient.NAME.format(1).toString(),
            // table column name
            i18n.tr("Date Processed"),
            SpecimenType.NAME.singular().toString() };
    }

    @SuppressWarnings("nls")
    @Override
    protected List<String> getParamNames() {
        List<String> paramNames = new ArrayList<String>();
        paramNames.add(START_DATE);
        paramNames.add(END_DATE);
        paramNames.add(
            // label
            i18n.tr("Top Container"));
        paramNames.add(SPECIMEN_TYPE);
        paramNames.add(NUMBER_OF_SPECIMENS);
        return paramNames;
    }

    @Override
    public void setValues() throws Exception {
        start.setDate(null);
        end.setDate(null);
        topContainers.reset();
        typesViewer.getCombo().deselectAll();
        numSpecimensText.setText(StringUtil.EMPTY_STRING);
        super.setValues();
    }

}
