package edu.ualberta.med.biobank.forms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.UpdateValueStrategy;
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
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvInfoDataWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.PvInfoData;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.validators.DateNotNulValidator;
import edu.ualberta.med.biobank.widgets.ComboAndQuantity;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.PvSampleSourceEntryWidget;
import edu.ualberta.med.biobank.widgets.SelectMultiple;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

public class PatientVisitEntryForm extends BiobankEntryForm {
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
        PvInfoWrapper pvInfo;
        PvInfoDataWrapper pvInfoData;
        Control control;

        public CombinedPvInfo() {
            pvInfo = null;
            pvInfoData = null;
            control = null;
        }
    }

    private ListOrderedMap combinedPvInfoMap;

    private DateTimeWidget dateDrawn;

    private DateTimeWidget dateProcessed;

    private DateTimeWidget dateReceived;

    private ComboViewer clinicsComboViewer;

    private Text commentsText;

    private PvSampleSourceEntryWidget pvSampleSourceEntryWidget;

    private PatientWrapper patientWrapper;

    public PatientVisitEntryForm() {
        super();
        combinedPvInfoMap = new ListOrderedMap();
    }

    @Override
    public void init() {
        Assert.isTrue(adapter instanceof PatientVisitAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientVisitAdapter = (PatientVisitAdapter) adapter;
        patientVisitWrapper = patientVisitAdapter.getWrapper();
        retrievePatientVisit();
        String tabName;
        if (patientVisitWrapper.isNew()) {
            tabName = "New Patient Visit";
        } else {
            tabName = "Visit " + patientVisitWrapper.getFormattedDateDrawn();
        }
        setPartName(tabName);
    }

    private void retrievePatientVisit() {
        try {
            patientVisitWrapper.reload();
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while retrieving patient visit "
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
        patientWrapper = retrievePatient();
        createMainSection(patientWrapper.getStudy());
        createSourcesSection();
        if (patientVisitWrapper.isNew()) {
            setDirty(true);
        }
    }

    private PatientWrapper retrievePatient() throws Exception {
        PatientWrapper patientWrapper = patientVisitAdapter.getParentFromClass(
            PatientAdapter.class).getWrapper();
        patientWrapper.reload();
        return patientWrapper;
    }

    private void createMainSection(StudyWrapper study) {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Label siteLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Site");
        FormUtils.setTextValue(siteLabel, patientVisitWrapper
            .getPatientWrapper().getStudy().getWrappedObject().getSite()
            .getName());

        if (patientVisitWrapper.getId() == null) {
            // choose clinic for new visit
            Collection<ContactWrapper> contacts = study.getContactCollection();
            ClinicWrapper selectedClinic = null;

            if (contacts.size() == 1) {
                selectedClinic = contacts.iterator().next().getClinicWrapper();
            }
            if (patientVisitWrapper.getClinic() != null) {
                for (ContactWrapper contact : contacts) {
                    ClinicWrapper clinic = contact.getClinicWrapper();
                    if (clinic.getId().equals(
                        patientVisitWrapper.getClinic().getId())) {
                        selectedClinic = clinic;
                        break;
                    }
                }
            }

            clinicsComboViewer = createCComboViewerWithNoSelectionValidator(
                client, "Clinic", contacts, selectedClinic,
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

        createPvDataSection(client, patientWrapper.getStudy());

        Label label = toolkit.createLabel(client, "Comments:", SWT.NONE);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        commentsText = toolkit.createText(client, patientVisitWrapper
            .getComments(), SWT.LEFT | SWT.MULTI);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        commentsText.setLayoutData(gd);
    }

    private void createPvDataSection(Composite client, StudyWrapper study) {
        if (study.getPvInfoCollection().size() > 0) {

            for (PvInfoWrapper pvInfo : study.getPvInfoCollection()) {
                CombinedPvInfo combinedPvInfo = new CombinedPvInfo();
                combinedPvInfo.pvInfo = pvInfo;
                combinedPvInfoMap.put(pvInfo.getId(), combinedPvInfo);
            }

            Collection<PvInfoDataWrapper> pvDataCollection = patientVisitWrapper
                .getPvInfoDataCollection();
            if (pvDataCollection != null) {
                for (PvInfoDataWrapper pvInfoData : pvDataCollection) {
                    Integer key = pvInfoData.getPvInfo().getId();
                    CombinedPvInfo combinedPvInfo = (CombinedPvInfo) combinedPvInfoMap
                        .get(key);
                    Assert.isNotNull(combinedPvInfo);
                    combinedPvInfo.pvInfoData = pvInfoData;
                }
            }

            MapIterator it = combinedPvInfoMap.mapIterator();
            while (it.hasNext()) {
                it.next();
                CombinedPvInfo combinedPvInfo = (CombinedPvInfo) it.getValue();
                int typeId = combinedPvInfo.pvInfo.getPvInfoType().getId();
                String possibleValues = combinedPvInfo.pvInfo
                    .getPossibleValues();
                String value = null;

                String[] pvalArr = null;
                if (possibleValues != null) {
                    pvalArr = possibleValues.split(";");
                }

                if (combinedPvInfo.pvInfoData != null) {
                    value = combinedPvInfo.pvInfoData.getValue();
                }

                Label labelWidget = toolkit.createLabel(client,
                    combinedPvInfo.pvInfo.getLabel() + ":", SWT.LEFT);
                labelWidget.setLayoutData(new GridData(
                    GridData.VERTICAL_ALIGN_BEGINNING));

                switch (typeId) {
                case 1: // number
                    combinedPvInfo.control = toolkit.createText(client, value,
                        SWT.LEFT);
                    break;

                case 2: // text
                    combinedPvInfo.control = toolkit.createText(client, value,
                        SWT.LEFT);
                    break;

                case 3: // date_time
                    SimpleDateFormat sdf = new SimpleDateFormat(
                        BioBankPlugin.DATE_TIME_FORMAT);

                    Date date = null;
                    if (value != null) {
                        try {
                            date = sdf.parse(value);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    DateTimeWidget w = new DateTimeWidget(client, SWT.NONE,
                        date);
                    w.addSelectionListener(selectionListener);
                    w.adaptToToolkit(toolkit, true);
                    combinedPvInfo.control = w;
                    break;

                case 4: // select_single
                    combinedPvInfo.control = createComboSection(client,
                        pvalArr, value);
                    break;

                case 5: // select_multiple
                    SelectMultiple s = new SelectMultiple(client, SWT.BORDER,
                        pvalArr);
                    s.adaptToToolkit(toolkit, true);
                    if (value != null) {
                        s.setSelections(value.split(";"));
                    }
                    combinedPvInfo.control = s;
                    break;

                case 6: // select_single_and_quantity_1_5_1
                    ComboAndQuantity c = new ComboAndQuantity(client,
                        SWT.BORDER);
                    c.adaptToToolkit(toolkit, true);
                    if (pvalArr != null) {
                        c.addValues(pvalArr);
                    }
                    if (value != null) {
                        String[] values = value.split(" ");
                        Assert.isTrue(values.length == 2);
                        c.setText(values[0], Integer.parseInt(values[1]));
                    }
                    combinedPvInfo.control = c;
                    break;

                default:
                    Assert.isTrue(false, "Invalid pvInfo type: " + typeId);
                }

                if (combinedPvInfo.control != null) {
                    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
                    combinedPvInfo.control.setLayoutData(gd);
                    controls.put(combinedPvInfo.pvInfo.getLabel(),
                        combinedPvInfo.control);
                }
            }
        }
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
            dbc.bindValue(new WritableValue(null, Date.class), dateValue, uvs,
                uvs);
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
            toolkit);
        pvSampleSourceEntryWidget.addSelectionChangedListener(listener);
    }

    private Control createComboSection(Composite client, String[] values,
        String selected) {

        Combo combo = new Combo(client, SWT.READ_ONLY);
        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        if (values != null) {
            combo.setItems(values);
        }

        combo.addSelectionListener(selectionListener);
        combo.addModifyListener(modifyListener);

        if (selected != null) {
            int count = 0;
            for (String value : values) {
                if (selected.equals(value)) {
                    combo.select(count);
                    break;
                }
                ++count;
            }
        }

        toolkit.adapt(combo, true, true);

        return combo;
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
        patientVisitWrapper.setPatientWrapper(patientAdapter.getWrapper());
        if (clinicsComboViewer != null) {
            IStructuredSelection clinicSelection = (IStructuredSelection) clinicsComboViewer
                .getSelection();
            if ((clinicSelection != null) && (clinicSelection.size() > 0)) {
                patientVisitWrapper.setClinic((Clinic) clinicSelection
                    .getFirstElement());
            } else {
                patientVisitWrapper.setClinic(null);
            }
        }

        patientVisitWrapper.setDateDrawn(dateDrawn.getDate());
        patientVisitWrapper.setDateProcessed(dateProcessed.getDate());
        patientVisitWrapper.setDateReceived(dateReceived.getDate());
        patientVisitWrapper.setComments(commentsText.getText());

        // FIXME get csm_user_id and set it to the Patient Visit at insert

        patientVisitWrapper.persist();

        // FIXME samplesources and pv infos datas could be done in the patient
        // visit persists method (if we send the information in parameters)
        savePvSampleSources();
        savePvInfoData();
        patientAdapter.performExpand();
    }

    private void savePvSampleSources() throws Exception {
        Collection<PvSampleSourceWrapper> ssCollection = pvSampleSourceEntryWidget
            .getPvSampleSources();
        SDKQuery query;
        SDKQueryResult result;

        removeDeletedPvSampleSources(ssCollection);

        Collection<PvSampleSourceWrapper> savedSsCollection = new HashSet<PvSampleSourceWrapper>();
        for (PvSampleSourceWrapper ss : ssCollection) {
            ss.setPatientVisit(patientVisitWrapper.getWrappedObject());
            if ((ss.getId() == null) || (ss.getId() == 0)) {
                query = new InsertExampleQuery(ss);
            } else {
                query = new UpdateExampleQuery(ss);
            }
            result = appService.executeQuery(query);
            savedSsCollection.add((PvSampleSourceWrapper) result
                .getObjectResult());
        }
        patientVisitWrapper.setPvSampleSourceCollection(savedSsCollection);
    }

    private void removeDeletedPvSampleSources(
        Collection<PvSampleSourceWrapper> ssCollection) throws Exception {
        // no need to remove if patientVisit is not yet in the database
        if (patientVisitWrapper.isNew())
            return;

        List<Integer> selectedPvSampleSourceIds = new ArrayList<Integer>();
        for (PvSampleSourceWrapper ss : ssCollection) {
            selectedPvSampleSourceIds.add(ss.getId());
        }

        SDKQuery query;
        if (patientVisitWrapper.getPvSampleSourceCollection() != null) {
            for (PvSampleSourceWrapper ss : patientVisitWrapper
                .getPvSampleSourceCollection()) {
                if (!selectedPvSampleSourceIds.contains(ss.getId())) {
                    query = new DeleteExampleQuery(ss);
                    appService.executeQuery(query);
                }
            }
        }
    }

    private void savePvInfoData() throws Exception {
        List<PvInfoDataWrapper> pvDataCollection = new ArrayList<PvInfoDataWrapper>();

        MapIterator it = combinedPvInfoMap.mapIterator();
        while (it.hasNext()) {
            it.next();
            CombinedPvInfo combinedPvInfo = (CombinedPvInfo) it.getValue();
            String value = "";

            if (combinedPvInfo.control instanceof Text) {
                value = ((Text) combinedPvInfo.control).getText();
            } else if (combinedPvInfo.control instanceof Combo) {
                if (combinedPvInfo.pvInfo.getPossibleValues() != null) {
                    String[] options = combinedPvInfo.pvInfo
                        .getPossibleValues().split(";");
                    int index = ((Combo) combinedPvInfo.control)
                        .getSelectionIndex();
                    if (index >= 0) {
                        Assert.isTrue(index < options.length,
                            "Invalid combo box selection " + index);
                        value = options[index];
                    }
                }
            } else if (combinedPvInfo.control instanceof DateTimeWidget) {
                value = ((DateTimeWidget) combinedPvInfo.control).getText();
            } else if (combinedPvInfo.control instanceof ComboAndQuantity) {
                value = ((ComboAndQuantity) combinedPvInfo.control).getText();
            } else if (combinedPvInfo.control instanceof SelectMultiple) {
                String[] values = ((SelectMultiple) combinedPvInfo.control)
                    .getSelections();
                value = StringUtils.join(values, ";");
            }

            if ((value == null) || (value.length() == 0))
                continue;

            PvInfoDataWrapper pvInfoData;

            if (combinedPvInfo.pvInfoData == null) {
                pvInfoData = new PvInfoDataWrapper(appService);
                pvInfoData.setPvInfo(combinedPvInfo.pvInfo);
                pvInfoData.setPatientVisit(patientVisitWrapper
                    .getWrappedObject());
            } else {
                pvInfoData = combinedPvInfo.pvInfoData;
            }
            pvInfoData.setValue(value);
            pvDataCollection.add(pvInfoData);
        }

        if (pvDataCollection.size() == 0)
            return;

        SDKQuery query;
        SDKQueryResult result;
        Collection<PvInfoData> savedPvDataCollection = new HashSet<PvInfoData>();
        for (PvInfoDataWrapper pvInfoData : pvDataCollection) {
            if (pvInfoData.isNew()) {
                query = new InsertExampleQuery(pvInfoData.getWrappedObject());
            } else {
                query = new UpdateExampleQuery(pvInfoData.getWrappedObject());
            }

            result = appService.executeQuery(query);
            savedPvDataCollection.add((PvInfoData) result.getObjectResult());
        }

        patientVisitWrapper.setPvInfoDataCollection(pvDataCollection);
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
