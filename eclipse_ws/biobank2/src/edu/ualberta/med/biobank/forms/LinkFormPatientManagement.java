package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class LinkFormPatientManagement {

    private boolean patientNumberTextModified = false;
    protected BiobankText patientNumberText;
    protected ComboViewer viewerCollectionEvents;

    // currentPatient
    protected PatientWrapper currentPatient;

    private WidgetCreator widgetCreator;

    private AbstractSpecimenAdminForm specimenAdminForm;

    private PatientTextCallback patientTextCallback;
    private Label patientLabel;
    private NonEmptyStringValidator patientValidator;
    private Label cEventTextLabel;
    private BiobankText cEventText;
    private Label cEventComboLabel;
    protected CollectionEventWrapper currentCEventSelected;
    protected String currentWorksheetNumber;
    protected boolean worksheetNumberModified;

    public LinkFormPatientManagement(WidgetCreator widgetCreator,
        AbstractSpecimenAdminForm specimenAdminForm) {
        this.widgetCreator = widgetCreator;
        this.specimenAdminForm = specimenAdminForm;
    }

    protected void createPatientNumberText(Composite parent) {
        patientLabel = widgetCreator.createLabel(parent,
            Messages.getString("ScanLink.patientNumber.label")); //$NON-NLS-1$
        patientLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        patientValidator = new NonEmptyStringValidator(
            Messages.getString("ScanLink.patientNumber.validationMsg"));//$NON-NLS-1$
        patientNumberText = (BiobankText) widgetCreator.createBoundWidget(
            parent, BiobankText.class, SWT.NONE, patientLabel, new String[0],
            new WritableValue("", String.class), patientValidator); //$NON-NLS-1$
        patientNumberText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (patientNumberTextModified) {
                    initFieldWithPatientSelection();
                    if (patientTextCallback != null) {
                        patientTextCallback.focusLost();
                    }
                }
                patientNumberTextModified = false;
                viewerCollectionEvents.getCombo().setFocus();
            }
        });
        patientNumberText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                patientNumberTextModified = true;
                if (patientTextCallback != null) {
                    patientTextCallback.textModified();
                }
            }
        });
        patientNumberText.addKeyListener(specimenAdminForm.textFieldKeyListener);
        setFirstControl();
    }

    protected void createCollectionEventWidgets(Composite compositeFields) {
        cEventComboLabel = widgetCreator.createLabel(compositeFields,
            Messages.getString("ScanLink.visit.number")); //$NON-NLS-1$
        viewerCollectionEvents = widgetCreator.createComboViewer(
            compositeFields, cEventComboLabel, null, null,
            Messages.getString("ScanLink.visit.validationMsg"), false, null, //$NON-NLS-1$
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    currentCEventSelected = (CollectionEventWrapper) selectedObject;
                }
            });
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        viewerCollectionEvents.getCombo().setLayoutData(gridData);

        viewerCollectionEvents.getCombo().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                IStructuredSelection selection = (IStructuredSelection) viewerCollectionEvents
                    .getSelection();
                if (selection != null && selection.size() > 0) {
                    CollectionEventWrapper ce = (CollectionEventWrapper) selection
                        .getFirstElement();
                    if (ce != null) {
                        specimenAdminForm.appendLogNLS(
                            "linkAssign.activitylog.visit.selection", ce //$NON-NLS-1$
                                .getVisitNumber());
                    }
                }
            }
        });

        // Will replace the combo in some specific situations (like cabinet
        // form):
        cEventTextLabel = widgetCreator.createLabel(compositeFields,
            Messages.getString("ScanLink.visit.label.drawn")); //$NON-NLS-1$
        cEventTextLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        cEventText = (BiobankText) widgetCreator.createWidget(compositeFields,
            BiobankText.class, SWT.NONE, ""); //$NON-NLS-1$
        cEventText.setEnabled(false);
        widgetCreator.hideWidget(cEventTextLabel);
        widgetCreator.hideWidget(cEventText);
    }

    protected CollectionEventWrapper getSelectedCollectionEvent() {
        return currentCEventSelected;
    }

    protected void initFieldWithPatientSelection() {
        currentPatient = null;
        try {
            currentPatient = PatientWrapper.getPatient(
                specimenAdminForm.appService, patientNumberText.getText());
            if (currentPatient != null) {
                specimenAdminForm.appendLog("--------"); //$NON-NLS-1$
                specimenAdminForm.appendLogNLS("linkAssign.activitylog.patient", //$NON-NLS-1$
                    currentPatient.getPnumber());
            }
        } catch (ApplicationException e) {
            BiobankPlugin.openError(
                Messages.getString("ScanLink.dialog.patient.errorMsg"), e); //$NON-NLS-1$
        }
        setCollectionEventList();
    }

    public void reset(boolean resetAll) {
        viewerCollectionEvents.setInput(null);
        currentPatient = null;
        if (resetAll) {
            patientNumberText.setText(""); //$NON-NLS-1$
            if (cEventText != null) {
                cEventText.setText(""); //$NON-NLS-1$
            }
        }
    }

    public PatientWrapper getCurrentPatient() {
        return currentPatient;
    }

    public void setCurrentPatientAndVisit(PatientWrapper patient,
        CollectionEventWrapper cEvent) throws Exception {
        patient.reload();
        this.currentPatient = patient;
        patientNumberText.setText(patient.getPnumber());
        viewerCollectionEvents.setInput(Arrays.asList(cEvent));
        viewerCollectionEvents.setSelection(new StructuredSelection(cEvent));
        if (cEventText != null) {
            cEventText.setText(cEvent.getVisitNumber().toString());
        }
    }

    public void enabledPatientText(boolean enabled) {
        patientNumberText.setEnabled(enabled);
    }

    public void enabledVisitsList(boolean enabled) {
        viewerCollectionEvents.getCombo().setEnabled(enabled);
        showVisitText(!enabled);
    }

    public void setPatientTextCallback(PatientTextCallback callback) {
        this.patientTextCallback = callback;
    }

    protected static interface PatientTextCallback {
        public void focusLost();

        public void textModified();
    }

    public void enableValidators(boolean enabled) {
        if (enabled) {
            patientNumberText.setText(""); //$NON-NLS-1$
            viewerCollectionEvents.getCombo().deselectAll();
        } else {
            patientNumberText.setText("?"); //$NON-NLS-1$
            viewerCollectionEvents.setInput(new String[] { "?" }); //$NON-NLS-1$
            viewerCollectionEvents.getCombo().select(0);
        }
    }

    public void showVisitText(boolean show) {
        widgetCreator.showWidget(cEventComboLabel, !show);
        widgetCreator.showWidget(viewerCollectionEvents.getCombo(), !show);
        if (cEventText != null) {
            widgetCreator.showWidget(cEventTextLabel, show);
            widgetCreator.showWidget(cEventText, show);
        }
    }

    public void setFirstControl() {
        specimenAdminForm.setFirstControl(patientNumberText);
    }

    public boolean fieldsValid() {
        IStructuredSelection selection = (IStructuredSelection) viewerCollectionEvents
            .getSelection();
        return patientValidator.validate(patientNumberText.getText()).equals(
            Status.OK_STATUS)
            && selection.size() > 0;
    }

    public void setCollectionEventList() {
        if (viewerCollectionEvents != null) {
            if (currentPatient != null) {
                List<CollectionEventWrapper> collection = currentPatient
                    .getCollectionEventCollection(true, false);
                viewerCollectionEvents.setInput(collection);
                viewerCollectionEvents.getCombo().setFocus();
                if (collection != null && collection.size() == 1) {
                    viewerCollectionEvents
                        .setSelection(new StructuredSelection(collection.get(0)));
                } else {
                    viewerCollectionEvents.getCombo().deselectAll();
                }
            } else {
                viewerCollectionEvents.setInput(null);
            }
            if (cEventText != null) {
                cEventText.setText(""); //$NON-NLS-1$
            }
        }

    }

    public String getCurrentWorksheetNumber() {
        return currentWorksheetNumber;
    }

    public List<SpecimenWrapper> getSpecimenInCollectionEvent() {
        List<SpecimenWrapper> specs = getSelectedCollectionEvent()
            .getSourceSpecimenCollectionInProcess(true);
        if (specs.size() == 0) {
            BiobankPlugin.openAsyncError(Messages
                .getString("ScanLink.sourceSpecimenInProcess.error.title"),
                Messages
                    .getString("ScanLink.sourceSpecimenInProcess.error.msg"));
        }
        return specs;
    }

    /**
     * get the list of aliquoted specimen type the study wants and that the
     * container authorized
     */
    public List<SpecimenTypeWrapper> getStudyAliquotedTypes(
        List<SpecimenTypeWrapper> authorizedSpecimenTypesInContainer,
        String containerLabel) {
        StudyWrapper study = getCurrentPatient().getStudy();
        try {
            // need to reload study to avoid performance problem when using
            // the same lots of time (like is try different positions for
            // same patient)
            study.reload();
        } catch (Exception e) {
            BiobankPlugin.openAsyncError("Problem reloading study", e);
        }
        List<SpecimenTypeWrapper> studiesAliquotedTypes;
        // done at first successful scan
        studiesAliquotedTypes = new ArrayList<SpecimenTypeWrapper>();
        for (AliquotedSpecimenWrapper ss : getCurrentPatient().getStudy()
            .getAliquotedSpecimenCollection(true)) {
            if (ss.getActivityStatus().isActive()) {
                SpecimenTypeWrapper type = ss.getSpecimenType();
                if (authorizedSpecimenTypesInContainer == null
                    || authorizedSpecimenTypesInContainer.contains(type)) {
                    studiesAliquotedTypes.add(type);
                }
            }
        }
        if (studiesAliquotedTypes.size() == 0) {
            String studyNameShort = "unknown";
            if (getCurrentPatient() != null)
                studyNameShort = getCurrentPatient().getStudy().getNameShort();
            if (containerLabel == null)
                BiobankPlugin.openAsyncError(Messages
                    .getString("ScanLink.aliquotedSpecimenTypes.error.title"), //$NON-NLS-1$
                    Messages.getString(
                        "ScanLink.aliquotedSpecimenTypes.error.msg", //$NON-NLS-1$
                        studyNameShort));
            else
                BiobankPlugin
                    .openAsyncError(
                        Messages
                            .getString("ScanLink.aliquotedSpecimenTypes.error.title"), //$NON-NLS-1$
                        Messages
                            .getString(
                                "ScanLink.aliquotedSpecimenTypes.error.msg.known.container", //$NON-NLS-1$
                                studyNameShort, containerLabel));
        }
        return studiesAliquotedTypes;
    }
}
