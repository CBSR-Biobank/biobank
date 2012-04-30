package edu.ualberta.med.biobank.forms.linkassign;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class LinkFormPatientManagement {

    private boolean patientNumberTextModified = false;
    protected BgcBaseText patientNumberText;
    protected ComboViewer viewerCollectionEvents;

    // currentPatient
    protected PatientWrapper currentPatient;

    private BgcWidgetCreator widgetCreator;

    private AbstractSpecimenAdminForm specimenAdminForm;

    private PatientTextCallback patientTextCallback;
    private Label patientLabel;
    private NonEmptyStringValidator patientValidator;
    // private Label cEventTextLabel;
    // private BiobankText cEventText;
    private Label cEventComboLabel;
    protected CollectionEventWrapper currentCEventSelected;
    protected String currentWorksheetNumber;
    protected boolean worksheetNumberModified;
    private CEventComboCallback cEventComboCallback;
    private Label pEventComboLabel;
    private ComboViewer viewerProcessingEvents;
    protected ProcessingEventWrapper currentPEventSelected;
    private Label pEventTextLabel;
    private BgcBaseText pEventText;
    private Button pEventListCheck;
    private boolean settingCollectionEvent;
    private static Boolean pEventListCheckSelection = true;

    public LinkFormPatientManagement(BgcWidgetCreator widgetCreator,
        AbstractSpecimenAdminForm specimenAdminForm) {
        this.widgetCreator = widgetCreator;
        this.specimenAdminForm = specimenAdminForm;
    }

    protected void createPatientNumberText(Composite parent) {
        patientLabel = widgetCreator.createLabel(parent,
            "Patient number");
        patientLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        patientValidator = new NonEmptyStringValidator(
            "Enter a patient number");
        patientNumberText = (BgcBaseText) widgetCreator.createBoundWidget(
            parent, BgcBaseText.class, SWT.NONE, patientLabel, new String[0],
            new WritableValue("", String.class), patientValidator);
        GridData gd = (GridData) patientNumberText.getLayoutData();
        gd.horizontalSpan = 2;
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
                viewerProcessingEvents.getCombo().setFocus();
            }
        });
        patientNumberText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                patientNumberTextModified = true;
                if (viewerCollectionEvents != null)
                    viewerCollectionEvents.setInput(null);
                if (patientTextCallback != null) {
                    patientTextCallback.textModified();
                }
            }
        });
        patientNumberText
            .addKeyListener(specimenAdminForm.textFieldKeyListener);
        setFirstControl();
    }

    protected void createEventsWidgets(Composite compositeFields) {
        createProcessingEventWidgets(compositeFields);
        createCollectionEventWidgets(compositeFields);
    }

    private void createProcessingEventWidgets(Composite compositeFields) {
        pEventComboLabel = widgetCreator.createLabel(compositeFields,
            "Processing event");
        viewerProcessingEvents = widgetCreator.createComboViewer(
            compositeFields, pEventComboLabel, null, null,
            "A processing event should be selected", false,
            null, new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    currentPEventSelected =
                        (ProcessingEventWrapper) selectedObject;
                    setCollectionEventListFromPEvent();
                }
            }, new BiobankLabelProvider());
        viewerProcessingEvents.setComparator(new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof ProcessingEventWrapper
                    && e2 instanceof ProcessingEventWrapper)
                    // want the date decreasing
                    return ((ProcessingEventWrapper) e2)
                        .compareTo((ProcessingEventWrapper) e1);
                return 0;
            }
        });
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        viewerProcessingEvents.getCombo().setLayoutData(gridData);

        viewerProcessingEvents.getCombo().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                IStructuredSelection selection = (IStructuredSelection)
                    viewerProcessingEvents.getSelection();
                if (selection != null && selection.size() > 0) {
                    ProcessingEventWrapper pe =
                        (ProcessingEventWrapper) selection
                            .getFirstElement();
                    if (pe != null) {
                        specimenAdminForm.appendLog(NLS
                            .bind(
                                "Processing event {0} / {1} selected",
                                pe.getWorksheet(), pe.getFormattedCreatedAt()));
                    }
                }
            }
        });
        pEventListCheck = specimenAdminForm.getToolkit().createButton(
            compositeFields,
            "Last 7 days", SWT.CHECK);
        pEventListCheck.setSelection(pEventListCheckSelection);
        pEventListCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setProcessingEventListFromPatient();
            }
        });

        // Will replace the combo in some specific situations (like cabinet
        // form):
        pEventTextLabel = widgetCreator.createLabel(compositeFields,
            "Processing event");
        pEventTextLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        pEventText = (BgcBaseText) widgetCreator.createWidget(compositeFields,
            BgcBaseText.class, SWT.NONE, "");
        pEventText.setEnabled(false);
        GridData gd = (GridData) pEventText.getLayoutData();
        gd.horizontalSpan = 2;
        widgetCreator.hideWidget(pEventTextLabel);
        widgetCreator.hideWidget(pEventText);
    }

    private void createCollectionEventWidgets(Composite compositeFields) {
        cEventComboLabel = widgetCreator.createLabel(compositeFields,
            "Collection visit#");
        viewerCollectionEvents = widgetCreator.createComboViewer(
            compositeFields, cEventComboLabel, null, null,
            "A collection event should be selected", false,
            null, new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    currentCEventSelected =
                        (CollectionEventWrapper) selectedObject;
                    if (cEventComboCallback != null && !settingCollectionEvent)
                        cEventComboCallback.selectionChanged();
                }
            }, new BiobankLabelProvider());
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.horizontalSpan = 2;
        viewerCollectionEvents.getCombo().setLayoutData(gridData);

        viewerCollectionEvents.getCombo().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                IStructuredSelection selection =
                    (IStructuredSelection) viewerCollectionEvents
                        .getSelection();
                if (selection != null && selection.size() > 0) {
                    CollectionEventWrapper ce =
                        (CollectionEventWrapper) selection
                            .getFirstElement();
                    if (ce != null) {
                        specimenAdminForm.appendLog(NLS
                            .bind(
                                "Visit number {0} selected",
                                ce.getVisitNumber()));
                    }
                }
            }
        });
    }

    protected CollectionEventWrapper getSelectedCollectionEvent() {
        return currentCEventSelected;
    }

    protected void initFieldWithPatientSelection() {
        currentPatient = null;
        try {
            currentPatient = PatientWrapper.getPatient(
                SessionManager.getAppService(), patientNumberText.getText());
            if (currentPatient != null) {
                if (!SessionManager.getUser().getCurrentWorkingCenter()
                    .getStudyCollection().contains(currentPatient.getStudy())) {
                    BgcPlugin
                        .openError(
                            "Patient search error",
                            MessageFormat
                                .format(
                                    "Patient {0} has been found but it is linked to the study {1}. The center {2} is not working with this study.",
                                    currentPatient.getPnumber(),
                                    currentPatient.getStudy().getNameShort(),
                                    SessionManager.getUser()
                                        .getCurrentWorkingCenter()
                                        .getNameShort()));
                    currentPatient = null;
                } else {
                    specimenAdminForm.appendLog("--------");
                    specimenAdminForm.appendLog(NLS.bind(
                        "Found patient with number {0}",
                        currentPatient.getPnumber()));
                }
            }
        } catch (ApplicationException e) {
            BgcPlugin.openError(
                "Patient search error",
                e);
        }
        setProcessingEventListFromPatient();
    }

    public void reset(boolean resetAll) {
        Assert.isNotNull(viewerProcessingEvents);
        Assert.isNotNull(patientNumberText);

        viewerProcessingEvents.setInput(null);
        viewerCollectionEvents.setInput(null);

        currentPatient = null;
        if (resetAll)
            patientNumberText.setText("");
    }

    public PatientWrapper getCurrentPatient() {
        return currentPatient;
    }

    public void setCurrentPatientPEventCEvent(PatientWrapper patient,
        ProcessingEventWrapper pEvent, CollectionEventWrapper cEvent)
        throws Exception {
        patient.reload();
        this.currentPatient = patient;
        patientNumberText.setText(patient.getPnumber());
        viewerProcessingEvents.setInput(Arrays.asList(pEvent));
        viewerProcessingEvents.setSelection(new StructuredSelection(pEvent));
        if (pEventText != null) {
            pEventText.setText(pEvent.getFormattedCreatedAt() + " - "
                + pEvent.getWorksheet());
        }
        viewerCollectionEvents.setInput(Arrays.asList(cEvent));
        viewerCollectionEvents.setSelection(new StructuredSelection(cEvent));
    }

    public void enabledPatientText(boolean enabled) {
        patientNumberText.setEnabled(enabled);
    }

    protected static interface PatientTextCallback {
        public void focusLost();

        public void textModified();
    }

    public void setPatientTextCallback(PatientTextCallback callback) {
        this.patientTextCallback = callback;
    }

    protected static interface CEventComboCallback {
        public void selectionChanged();
    }

    public void setCEventComboCallback(CEventComboCallback callback) {
        this.cEventComboCallback = callback;
    }

    public void enableValidators(boolean enabled) {
        if (enabled) {
            patientNumberText.setText("");
            viewerProcessingEvents.getCombo().deselectAll();
            viewerCollectionEvents.getCombo().deselectAll();
        } else {
            patientNumberText.setText("?");
            viewerProcessingEvents.setInput(new String[] { "?" });
            viewerProcessingEvents.getCombo().select(0);
            viewerCollectionEvents.setInput(new String[] { "?" });
            viewerCollectionEvents.getCombo().select(0);
        }
    }

    public void setFirstControl() {
        specimenAdminForm.setFirstControl(patientNumberText);
    }

    public boolean fieldsValid() {
        IStructuredSelection pEventSelection =
            (IStructuredSelection) viewerProcessingEvents.getSelection();
        IStructuredSelection cEventSelection =
            (IStructuredSelection) viewerCollectionEvents.getSelection();
        if (patientNumberText.isDisposed())
            return false;
        return patientValidator.validate(patientNumberText.getText()).equals(
            Status.OK_STATUS)
            && pEventSelection.size() > 0 && cEventSelection.size() > 0;
    }

    public void setProcessingEventListFromPatient() {
        currentPEventSelected = null;
        if (viewerProcessingEvents != null) {
            if (currentPatient != null) {
                List<ProcessingEventWrapper> collection = null;
                if (pEventListCheck.getSelection())
                    try {
                        collection = currentPatient
                            .getLast7DaysProcessingEvents(SessionManager
                                .getUser().getCurrentWorkingCenter());
                    } catch (ApplicationException e) {
                        BgcPlugin
                            .openAsyncError(
                                "Problem retrieving processing events",
                                e);
                    }
                else
                    collection =
                        currentPatient
                            .getProcessingEventCollection(SessionManager
                                .getUser().getCurrentWorkingCenter()
                                , true);
                viewerProcessingEvents.setInput(collection);
                viewerProcessingEvents.getCombo().setFocus();
                if (collection != null && collection.size() == 1) {
                    viewerProcessingEvents.setSelection(
                        new StructuredSelection(collection.get(0)));
                    currentPEventSelected = collection.get(0);
                } else {
                    viewerProcessingEvents.getCombo().deselectAll();
                }
            } else {
                viewerProcessingEvents.setInput(null);
            }
            if (pEventText != null) {
                pEventText.setText("");
            }
        }
        viewerProcessingEvents.getCombo().setFocus();
    }

    public void setCollectionEventListFromPEvent() {
        currentCEventSelected = null;
        if (viewerCollectionEvents != null) {
            settingCollectionEvent = true;
            if (currentPEventSelected != null) {
                List<CollectionEventWrapper> collection = null;
                try {
                    collection =
                        currentPEventSelected
                            .getCollectionEventFromSpecimensAndPatient(currentPatient);
                } catch (ApplicationException e) {
                    BgcPlugin
                        .openAsyncError(
                            "Problem retrieving collection events",
                            e);
                }
                viewerCollectionEvents.setInput(collection);
                if (collection != null && collection.size() == 1) {
                    viewerCollectionEvents
                        .setSelection(new StructuredSelection(collection.get(0)));
                    currentCEventSelected = collection.get(0);
                    cEventComboCallback.selectionChanged();
                } else {
                    viewerCollectionEvents.getCombo().deselectAll();
                }
            } else {
                viewerCollectionEvents.setInput(null);
            }
            settingCollectionEvent = false;
        }
    }

    public String getCurrentWorksheetNumber() {
        return currentWorksheetNumber;
    }

    protected List<SpecimenWrapper> getParentSpecimenForPEventAndCEvent() {
        if (currentCEventSelected == null || currentPEventSelected == null)
            return Collections.emptyList();
        List<SpecimenWrapper> specs;
        try {
            specs = currentCEventSelected
                .getSourceSpecimenCollectionInProcessNotFlagged(
                    currentPEventSelected, true);
            if (specs.size() == 0) {
                BgcPlugin
                    .openAsyncError(
                        "Source specimens error",
                        "No source specimen of this collection event has been declared in a processing event.");
            }
        } catch (ApplicationException e) {
            specs = new ArrayList<SpecimenWrapper>();
            BgcPlugin.openAsyncError(
                "Problem retrieveing source specimens",
                e);
        }
        return specs;
    }

    /**
     * get the list of aliquoted specimen type the study wants and that the
     * container authorized
     */
    public List<SpecimenTypeWrapper> getStudyAliquotedTypes(
        List<SpecimenTypeWrapper> authorizedSpecimenTypesInContainer) {
        if (currentPatient == null)
            return Collections.emptyList();
        StudyWrapper study = currentPatient.getStudy();
        try {
            // need to reload study to avoid performance problem when using
            // the same lots of time (for instance if try different positions
            // for same patient)
            study.reload();
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                "Problem reloading study", e);
        }

        List<SpecimenTypeWrapper> studiesAliquotedTypes;
        try {
            studiesAliquotedTypes =
                study
                    .getAuthorizedActiveAliquotedTypes(authorizedSpecimenTypesInContainer);
            if (studiesAliquotedTypes.size() == 0) {
                String studyNameShort =
                    "unknown";
                if (getCurrentPatient() != null)
                    studyNameShort = study.getNameShort();
                BgcPlugin
                    .openAsyncError(
                        "No specimen types",
                        NLS.bind(
                            "There are no specimen types that are defined in study {0} and that are authorized inside available containers.",
                            studyNameShort));
            }
        } catch (ApplicationException e) {
            studiesAliquotedTypes = new ArrayList<SpecimenTypeWrapper>();
            BgcPlugin.openAsyncError(
                "Error retrieving available types", e);
        }
        return studiesAliquotedTypes;
    }

    public void onClose() {
        if (specimenAdminForm.finished) {
            pEventListCheckSelection = true;
        } else {
            pEventListCheckSelection = pEventListCheck.getSelection();
        }
    }
}
