package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.LinkFormPatientManagement.PatientTextCallback;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.validators.CabinetInventoryIDValidator;
import edu.ualberta.med.biobank.validators.CabinetLabelValidator;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.grids.AbstractContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayFatory;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class CabinetLinkAssignEntryForm extends AbstractAliquotAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.CabinetLinkAssignEntryForm"; //$NON-NLS-1$

    private static BiobankLogger logger = BiobankLogger
        .getLogger(CabinetLinkAssignEntryForm.class.getName());

    private LinkFormPatientManagement linkFormPatientManagement;

    private Label cabinetLabel;
    private Label drawerLabel;
    private AbstractContainerDisplayWidget cabinetWidget;
    private AbstractContainerDisplayWidget drawerWidget;

    private ComboViewer viewerSampleTypes;
    private Text inventoryIdText;
    private Text positionText;
    private Button checkPositionButton;

    private CancelConfirmWidget cancelConfirmWidget;

    private IObservableValue positionValue = new WritableValue("", String.class); //$NON-NLS-1$
    private IObservableValue resultShownValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue selectedSampleTypeValue = new WritableValue("", //$NON-NLS-1$
        String.class);

    private AliquotWrapper aliquot;
    private ContainerWrapper cabinet;
    private ContainerWrapper drawer;
    private ContainerWrapper bin;

    private String cabinetNameContains = ""; //$NON-NLS-1$

    private Button radioNew;

    private CabinetInventoryIDValidator inventoryIDValidator;

    private List<ContainerTypeWrapper> cabinetContainerTypes;

    protected boolean positionTextModified;

    private CabinetLabelValidator cabinetPositionvalidator;

    protected boolean inventoryIdModified;

    @Override
    protected void init() {
        super.init();
        setPartName(Messages.getString("Cabinet.tabTitle")); //$NON-NLS-1$
        aliquot = new AliquotWrapper(appService);
        IPreferenceStore store = BioBankPlugin.getDefault()
            .getPreferenceStore();
        cabinetNameContains = store
            .getString(PreferenceConstants.CABINET_CONTAINER_NAME_CONTAINS);
        linkFormPatientManagement = new LinkFormPatientManagement(
            widgetCreator, this);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("Cabinet.formTitle")); //$NON-NLS-1$
        GridLayout layout = new GridLayout(2, false);
        form.getBody().setLayout(layout);

        createFieldsSection();
        createLocationSection();

        cancelConfirmWidget = new CancelConfirmWidget(form.getBody(), this,
            true);

        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            resultShownValue, Messages
                .getString("Cabinet.checkButton.validationMsg"));

        radioNew.setSelection(true);
        setMoveMode(false);
    }

    private void createLocationSection() throws ApplicationException {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        client.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        client.setLayoutData(gd);
        toolkit.paintBordersFor(client);

        cabinetLabel = toolkit.createLabel(client, "Cabinet"); //$NON-NLS-1$
        drawerLabel = toolkit.createLabel(client, "Drawer"); //$NON-NLS-1$

        List<ContainerTypeWrapper> types = ContainerTypeWrapper
            .getContainerTypesInSite(appService, SessionManager.getInstance()
                .getCurrentSite(), cabinetNameContains, false);
        ContainerTypeWrapper cabinetType = null;
        ContainerTypeWrapper drawerType = null;
        if (types.size() == 0) {
            BioBankPlugin.openAsyncError(Messages
                .getString("Cabinet.dialog.noType.error.title"), //$NON-NLS-1$
                Messages.getFormattedString("Cabinet.dialog.notType.error.msg", //$NON-NLS-1$
                    cabinetNameContains));
        } else {
            cabinetType = types.get(0);
            List<ContainerTypeWrapper> children = cabinetType
                .getChildContainerTypeCollection();
            if (children.size() > 0) {
                drawerType = children.get(0);
            }
        }
        cabinetWidget = ContainerDisplayFatory
            .createWidget(client, cabinetType);
        toolkit.adapt(cabinetWidget);
        GridData gdDrawer = new GridData();
        gdDrawer.verticalAlignment = SWT.TOP;
        cabinetWidget.setLayoutData(gdDrawer);

        drawerWidget = ContainerDisplayFatory.createWidget(client, drawerType);
        toolkit.adapt(drawerWidget);
        GridData gdBin = new GridData();
        gdBin.verticalSpan = 2;
        drawerWidget.setLayoutData(gdBin);
    }

    private void createFieldsSection() throws ApplicationException {
        Composite fieldsComposite = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        fieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(fieldsComposite);
        GridData gd = new GridData();
        gd.widthHint = 500;
        gd.verticalAlignment = SWT.TOP;
        fieldsComposite.setLayoutData(gd);

        // radio button to choose new or move
        radioNew = toolkit.createButton(fieldsComposite, Messages
            .getString("Cabinet.button.new.text"), //$NON-NLS-1$
            SWT.RADIO);
        radioNew.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioNew.getSelection()) {
                    setMoveMode(false);
                }
            }
        });
        Button radioMove = toolkit.createButton(fieldsComposite, Messages
            .getString("Cabinet.button.move.text"), SWT.RADIO); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalSpan = 2;
        radioMove.setLayoutData(gd);
        radioMove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!radioNew.getSelection()) {
                    setMoveMode(true);
                }
            }
        });

        linkFormPatientManagement.initPatientNumberText(fieldsComposite);
        linkFormPatientManagement
            .setPatientTextCallback(new PatientTextCallback() {
                @Override
                public void focusLost() {
                    setTypeCombosLists();
                }

                @Override
                public void textModified() {
                    viewerSampleTypes.setInput(null);
                }
            });

        linkFormPatientManagement.createVisitCombo(fieldsComposite);

        inventoryIDValidator = new CabinetInventoryIDValidator();
        inventoryIdText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, Messages
                .getString("Cabinet.inventoryId.label"), new String[0], //$NON-NLS-1$
            BeansObservables.observeValue(aliquot, "inventoryId"), //$NON-NLS-1$
            inventoryIDValidator);
        gd = (GridData) inventoryIdText.getLayoutData();
        gd.horizontalSpan = 2;

        inventoryIdText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        inventoryIdText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (inventoryIdModified && !radioNew.getSelection()) {
                    // Move Mode only
                    try {
                        retrieveAliquotDataForMoving();
                    } catch (Exception ex) {
                        BioBankPlugin
                            .openAsyncError("Move - aliquot error", ex); //$NON-NLS-1$
                    }
                }
                inventoryIdModified = false;
            }
        });
        inventoryIdText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                inventoryIdModified = true;
            }
        });

        cabinetPositionvalidator = new CabinetLabelValidator(Messages
            .getString("Cabinet.position.validationMsg"));
        positionText = (Text) createBoundWidgetWithLabel(
            fieldsComposite,
            Text.class,
            SWT.NONE,
            Messages.getString("Cabinet.position.label"), new String[0], positionValue, //$NON-NLS-1$
            cabinetPositionvalidator); //$NON-NLS-1$
        gd = (GridData) positionText.getLayoutData();
        gd.horizontalSpan = 2;
        positionText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (positionTextModified
                    && cabinetPositionvalidator
                        .validate(positionText.getText()) == Status.OK_STATUS) {
                    BusyIndicator.showWhile(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell().getDisplay(),
                        new Runnable() {
                            @Override
                            public void run() {
                                initContainersFromPosition();
                                int typeListSize = setTypeCombosLists();
                                if (typeListSize == 0) {
                                    positionText.setFocus();
                                }
                            }
                        });
                }
                positionTextModified = false;
            }
        });
        positionText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                positionTextModified = true;
                if (radioNew.getSelection()) {
                    viewerSampleTypes.setInput(null);
                }
            }
        });

        createTypeCombo(fieldsComposite);

        checkPositionButton = toolkit.createButton(fieldsComposite, Messages
            .getString("Cabinet.checkButton.text"), //$NON-NLS-1$
            SWT.PUSH);
        gd = new GridData();
        gd.horizontalSpan = 3;
        checkPositionButton.setLayoutData(gd);
        checkPositionButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                checkPositionAndAliquot();
            }
        });
    }

    protected void initContainersFromPosition() {
        try {
            String binLabel = positionText.getText().substring(0, 6);
            List<ContainerWrapper> foundContainers = ContainerWrapper
                .getContainersInSite(appService, SessionManager.getInstance()
                    .getCurrentSite(), binLabel);
            List<ContainerWrapper> cabinetContainers = new ArrayList<ContainerWrapper>();
            for (ContainerWrapper container : foundContainers) {
                ContainerWrapper cont = container;
                while (cont.getParent() != null) {
                    cont = cont.getParent();
                }
                if (cabinetContainerTypes.contains(cont.getContainerType())) {
                    cabinetContainers.add(container);
                }
            }
            if (cabinetContainers.size() == 1) {
                bin = cabinetContainers.get(0);
                drawer = bin.getParent();
                cabinet = drawer.getParent();
            } else if (cabinetContainers.size() == 0) {
                String errorMsg = Messages.getFormattedString(
                    "Cabinet.activitylog.checkParent.error.found", binLabel); //$NON-NLS-1$
                BioBankPlugin.openAsyncError(
                    "Check position and aliquot", errorMsg); //$NON-NLS-1$
                appendLogNLS("Cabinet.activitylog.checkParent.error", errorMsg); //$NON-NLS-1$
                viewerSampleTypes.getCombo().setEnabled(false);
                return;
            } else {
                BioBankPlugin.openAsyncError("Container problem",
                    "More than one container found for " + binLabel //$NON-NLS-1$
                        + " --- should do something"); //$NON-NLS-1$
                viewerSampleTypes.getCombo().setEnabled(false);
                return;
            }
        } catch (Exception ex) {
            BioBankPlugin.openAsyncError("Init container from position", ex);
        }
    }

    protected void setMoveMode(boolean moveMode) {
        try {
            String inventoryId = inventoryIdText.getText();
            String position = positionText.getText();
            reset();
            inventoryIdText.setText(inventoryId);
            positionText.setText(position);
            linkFormPatientManagement.enabledPatientText(!moveMode);
            linkFormPatientManagement.enabledVisitsList(!moveMode);
            viewerSampleTypes.getCombo().setEnabled(!moveMode);
            inventoryIDValidator.setManageOldInventoryIDs(moveMode);
            // Validator has change: we need to re-validate
            inventoryIDValidator.validate(inventoryId);
        } catch (Exception ex) {
            BioBankPlugin.openAsyncError("Error setting move mode " + moveMode, //$NON-NLS-1$
                ex);
        }
    }

    private void createTypeCombo(Composite fieldsComposite)
        throws ApplicationException {
        initCabinetContainerTypesList();
        viewerSampleTypes = createComboViewerWithNoSelectionValidator(
            fieldsComposite,
            Messages.getString("Cabinet.sampleType.label"), null, null, //$NON-NLS-1$
            Messages.getString("Cabinet.sampleType.validationMsg")); //$NON-NLS-1$
        GridData gd = (GridData) viewerSampleTypes.getCombo().getLayoutData();
        gd.horizontalSpan = 2;
        viewerSampleTypes
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection stSelection = (IStructuredSelection) viewerSampleTypes
                        .getSelection();
                    aliquot.setSampleType((SampleTypeWrapper) stSelection
                        .getFirstElement());
                }
            });
    }

    private void initCabinetContainerTypesList() throws ApplicationException {
        cabinetContainerTypes = ContainerTypeWrapper.getContainerTypesInSite(
            appService, SessionManager.getInstance().getCurrentSite(),
            cabinetNameContains, false);
    }

    protected void checkPositionAndAliquot() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    appendLog("----"); //$NON-NLS-1$
                    PatientVisitWrapper pv = linkFormPatientManagement
                        .getSelectedPatientVisit();
                    aliquot.setPatientVisit(pv);
                    if (radioNew.getSelection()) {
                        appendLogNLS("Cabinet.activitylog.checkingId", //$NON-NLS-1$
                            aliquot.getInventoryId());
                        aliquot.checkInventoryIdUnique();
                    }
                    String positionString = positionText.getText();
                    if (bin == null) {
                        resultShownValue.setValue(Boolean.FALSE);
                        hidePositions();
                        return;
                    }
                    appendLogNLS(
                        "Cabinet.activitylog.checkingPosition", positionString); //$NON-NLS-1$
                    aliquot.setAliquotPositionFromString(positionString, bin);
                    if (aliquot.isPositionFree(bin)) {
                        aliquot.setParent(bin);
                        showPositions();
                        resultShownValue.setValue(Boolean.TRUE);
                        cancelConfirmWidget.setFocus();
                    } else {
                        BioBankPlugin.openAsyncError("Position not free",
                            Messages.getFormattedString(
                                "Cabinet.checkStatus.error", positionString, //$NON-NLS-1$
                                bin.getLabel()));
                        appendLogNLS(
                            "Cabinet.activitylog.checkPosition.error", positionString, bin.getLabel()); //$NON-NLS-1$
                        return;
                    }
                    setDirty(true);
                } catch (RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (BiobankCheckException bce) {
                    BioBankPlugin.openAsyncError(
                        "Error while checking position", bce); //$NON-NLS-1$
                    appendLog("ERROR: " + bce.getMessage()); //$NON-NLS-1$
                    resultShownValue.setValue(Boolean.FALSE);
                } catch (Exception e) {
                    BioBankPlugin.openAsyncError(
                        "Error while checking position", e); //$NON-NLS-1$
                }
            }

        });
    }

    /**
     * Get sample types only defined in the patient's study and available in
     * current selected bin. Then set these types to the types combo
     * 
     * @return the size of type combo list
     */
    private int setTypeCombosLists() {
        viewerSampleTypes.getCombo().setEnabled(true);
        List<SampleTypeWrapper> studiesSampleTypes = new ArrayList<SampleTypeWrapper>();
        if (linkFormPatientManagement.getCurrentPatient() != null
            && bin != null) {
            List<SampleTypeWrapper> binTypes = bin.getContainerType()
                .getSampleTypeCollection();
            StudyWrapper study = linkFormPatientManagement.getCurrentPatient()
                .getStudy();
            try {
                // need to reload study to avoid performance problem when using
                // the same lots of time (like is try differents positions for
                // same patient)
                study.reload();
            } catch (Exception e) {
                BioBankPlugin.openAsyncError("Problem reloading study", e);
            }
            for (SampleStorageWrapper ss : study.getSampleStorageCollection()) {
                if (ss.getActivityStatus().isActive()) {
                    SampleTypeWrapper type = ss.getSampleType();
                    if (binTypes.contains(type)) {
                        studiesSampleTypes.add(type);
                    }
                }
            }
            if (studiesSampleTypes.size() == 0) {
                String studyText = "unknown";
                if (linkFormPatientManagement.getCurrentPatient() != null) {
                    studyText = linkFormPatientManagement.getCurrentPatient()
                        .getStudy().getNameShort();
                }
                BioBankPlugin.openError("No Sample Types",
                    "There are no sample types that "
                        + "are defined for current patient study (" + studyText
                        + ") and that are defined as possible for bin "
                        + bin.getLabel());
            }
            if (!radioNew.getSelection()) {
                // Move
                SampleTypeWrapper type = aliquot.getSampleType();
                if (!studiesSampleTypes.contains(type)
                    && binTypes.contains(type)) {
                    // in move mode, the sample source could be deactivate
                    studiesSampleTypes.add(type);
                }
            }
        }
        viewerSampleTypes.setInput(studiesSampleTypes);
        if (radioNew.getSelection()) {
            viewerSampleTypes.getCombo().setEnabled(true);
            if (studiesSampleTypes.size() == 1) {
                viewerSampleTypes.getCombo().select(0);
                aliquot.setSampleType(studiesSampleTypes.get(0));
            } else {
                viewerSampleTypes.getCombo().deselectAll();
                aliquot.setSampleType(null);
            }
        } else {
            viewerSampleTypes.setSelection(new StructuredSelection(aliquot
                .getSampleType()));
            viewerSampleTypes.getCombo().setEnabled(false);
        }
        return studiesSampleTypes.size();
    }

    /**
     * In move mode, get informations from the existing aliquot
     * 
     * @throws Exception
     */
    protected void retrieveAliquotDataForMoving() throws Exception {
        String inventoryId = inventoryIdText.getText();
        if (inventoryId.isEmpty()) {
            return;
        }
        if (inventoryId.length() == 4) {
            // compatibility with old aliquots imported
            // 4 letters aliquots are now C+4letters
            inventoryId = "C" + inventoryId; //$NON-NLS-1$
        }
        resultShownValue.setValue(false);
        reset();
        aliquot.setInventoryId(inventoryId);
        inventoryIdText.setText(inventoryId);

        appendLogNLS("Cabinet.activitylog.gettingInfoId", //$NON-NLS-1$
            aliquot.getInventoryId());
        List<AliquotWrapper> aliquots = AliquotWrapper.getAliquotsInSite(
            appService, aliquot.getInventoryId(), SessionManager.getInstance()
                .getCurrentSite());
        if (aliquots.size() > 1) {
            throw new Exception(
                "Error while retrieving aliquot with inventoryId " //$NON-NLS-1$
                    + aliquot.getInventoryId()
                    + ": more than one aliquot found."); //$NON-NLS-1$
        }
        if (aliquots.size() == 0) {
            throw new Exception("No aliquot found with inventoryId " //$NON-NLS-1$
                + aliquot.getInventoryId());
        }
        aliquot.initObjectWith(aliquots.get(0));
        PatientWrapper patient = aliquot.getPatientVisit().getPatient();
        linkFormPatientManagement.setCurrentPatientAndVisit(patient, aliquot
            .getPatientVisit());
        positionText.setText(aliquot.getPositionString(true, false));
        initParentContainersFromPosition(positionText.getText());
        setTypeCombosLists();
        String posStr = aliquot.getPositionString(true, false);
        if (posStr == null) {
            posStr = "none"; //$NON-NLS-1$
        }
        appendLogNLS(
            "Cabinet.activitylog.aliquotInfo", aliquot.getInventoryId(), //$NON-NLS-1$
            posStr);
    }

    private void showPositions() {
        if (drawer == null || bin == null || cabinet == null) {
            cabinetWidget.setSelection(null);
            cabinetLabel.setText("Cabinet"); //$NON-NLS-1$
            drawerWidget.setSelection(null);
            drawerLabel.setText("Drawer"); //$NON-NLS-1$
        } else {
            cabinetWidget.setContainerType(cabinet.getContainerType());
            cabinetWidget.setSelection(drawer.getPosition());
            cabinetLabel.setText("Cabinet " + cabinet.getLabel()); //$NON-NLS-1$
            drawerWidget.setSelection(bin.getPosition());
            drawerLabel.setText("Drawer " + drawer.getLabel()); //$NON-NLS-1$
        }
        form.layout(true, true);
    }

    private void hidePositions() {
        cabinet = null;
        bin = null;
        drawer = null;
        showPositions();
    }

    protected void initParentContainersFromPosition(String positionString)
        throws Exception {
        bin = null;
        String binLabel = positionString.substring(0, 6);
        appendLogNLS("Cabinet.activitylog.checkingParent", binLabel, //$NON-NLS-1$ 
            aliquot.getSampleType().getName());
        List<ContainerWrapper> containers = ContainerWrapper
            .getContainersHoldingSampleType(appService, SessionManager
                .getInstance().getCurrentSite(), binLabel, aliquot
                .getSampleType());
        if (containers.size() == 1) {
            bin = containers.get(0);
            drawer = bin.getParent();
            cabinet = drawer.getParent();
        } else if (containers.size() == 0) {
            containers = ContainerWrapper.getContainersInSite(appService,
                SessionManager.getInstance().getCurrentSite(), binLabel);
            String errorMsg = null;
            if (containers.size() > 0) {
                errorMsg = Messages.getFormattedString(
                    "Cabinet.activitylog.checkParent.error.type", binLabel, //$NON-NLS-1$
                    aliquot.getSampleType().getName());
            } else {
                errorMsg = Messages.getFormattedString(
                    "Cabinet.activitylog.checkParent.error.found", binLabel); //$NON-NLS-1$
            }
            if (errorMsg != null) {
                BioBankPlugin.openError("Check position and aliquot", errorMsg); //$NON-NLS-1$
                appendLogNLS("Cabinet.activitylog.checkParent.error", errorMsg); //$NON-NLS-1$
            }
            return;
        } else {
            throw new Exception("More than one container found for " + binLabel //$NON-NLS-1$
                + " --- should do something"); //$NON-NLS-1$
        }
    }

    @Override
    public void reset() throws Exception {
        aliquot.resetToNewObject();
        cabinet = null;
        drawer = null;
        bin = null;
        cabinetWidget.setSelection(null);
        drawerWidget.setSelection(null);
        resultShownValue.setValue(Boolean.FALSE);
        selectedSampleTypeValue.setValue(""); //$NON-NLS-1$
        linkFormPatientManagement.reset(true);
        inventoryIdText.setText(""); //$NON-NLS-1$
        positionText.setText(""); //$NON-NLS-1$
        if (viewerSampleTypes.getCombo().getItemCount() > 1) {
            viewerSampleTypes.getCombo().deselectAll();
        }
        setDirty(false);
    }

    @Override
    protected void saveForm() throws Exception {
        if (radioNew.getSelection()) {
            aliquot.setLinkDate(new Date());
            aliquot.setQuantityFromType();
        }
        aliquot.persist();
        String posStr = aliquot.getPositionString(true, false);
        if (posStr == null) {
            posStr = "none"; //$NON-NLS-1$
        }
        String msgString = "";
        if (radioNew.getSelection()) {
            msgString = "Cabinet.activitylog.aliquot.saveNew"; //$NON-NLS-1$
        } else {
            msgString = "Cabinet.activitylog.aliquot.saveMove"; //$NON-NLS-1$
        }
        appendLogNLS(msgString, posStr, aliquot.getInventoryId(), aliquot
            .getSampleType().getName(), linkFormPatientManagement
            .getCurrentPatient().getPnumber(), aliquot.getPatientVisit()
            .getFormattedDateProcessed(), aliquot.getPatientVisit()
            .getShipment().getClinic().getName());
        setFinished(false);
    }

    @Override
    protected String getOkMessage() {
        return "Add cabinet aliquots."; //$NON-NLS-1$
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            cancelConfirmWidget.setConfirmEnabled(true);
            setConfirmEnabled(true);
            checkPositionButton.setEnabled(true);
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            cancelConfirmWidget.setConfirmEnabled(false);
            setConfirmEnabled(false);
            if (status.getMessage() != null
                && status.getMessage().contentEquals(
                    Messages.getString("Cabinet.checkButton.validationMsg"))) {
                checkPositionButton.setEnabled(true);
            } else {
                checkPositionButton.setEnabled(false);
            }
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return ID;
    }

    @Override
    protected String getActivityTitle() {
        return "Cabinet link/assign activity"; //$NON-NLS-1$
    }

    @Override
    public BiobankLogger getErrorLogger() {
        return logger;
    }

    @Override
    public boolean onClose() {
        linkFormPatientManagement.onClose();
        return super.onClose();
    }
}
