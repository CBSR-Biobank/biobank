package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.ComboAndQuantityWidget;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.SelectMultipleWidget;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable.ColumnsShown;
import edu.ualberta.med.biobank.widgets.infotables.entry.CEventSpecimenEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class CollectionEventEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.CollectionEventEntryForm";

    public static final String MSG_NEW_PATIENT_VISIT_OK = Messages
        .getString("CollectionEventEntryForm.creation.msg");

    public static final String MSG_PATIENT_VISIT_OK = Messages
        .getString("CollectionEventEntryForm.edition.msg");

    private CollectionEventAdapter ceventAdapter;

    private CollectionEventWrapper cevent;

    private PatientWrapper patient;

    private static class FormPvCustomInfo extends PvAttrCustom {
        private Control control;
    }

    private List<FormPvCustomInfo> pvCustomInfoList;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    private ComboViewer activityStatusComboViewer;

    private CEventSpecimenEntryInfoTable specimensTable;
    private BiobankText visitNumberText;

    private DateTimeWidget timeDrawnWidget;

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof CollectionEventAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        ceventAdapter = (CollectionEventAdapter) adapter;
        cevent = (CollectionEventWrapper) getModelObject();
        patient = cevent.getPatient();

        SessionManager.logEdit(cevent);
        String tabName;
        if (cevent.isNew()) {
            tabName = Messages.getString("CollectionEventEntryForm.title.new");
            cevent.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
            cevent.setVisitNumber(CollectionEventWrapper.getNextVisitNumber(
                appService, cevent));
        } else {
            tabName = Messages.getString("CollectionEventEntryForm.title.edit",
                cevent.getVisitNumber());
        }

        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("CollectionEventEntryForm.main.title"));
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));
        createMainSection();
        createSpecimensSection();
        if (cevent.isNew()) {
            setDirty(true);
        }
    }

    private void createMainSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("CollectionEventEntryForm.field.study.label"),
            patient.getStudy().getName());

        createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("CollectionEventEntryForm.field.patient.label"),
            patient.getPnumber());

        visitNumberText = (BiobankText) createBoundWidgetWithLabel(
            client,
            BiobankText.class,
            SWT.NONE,
            Messages
                .getString("CollectionEventEntryForm.field.visitNumber.label"),
            null,
            cevent,
            CollectionEventPeer.VISIT_NUMBER.getName(),
            new IntegerNumberValidator(
                Messages
                    .getString("CollectionEventEntryForm.field.visitNumber.validation.msg"),
                false));

        visitNumberText.addSelectionChangedListener(listener);
        setFirstControl(visitNumberText);

        activityStatusComboViewer = createComboViewer(
            client,
            Messages.getString("label.activity"),
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            cevent.getActivityStatus(),
            Messages
                .getString("CollectionEventEntryForm.field.activity.validation.msg"),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    if (!selectedObject.equals(cevent.getActivityStatus()))
                        setDirty(true);
                    cevent
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

        widgetCreator.createLabel(client,
            Messages.getString("CollectionEventEntryForm.timeDrawn.label"));
        timeDrawnWidget = new DateTimeWidget(client, SWT.DATE | SWT.TIME,
            new Date());
        toolkit.adapt(timeDrawnWidget);

        createPvDataSection(client);

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            Messages.getString("label.comments"), null, cevent,
            CollectionEventPeer.COMMENT.getName(), null);
    }

    private void createSpecimensSection() {
        Section section = createSection(Messages
            .getString("CollectionEventEntryForm.specimens.title"));
        specimensTable = new CEventSpecimenEntryInfoTable(section,
            cevent.getOriginalSpecimenCollection(true),
            ColumnsShown.SOURCE_SPECIMENS);
        specimensTable.adaptToToolkit(toolkit, true);
        specimensTable.addSelectionChangedListener(listener);

        try {
            final List<SpecimenTypeWrapper> allSpecimenTypes = SpecimenTypeWrapper
                .getAllSpecimenTypes(SessionManager.getAppService(), true);
            specimensTable.addEditSupport(cevent.getPatient().getStudy()
                .getSourceSpecimenCollection(true), allSpecimenTypes);
            addSectionToolbar(section,
                Messages
                    .getString("CollectionEventEntryForm.specimens.add.title"),
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        specimensTable.addOrEditSpecimen(true, null, cevent
                            .getPatient().getStudy()
                            .getSourceSpecimenCollection(true),
                            allSpecimenTypes, cevent, timeDrawnWidget.getDate());
                    }
                });
        } catch (ApplicationException e) {
            BiobankPlugin
                .openAsyncError(
                    Messages
                        .getString("CollectionEventEntryForm.specimenstypes.error.msg"),
                    e);
        }
        section.setClient(specimensTable);
    }

    private void createPvDataSection(Composite client) throws Exception {
        StudyWrapper study = cevent.getPatient().getStudy();
        String[] labels = study.getStudyEventAttrLabels();
        if (labels == null)
            return;

        pvCustomInfoList = new ArrayList<FormPvCustomInfo>();

        for (String label : labels) {
            FormPvCustomInfo pvCustomInfo = new FormPvCustomInfo();
            pvCustomInfo.setLabel(label);
            pvCustomInfo.setType(study.getStudyEventAttrType(label));
            pvCustomInfo.setAllowedValues(study
                .getStudyEventAttrPermissible(label));
            pvCustomInfo.setValue(cevent.getEventAttrValue(label));
            pvCustomInfo.control = getControlForLabel(client, pvCustomInfo);
            pvCustomInfoList.add(pvCustomInfo);
        }
    }

    private Control getControlForLabel(Composite client,
        FormPvCustomInfo pvCustomInfo) {
        Control control;
        if (EventAttrTypeEnum.NUMBER == pvCustomInfo.getType()) {
            control = createBoundWidgetWithLabel(client, BiobankText.class,
                SWT.NONE, pvCustomInfo.getLabel(), null, pvCustomInfo, "value",
                new DoubleNumberValidator("You should select a valid number"));
        } else if (EventAttrTypeEnum.TEXT == pvCustomInfo.getType()) {
            control = createBoundWidgetWithLabel(client, BiobankText.class,
                SWT.NONE, pvCustomInfo.getLabel(), null, pvCustomInfo, "value",
                null);
        } else if (EventAttrTypeEnum.DATE_TIME == pvCustomInfo.getType()) {
            control = createDateTimeWidget(client, pvCustomInfo.getLabel(),
                DateFormatter.parseToDateTime(pvCustomInfo.getValue()), null,
                null);
        } else if (EventAttrTypeEnum.SELECT_SINGLE == pvCustomInfo.getType()) {
            control = createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
                pvCustomInfo.getLabel(), pvCustomInfo.getAllowedValues(),
                pvCustomInfo, "value", null);
        } else if (EventAttrTypeEnum.SELECT_MULTIPLE == pvCustomInfo.getType()) {
            createFieldLabel(client, pvCustomInfo.getLabel());
            SelectMultipleWidget s = new SelectMultipleWidget(client,
                SWT.BORDER, pvCustomInfo.getAllowedValues(), selectionListener);
            s.adaptToToolkit(toolkit, true);
            if (pvCustomInfo.getValue() != null) {
                s.setSelections(pvCustomInfo.getValue().split(";"));
            }
            control = s;
        } else {
            Assert.isTrue(false,
                "Invalid pvInfo type: " + pvCustomInfo.getType());
            return null;
        }
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        control.setLayoutData(gd);
        return control;
    }

    private void createFieldLabel(Composite parent, String label) {
        Label labelWidget = toolkit.createLabel(parent, label + ":", SWT.LEFT);
        labelWidget.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
    }

    @Override
    protected String getOkMessage() {
        return (cevent.isNew()) ? MSG_NEW_PATIENT_VISIT_OK
            : MSG_PATIENT_VISIT_OK;
    }

    @Override
    protected void doBeforeSave() throws Exception {
        PatientAdapter patientAdapter = (PatientAdapter) ceventAdapter
            .getParent();
        if (patientAdapter != null)
            cevent.setPatient(patientAdapter.getWrapper());
        cevent.addToOriginalSpecimenCollection(specimensTable
            .getAddedSpecimens());
        cevent.removeFromOriginalSpecimenCollection(specimensTable
            .getRemovedSpecimens());
        savePvCustomInfo();
    }

    @Override
    protected void saveForm() throws Exception {
        // create the origin info to be used
        if (specimensTable.getAddedSpecimens().size() > 0) {
            OriginInfoWrapper originInfo = new OriginInfoWrapper(
                SessionManager.getAppService());
            originInfo.setCenter(SessionManager.getUser()
                .getCurrentWorkingCenter());
            originInfo.persist();
            for (SpecimenWrapper spec : specimensTable.getAddedSpecimens()) {
                spec.setOriginInfo(originInfo);
            }
        }
        // save the collection event
        cevent.persist();
        SessionManager.updateAllSimilarNodes(ceventAdapter, true);
    }

    private void savePvCustomInfo() throws Exception {
        for (FormPvCustomInfo combinedPvInfo : pvCustomInfoList) {
            savePvInfoValueFromControlType(combinedPvInfo);
            String value = combinedPvInfo.getValue();
            if (value == null)
                continue;

            cevent.setEventAttrValue(combinedPvInfo.getLabel(), value);
        }
    }

    private void savePvInfoValueFromControlType(FormPvCustomInfo pvCustomInfo) {
        // for text and combo, the databinding is used
        if (pvCustomInfo.control instanceof DateTimeWidget) {
            pvCustomInfo.setValue(((DateTimeWidget) pvCustomInfo.control)
                .getText());
        } else if (pvCustomInfo.control instanceof ComboAndQuantityWidget) {
            pvCustomInfo
                .setValue(((ComboAndQuantityWidget) pvCustomInfo.control)
                    .getText());
        } else if (pvCustomInfo.control instanceof SelectMultipleWidget) {
            String[] values = ((SelectMultipleWidget) pvCustomInfo.control)
                .getSelections();
            pvCustomInfo.setValue(StringUtils.join(values, ";"));
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return CollectionEventViewForm.ID;
    }

    @Override
    protected void onReset() throws Exception {
        cevent.reset();

        if (cevent.isNew()) {
            cevent.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
            cevent.setVisitNumber(CollectionEventWrapper.getNextVisitNumber(
                appService, cevent));
        }

        patient.reset();
        cevent.setPatient(patient);

        GuiUtil.reset(activityStatusComboViewer, cevent.getActivityStatus());

        specimensTable.reload(cevent.getOriginalSpecimenCollection(true));
        resetPvCustomInfo();
    }

    private void resetPvCustomInfo() throws Exception {
        StudyWrapper study = cevent.getPatient().getStudy();
        String[] labels = study.getStudyEventAttrLabels();
        if (labels == null)
            return;

        for (FormPvCustomInfo pvCustomInfo : pvCustomInfoList) {
            pvCustomInfo.setValue(cevent.getEventAttrValue(pvCustomInfo
                .getLabel()));
            if (EventAttrTypeEnum.DATE_TIME == pvCustomInfo.getType()) {
                DateTimeWidget dateWidget = (DateTimeWidget) pvCustomInfo.control;
                dateWidget.setDate(DateFormatter.parseToDateTime(pvCustomInfo
                    .getValue()));
            } else if (EventAttrTypeEnum.SELECT_MULTIPLE == pvCustomInfo
                .getType()) {
                SelectMultipleWidget s = (SelectMultipleWidget) pvCustomInfo.control;
                if (pvCustomInfo.getValue() != null) {
                    s.setSelections(pvCustomInfo.getValue().split(";"));
                } else
                    s.setSelections(new String[] {});
            }
        }
    }
}
