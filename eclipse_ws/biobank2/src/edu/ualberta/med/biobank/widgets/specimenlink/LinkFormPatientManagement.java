package edu.ualberta.med.biobank.widgets.specimenlink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
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
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetSourceSpecimensAction;
import edu.ualberta.med.biobank.common.action.scanprocess.GetProcessingEventsAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetAliquotedSpecimensAction;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * A widget that displays a text field to enter a patient number, and pull down combo boxes to
 * select a processing event and a collection event visit number.
 * 
 * Once the user enters the patient number, the server is queried for possible processing events and
 * collection events. Once the server responds, the combo boxes are populated with the possible
 * choices.
 * 
 */
public class LinkFormPatientManagement {

    private static final I18n i18n = I18nFactory.getI18n(LinkFormPatientManagement.class);

    private boolean patientNumberTextModified = false;
    protected BgcBaseText patientNumberText;
    protected ComboViewer viewerCollectionEvents;

    // currentPatient
    protected PatientWrapper currentPatient;

    private final BgcWidgetCreator widgetCreator;

    private final ILinkFormPatientManagementParent widgetParent;

    private Label patientLabel;
    private NonEmptyStringValidator patientValidator;
    // private Label cEventTextLabel;
    // private BiobankText cEventText;
    private Label cEventComboLabel;
    protected CollectionEventWrapper currentCEventSelected;
    protected String currentWorksheetNumber;
    protected boolean worksheetNumberModified;
    private Label pEventComboLabel;
    private ComboViewer viewerProcessingEvents;
    protected ProcessingEventWrapper currentPEventSelected;
    private Label pEventTextLabel;
    private BgcBaseText pEventText;
    private Button pEventListCheck;
    private boolean settingCollectionEvent;
    private static Boolean pEventListCheckSelection = true;
    private final Logger activityLogger;

    public LinkFormPatientManagement(BgcWidgetCreator widgetCreator,
        ILinkFormPatientManagementParent widgetParent, Logger activityLogger) {
        this.widgetCreator = widgetCreator;
        this.widgetParent = widgetParent;
        this.activityLogger = activityLogger;
    }

    public void addPatientNumberKeyListener(KeyListener listener) {
        patientNumberText.addKeyListener(listener);
    }

    public void removePatientNumberKeyListener(KeyListener listener) {
        patientNumberText.removeKeyListener(listener);
    }

    @SuppressWarnings("nls")
    public void createPatientNumberText(Composite parent) {
        patientLabel = widgetCreator.createLabel(parent,
            Patient.PropertyName.PNUMBER.toString());
        patientLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        patientValidator = new NonEmptyStringValidator(
            // TR: validation error message
            i18n.tr("Enter a patient number"));
        patientNumberText =
            (BgcBaseText) widgetCreator.createBoundWidget(
                parent, BgcBaseText.class, SWT.NONE, patientLabel,
                new String[0],
                new WritableValue(StringUtil.EMPTY_STRING, String.class),
                patientValidator);
        GridData gd = (GridData) patientNumberText.getLayoutData();
        gd.horizontalSpan = 2;
        patientNumberText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (patientNumberTextModified) {
                    initFieldWithPatientSelection();
                    widgetParent.focusLost();
                }
                patientNumberTextModified = false;
                viewerProcessingEvents.getCombo().setFocus();
            }
        });
        patientNumberText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                patientNumberTextModified = true;
                if (viewerCollectionEvents != null) {
                    viewerCollectionEvents.setInput(null);
                }
                widgetParent.textModified();
            }
        });
    }

    public void createEventsWidgets(Composite compositeFields) {
        createProcessingEventWidgets(compositeFields);
        createCollectionEventWidgets(compositeFields);
    }

    @SuppressWarnings("nls")
    private void createProcessingEventWidgets(Composite compositeFields) {
        pEventComboLabel = widgetCreator.createLabel(compositeFields,
            ProcessingEvent.NAME.singular().toString());
        viewerProcessingEvents = widgetCreator.createComboViewer(
            compositeFields, pEventComboLabel, null, null,
            // TR: validation error message
            i18n.tr("A processing event should be selected"), false,
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
                        (ProcessingEventWrapper) selection.getFirstElement();
                    if (pe != null) {
                        activityLogger.trace(NLS.bind(
                            "Processing event {0} / {1} selected",
                            pe.getWorksheet(), pe.getFormattedCreatedAt()));
                    }
                }
            }
        });
        pEventListCheck = widgetParent.createButton(compositeFields,
            // TR: checkbox text
            i18n.tr("Last 7 days"), SWT.CHECK);
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
            ProcessingEvent.NAME.singular().toString());
        pEventTextLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        pEventText = (BgcBaseText) widgetCreator.createWidget(compositeFields,
            BgcBaseText.class, SWT.NONE, StringUtil.EMPTY_STRING);
        pEventText.setEnabled(false);
        GridData gd = (GridData) pEventText.getLayoutData();
        gd.horizontalSpan = 2;
        widgetCreator.hideWidget(pEventTextLabel);
        widgetCreator.hideWidget(pEventText);
    }

    @SuppressWarnings("nls")
    private void createCollectionEventWidgets(Composite compositeFields) {
        cEventComboLabel = widgetCreator.createLabel(compositeFields,
            // TR: label
            i18n.tr("Collection visit#"));
        viewerCollectionEvents = widgetCreator.createComboViewer(
            compositeFields, cEventComboLabel, null, null,
            // TR: validation error message
            i18n.tr("A collection event should be selected"),
            false,
            null, new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    currentCEventSelected = (CollectionEventWrapper) selectedObject;
                    if (!settingCollectionEvent) {
                        widgetParent.collectionEventSelectionChanged();
                    }
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
                    (IStructuredSelection) viewerCollectionEvents.getSelection();
                if (selection != null && selection.size() > 0) {
                    CollectionEventWrapper ce =
                        (CollectionEventWrapper) selection.getFirstElement();
                    if (ce != null) {
                        activityLogger.trace(NLS.bind(
                            "Visit number {0} selected", ce.getVisitNumber()));
                    }
                }
            }
        });
    }

    public CollectionEventWrapper getSelectedCollectionEvent() {
        return currentCEventSelected;
    }

    @SuppressWarnings("nls")
    protected void initFieldWithPatientSelection() {
        currentPatient = null;
        try {
            currentPatient = PatientWrapper.getPatient(
                SessionManager.getAppService(), patientNumberText.getText());
            if (currentPatient != null) {
                if (!SessionManager.getUser().getCurrentWorkingCenter().getStudyCollection().contains(
                    currentPatient.getStudy())) {
                    BgcPlugin.openError(
                        // TR: dialog title
                        i18n.tr("Patient search error"),
                        // TR: dialog message
                        i18n.tr(
                            "Patient {0} has been found but it is linked to the study {1}. The center {2} is not working with this study.",
                            currentPatient.getPnumber(),
                            currentPatient.getStudy().getNameShort(),
                            SessionManager.getUser().getCurrentWorkingCenter().getNameShort()));
                    currentPatient = null;
                } else {
                    activityLogger.trace("--------");
                    activityLogger.trace(NLS.bind(
                        "Found patient with number {0}",
                        currentPatient.getPnumber()));
                }
            }
        } catch (ApplicationException e) {
            BgcPlugin.openError(
                // TR: dialog title
                i18n.tr("Patient search error"),
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
        if (resetAll) {
            patientNumberText.setText(StringUtil.EMPTY_STRING);
            currentCEventSelected = null;
        }
    }

    public PatientWrapper getCurrentPatient() {
        return currentPatient;
    }

    @SuppressWarnings("nls")
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

    @SuppressWarnings("nls")
    public void enableValidators(boolean enabled) {
        if (enabled) {
            patientNumberText.setText(StringUtil.EMPTY_STRING);
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

    public Composite getFirstControl() {
        return patientNumberText;
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

    @SuppressWarnings("nls")
    public void setProcessingEventListFromPatient() {
        currentPEventSelected = null;
        if (viewerProcessingEvents != null) {
            if (currentPatient != null) {
                List<ProcessingEventWrapper> collection = null;
                try {
                    BiobankApplicationService appService = SessionManager.getAppService();
                    List<ProcessingEvent> pevents = appService.doAction(
                        new GetProcessingEventsAction(
                            currentPatient.getPnumber(),
                            SessionManager.getUser().getCurrentWorkingCenter().getId(),
                            pEventListCheck.getSelection())).getList();

                    collection = ModelWrapper.wrapModelCollection(
                        appService, pevents, ProcessingEventWrapper.class);
                } catch (ApplicationException e) {
                    BgcPlugin.openAsyncError(
                        // TR: dialog title
                        i18n.tr("Problem retrieving processing events"),
                        e);
                }
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
                pEventText.setText(StringUtil.EMPTY_STRING);
            }
        }
        viewerProcessingEvents.getCombo().setFocus();
    }

    @SuppressWarnings("nls")
    public void setCollectionEventListFromPEvent() {
        currentCEventSelected = null;
        if (viewerCollectionEvents != null) {
            settingCollectionEvent = true;
            if (currentPEventSelected != null) {
                List<CollectionEventWrapper> collection = null;
                try {
                    collection = currentPEventSelected.getCollectionEventFromSpecimensAndPatient(
                        currentPatient);
                } catch (ApplicationException e) {
                    BgcPlugin.openAsyncError(
                        // TR: dialog title
                        i18n.tr("Problem retrieving collection events"),
                        e);
                }
                viewerCollectionEvents.setInput(collection);
                if (collection != null && collection.size() == 1) {
                    viewerCollectionEvents
                        .setSelection(new StructuredSelection(collection.get(0)));
                    currentCEventSelected = collection.get(0);
                    widgetParent.collectionEventSelectionChanged();
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

    @SuppressWarnings("nls")
    public List<Specimen> getParentSpecimenForPEventAndCEvent() {
        if (currentCEventSelected == null || currentPEventSelected == null)
            return Collections.emptyList();
        List<Specimen> specs;
        try {
            specs = SessionManager.getAppService().doAction(
                new CollectionEventGetSourceSpecimensAction(
                    currentCEventSelected.getWrappedObject(),
                    currentPEventSelected.getWrappedObject(),
                    true)).getList();
            if (specs.size() == 0) {
                BgcPlugin
                    .openAsyncError(
                        // TR: dialog title
                        i18n.tr("Source specimens error"),
                        // TR: dialog message
                        i18n.tr("No source specimen of this collection event has been declared in a processing event."));
            }
        } catch (ApplicationException e) {
            specs = new ArrayList<Specimen>();
            BgcPlugin.openAsyncError(
                // TR: dialog title
                i18n.tr("Problem retrieveing source specimens"),
                e);
        }
        return specs;
    }

    /**
     * get the list of aliquoted specimen type the study wants and that the container authorized
     */
    @SuppressWarnings("nls")
    public List<AliquotedSpecimen> getStudyAliquotedTypes(
        List<SpecimenType> authorizedSpecimenTypesInContainer) {
        if (currentPatient == null) return Collections.emptyList();

        StudyWrapper study = currentPatient.getStudy();
        try {
            // need to reload study to avoid performance problem when using
            // the same lots of time (for instance if try different positions
            // for same patient)
            study.reload();
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                // TR: dialog title
                i18n.tr("Problem reloading study"), e);
        }

        List<AliquotedSpecimen> studiesAliquotedTypes;
        try {
            studiesAliquotedTypes = getAuthorizedActiveAliquotedTypes(
                study, authorizedSpecimenTypesInContainer);

            if (studiesAliquotedTypes.size() == 0) {
                // TR: study name short
                String studyNameShort = i18n.tr("unknown");
                if (getCurrentPatient() != null)
                    studyNameShort = study.getNameShort();
                BgcPlugin.openAsyncError(
                    // TR: dialog title
                    i18n.tr("No specimen types"),
                    // TR: dialog message
                    i18n.tr(
                        "There are no specimen types that are defined in study {0} and that are authorized inside available containers.",
                        studyNameShort));
            }
        } catch (ApplicationException e) {
            studiesAliquotedTypes = new ArrayList<AliquotedSpecimen>();
            BgcPlugin.openAsyncError(
                // TR: dialog title
                i18n.tr("Error retrieving available types"), e);
        }
        return studiesAliquotedTypes;
    }

    public void onClose() {
        if (widgetParent.isFinished()) {
            pEventListCheckSelection = true;
        } else {
            pEventListCheckSelection = pEventListCheck.getSelection();
        }
    }

    @SuppressWarnings("nls")
    public List<AliquotedSpecimen> getAuthorizedActiveAliquotedTypes(StudyWrapper study,
        List<SpecimenType> authorizedTypes) throws ApplicationException {

        if (authorizedTypes == null) {
            throw new NullPointerException("authorizedTypes is null");
        }

        Set<AliquotedSpecimen> aliquotedSpecTypes = SessionManager.getAppService().doAction(
            new StudyGetAliquotedSpecimensAction(study.getId(), ActivityStatus.ACTIVE)).getSet();

        List<AliquotedSpecimen> result = new ArrayList<AliquotedSpecimen>();
        for (AliquotedSpecimen aqSpc : aliquotedSpecTypes) {
            SpecimenType spcType = aqSpc.getSpecimenType();
            if (authorizedTypes.isEmpty() || authorizedTypes.contains(spcType)) {
                result.add(aqSpc);
            }
        }
        return result;
    }
}
