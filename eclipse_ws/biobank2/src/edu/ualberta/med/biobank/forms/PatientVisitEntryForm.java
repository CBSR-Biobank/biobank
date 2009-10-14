package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvInfoDataWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper.PvInfoPvInfoData;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.validators.DateNotNulValidator;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.widgets.ComboAndQuantity;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.PvSampleSourceEntryWidget;
import edu.ualberta.med.biobank.widgets.SelectMultiple;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;

public class PatientVisitEntryForm extends BiobankEntryForm {

    private static Logger LOGGER = Logger.getLogger(PatientVisitEntryForm.class
        .getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.PatientVisitEntryForm";

    public static final String MSG_NEW_PATIENT_VISIT_OK = "Creating a new patient visit record.";

    public static final String MSG_PATIENT_VISIT_OK = "Editing an existing patient visit record.";

    public static final String MSG_NO_VISIT_NUMBER = "Visit must have a number";

    private PatientVisitAdapter patientVisitAdapter;

    private PatientVisitWrapper patientVisitWrapper;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    class CombinedPvInfo {
        PvInfoPvInfoData pvInfoPvInfoData;
        Control control;
        String[] possibleValues;
        String value;

        public CombinedPvInfo(PvInfoPvInfoData pvInfoPvInfoData,
            Control control, String[] possibleValues) {
            this.pvInfoPvInfoData = pvInfoPvInfoData;
            this.control = control;
            this.possibleValues = possibleValues;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    private List<CombinedPvInfo> combinedPvInfoList;

    private DateTimeWidget dateDrawn;

    private DateTimeWidget dateProcessed;

    private DateTimeWidget dateReceived;

    private ComboViewer clinicsComboViewer;

    private Text commentsText;

    private PvSampleSourceEntryWidget pvSampleSourceEntryWidget;

    private PatientWrapper patientWrapper;

    @Override
    public void init() {
        Assert.isTrue(adapter instanceof PatientVisitAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientVisitAdapter = (PatientVisitAdapter) adapter;
        patientVisitWrapper = patientVisitAdapter.getWrapper();
        patientWrapper = patientVisitAdapter.getParentFromClass(
            PatientAdapter.class).getWrapper();
        retrieve();
        String tabName;
        if (patientVisitWrapper.isNew()) {
            tabName = "New Patient Visit";
        } else {
            tabName = "Visit " + patientVisitWrapper.getFormattedDateDrawn();
        }
        setPartName(tabName);
    }

    private void retrieve() {
        try {
            patientVisitWrapper.reload();
            patientWrapper.reload();
        } catch (Exception e) {
            LOGGER.error("Error while retrieving patient visit "
                + patientVisitAdapter.getWrapper().getDateDrawn(), e);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Patient Visit Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_PATIENT_VISIT));
        createMainSection(patientWrapper.getStudy());
        createSourcesSection();
        if (patientVisitWrapper.isNew()) {
            setDirty(true);
        }
    }

    private void createMainSection(StudyWrapper study) throws Exception {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Label siteLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Site");
        FormUtils.setTextValue(siteLabel, patientVisitWrapper.getPatient()
            .getStudy().getWrappedObject().getSite().getName());

        if (patientVisitWrapper.getId() == null) {
            // choose clinic for new visit
            List<ClinicWrapper> studyClinics = study.getClinicCollection();
            ClinicWrapper selectedClinic = patientVisitWrapper.getClinic();
            if (studyClinics.size() == 1) {
                selectedClinic = studyClinics.get(0);
            }
            clinicsComboViewer = createCComboViewerWithNoSelectionValidator(
                client, "Clinic", studyClinics, selectedClinic,
                "A clinic should be selected");
        } else {
            Label clinicLabel = (Label) createWidget(client, Label.class,
                SWT.NONE, "Clinic");
            if (patientVisitWrapper.getClinic() != null) {
                clinicLabel.setText(patientVisitWrapper.getClinic().getName());
            }
        }

        dateDrawn = createDateTimeWidget(client, "Date Drawn",
            patientVisitWrapper.getDateDrawn(), false,
            "Date drawn should be set");

        firstControl = dateDrawn;

        Date processedDate = patientVisitWrapper.getDateProcessed();
        if (processedDate == null) {
            processedDate = new Date();
        }
        dateProcessed = createDateTimeWidget(client, "Date Processed",
            processedDate, false, "Date processed should be set");

        dateReceived = createDateTimeWidget(client, "Date Received",
            patientVisitWrapper.getDateReceived(), false,
            "Date received should be set");

        createPvDataSection(client);

        commentsText = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.MULTI, "Comments", null, BeansObservables.observeValue(
                patientVisitWrapper, "comments"), null);
    }

    private void createPvDataSection(Composite client) {
        combinedPvInfoList = new ArrayList<CombinedPvInfo>();
        List<PvInfoPvInfoData> pvInfoPvInfoDatas = patientVisitWrapper
            .getPvInfoWithValues();
        if (pvInfoPvInfoDatas != null) {
            for (PvInfoPvInfoData infos : pvInfoPvInfoDatas) {
                CombinedPvInfo cPvInfo = new CombinedPvInfo(infos, null, null);
                Control control = getControlForPvInfoType(client, cPvInfo);
                cPvInfo.control = control;
                combinedPvInfoList.add(cPvInfo);
                if (control != null) {
                    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
                    control.setLayoutData(gd);
                    controls.put(infos.getPvInfo().getLabel(), control);
                }
            }
        }
    }

    private Control getControlForPvInfoType(Composite client,
        CombinedPvInfo cPvInfo) {
        PvInfoWrapper pvInfo = cPvInfo.pvInfoPvInfoData.getPvInfo();
        PvInfoDataWrapper pvInfoData = cPvInfo.pvInfoPvInfoData.getPvInfoData();

        String possibleValues = pvInfo.getPossibleValues();
        if (possibleValues != null) {
            cPvInfo.possibleValues = possibleValues.split(";");
        }

        if (pvInfoData != null) {
            cPvInfo.value = pvInfoData.getValue();
        }

        Integer typeId = pvInfo.getPvInfoType().getId();
        switch (typeId) {
        case 1: // number
            return createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
                pvInfo.getLabel(), null, PojoObservables.observeValue(cPvInfo,
                    "value"), new DoubleNumberValidator(
                    "You should select a valid number"));
        case 2: // text
            return createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
                pvInfo.getLabel(), null, PojoObservables.observeValue(cPvInfo,
                    "value"), null);
        case 3: // date_time
            return createDateTimeWidget(client, pvInfo.getLabel(),
                DateFormatter.parseToDateTime(cPvInfo.value), true, null);
        case 4: // select_single
            return createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
                pvInfo.getLabel(), cPvInfo.possibleValues, PojoObservables
                    .observeValue(cPvInfo, "value"), null);
        case 5: // select_multiple
            createFieldLabel(client, pvInfo.getLabel());
            SelectMultiple s = new SelectMultiple(client, SWT.BORDER,
                cPvInfo.possibleValues, selectionListener);
            s.adaptToToolkit(toolkit, true);
            if (cPvInfo.value != null) {
                s.setSelections(cPvInfo.value.split(";"));
            }
            return s;
        case 6: // select_single_and_quantity_1_5_1
            createFieldLabel(client, pvInfo.getLabel());
            ComboAndQuantity c = new ComboAndQuantity(client, SWT.BORDER);
            c.adaptToToolkit(toolkit, true);
            if (possibleValues != null) {
                c.addValues(cPvInfo.possibleValues);
            }
            if (cPvInfo.value != null) {
                String[] values = cPvInfo.value.split(" ");
                Assert.isTrue(values.length == 2);
                c.setText(values[0], Integer.parseInt(values[1]));
            }
            return c;
        default:
            Assert.isTrue(false, "Invalid pvInfo type: " + typeId);
        }
        return null;
    }

    private void createFieldLabel(Composite parent, String label) {
        Label labelWidget = toolkit.createLabel(parent, label + ":", SWT.LEFT);
        labelWidget.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
    }

    private DateTimeWidget createDateTimeWidget(Composite client,
        String nameLabel, Date date, boolean canBeEmpty,
        final String emptyMessage) {
        Label label = toolkit.createLabel(client, nameLabel + ":", SWT.NONE);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        final DateTimeWidget widget = new DateTimeWidget(client, SWT.NONE, date);
        widget.addSelectionListener(selectionListener);
        widget.adaptToToolkit(toolkit, true);

        if (!canBeEmpty) {
            final IObservableValue dateValue = new WritableValue(null,
                Date.class);
            DateNotNulValidator validator = new DateNotNulValidator(
                emptyMessage);
            validator.setControlDecoration(FormUtils.createDecorator(label,
                validator.getErrorMessage()));
            UpdateValueStrategy uvs = new UpdateValueStrategy();
            uvs.setAfterConvertValidator(validator);
            bindValue(new WritableValue(null, Date.class), dateValue, uvs, uvs);
            dateValue.setValue(date);
            widget.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    dateValue.setValue(widget.getDate());
                }
            });
        }
        return widget;
    }

    private void createSourcesSection() {
        Composite client = createSectionWithClient("Source Vessels");

        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        pvSampleSourceEntryWidget = new PvSampleSourceEntryWidget(client,
            SWT.NONE, patientVisitWrapper.getPvSampleSourceCollection(),
            patientVisitWrapper, toolkit);
        pvSampleSourceEntryWidget.addSelectionChangedListener(listener);
    }

    @Override
    protected String getOkMessage() {
        return (patientVisitWrapper.isNew()) ? MSG_NEW_PATIENT_VISIT_OK
            : MSG_PATIENT_VISIT_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        PatientAdapter patientAdapter = (PatientAdapter) patientVisitAdapter
            .getParent();
        patientVisitWrapper.setPatient(patientAdapter.getWrapper());
        if (clinicsComboViewer != null) {
            IStructuredSelection clinicSelection = (IStructuredSelection) clinicsComboViewer
                .getSelection();
            if ((clinicSelection != null) && (clinicSelection.size() > 0)) {
                patientVisitWrapper.setClinic((ClinicWrapper) clinicSelection
                    .getFirstElement());
            } else {
                patientVisitWrapper.setClinic((Clinic) null);
            }
        }

        patientVisitWrapper.setDateDrawn(dateDrawn.getDate());
        patientVisitWrapper.setDateProcessed(dateProcessed.getDate());
        patientVisitWrapper.setDateReceived(dateReceived.getDate());
        patientVisitWrapper.setComments(commentsText.getText());

        // FIXME get csm_user_id and set it to the Patient Visit at insert

        setPvInfoDatas();
        patientVisitWrapper
            .setPvSampleSourceCollection(pvSampleSourceEntryWidget
                .getPvSampleSources());
        patientVisitWrapper.persist();

        patientAdapter.performExpand();
    }

    private void setPvInfoDatas() {
        List<PvInfoDataWrapper> pvDataCollection = new ArrayList<PvInfoDataWrapper>();

        for (CombinedPvInfo combinedPvInfo : combinedPvInfoList) {
            setPvInfoValueFromControlType(combinedPvInfo);
            PvInfoDataWrapper pvInfoData = combinedPvInfo.pvInfoPvInfoData
                .getPvInfoData();
            if (combinedPvInfo.value == null
                || (combinedPvInfo.value.length() == 0 && pvInfoData == null))
                continue;

            PvInfoWrapper pvInfo = combinedPvInfo.pvInfoPvInfoData.getPvInfo();
            if (pvInfoData == null) {
                pvInfoData = new PvInfoDataWrapper(appService);
                pvInfoData.setPvInfo(pvInfo);
                pvInfoData.setPatientVisit(patientVisitWrapper
                    .getWrappedObject());
            }
            pvInfoData.setValue(combinedPvInfo.value);
            pvDataCollection.add(pvInfoData);
        }
        patientVisitWrapper.setPvInfoDataCollection(pvDataCollection);
    }

    private void setPvInfoValueFromControlType(CombinedPvInfo combinedPvInfo) {
        // for text and combo, the databinding is used
        if (combinedPvInfo.control instanceof DateTimeWidget) {
            combinedPvInfo.value = ((DateTimeWidget) combinedPvInfo.control)
                .getText();
        } else if (combinedPvInfo.control instanceof ComboAndQuantity) {
            combinedPvInfo.value = ((ComboAndQuantity) combinedPvInfo.control)
                .getText();
        } else if (combinedPvInfo.control instanceof SelectMultiple) {
            String[] values = ((SelectMultiple) combinedPvInfo.control)
                .getSelections();
            combinedPvInfo.value = StringUtils.join(values, ";");
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return PatientVisitViewForm.ID;
    }

    @Override
    public void setFocus() {
        firstControl.setFocus();
    }
}
