package edu.ualberta.med.biobank.forms;

import java.util.Date;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.validators.CabinetLabelValidator;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.grids.AbstractContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayFatory;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class CabinetLinkAssignEntryForm extends AbstractPatientAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.CabinetLinkAssignEntryForm";

    private PatientWrapper currentPatient;

    private Label cabinetLabel;
    private Label drawerLabel;
    private AbstractContainerDisplayWidget cabinetWidget;
    private AbstractContainerDisplayWidget drawerWidget;

    private Text patientNumberText;
    private ComboViewer viewerVisits;
    private ComboViewer viewerSampleTypes;
    private Text inventoryIdText;
    private Text positionText;
    private Button checkPositionButton;

    private CancelConfirmWidget cancelConfirmWidget;

    private IObservableValue patientNumberValue = new WritableValue("",
        String.class);
    private IObservableValue visitSelectionValue = new WritableValue("",
        String.class);
    private IObservableValue positionValue = new WritableValue("", String.class);
    private IObservableValue resultShownValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue selectedSampleTypeValue = new WritableValue("",
        String.class);

    private SampleWrapper sampleWrapper;
    private ContainerWrapper cabinet;
    private ContainerWrapper drawer;
    private ContainerWrapper bin;

    private String cabinetNameContains = "";

    private Button radioNew;

    private static final String CHECK_CLICK_MESSAGE = "Click on check";

    @Override
    protected void init() {
        super.init();
        setPartName("Cabinet Link/Assign");
        sampleWrapper = new SampleWrapper(appService);
        IPreferenceStore store = BioBankPlugin.getDefault()
            .getPreferenceStore();
        cabinetNameContains = store
            .getString(PreferenceConstants.CABINET_CONTAINER_NAME_CONTAINS);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Link and Assign Cabinet Samples");
        GridLayout layout = new GridLayout(2, false);
        form.getBody().setLayout(layout);

        createFieldsSection();
        createLocationSection();

        cancelConfirmWidget = new CancelConfirmWidget(form.getBody(), this,
            true);

        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            resultShownValue, CHECK_CLICK_MESSAGE);

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

        cabinetLabel = toolkit.createLabel(client, "Cabinet");
        drawerLabel = toolkit.createLabel(client, "Drawer");

        List<ContainerTypeWrapper> types = ContainerTypeWrapper
            .getContainerTypesInSite(appService, SessionManager.getInstance()
                .getCurrentSiteWrapper(), cabinetNameContains, false);
        ContainerTypeWrapper cabinetType = null;
        ContainerTypeWrapper drawerType = null;
        if (types.size() == 0) {
            BioBankPlugin.openAsyncError("No container type",
                "No container type found with name containing '"
                    + cabinetNameContains + "'...");
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
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        fieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(fieldsComposite);
        GridData gd = new GridData();
        gd.widthHint = 400;
        gd.verticalAlignment = SWT.TOP;
        fieldsComposite.setLayoutData(gd);

        // radio button to choose new or move
        radioNew = toolkit.createButton(fieldsComposite, "New sample",
            SWT.RADIO);
        final Button radioMove = toolkit.createButton(fieldsComposite,
            "Move sample", SWT.RADIO);
        radioNew.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioNew.getSelection()) {
                    setMoveMode(false);
                }
            }
        });
        radioMove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioMove.getSelection()) {
                    setMoveMode(true);
                }
            }
        });

        patientNumberText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Patient Number", new String[0],
            patientNumberValue, new NonEmptyStringValidator(
                "Enter a patient number"));
        patientNumberText.addListener(SWT.DefaultSelection, new Listener() {
            public void handleEvent(Event e) {
                setVisitsList();
            }
        });
        patientNumberText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        patientNumberText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setVisitsList();
            }
        });
        firstControl = patientNumberText;

        createVisitCombo(fieldsComposite);

        inventoryIdText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Inventory ID", new String[0],
            BeansObservables.observeValue(sampleWrapper, "inventoryId"),
            new NonEmptyStringValidator("Enter Inventory Id"));
        inventoryIdText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        inventoryIdText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!radioNew.getSelection()) {
                    // Move Mode
                    try {
                        retrieveSampleInformations();
                    } catch (Exception ex) {
                        BioBankPlugin.openAsyncError("Move - sample error", ex);
                    }
                }
            }
        });

        positionText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Position", new String[0], positionValue,
            new CabinetLabelValidator("Enter a position (eg 01AA01AB)"));

        createTypeCombo(fieldsComposite);

        checkPositionButton = toolkit.createButton(fieldsComposite, "Check",
            SWT.PUSH);
        gd = new GridData();
        gd.horizontalSpan = 2;
        checkPositionButton.setLayoutData(gd);
        checkPositionButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                checkPositionAndSample();
            }
        });
    }

    protected void setMoveMode(boolean moveMode) {
        try {
            String inventoryId = inventoryIdText.getText();
            String position = positionText.getText();
            reset();
            inventoryIdText.setText(inventoryId);
            positionText.setText(position);
            patientNumberText.setEnabled(!moveMode);
            viewerVisits.getCombo().setEnabled(!moveMode);
            viewerSampleTypes.getCombo().setEnabled(!moveMode);
        } catch (Exception ex) {
            BioBankPlugin.openAsyncError("Error settind move mode " + moveMode,
                ex);
        }
    }

    private void createTypeCombo(Composite fieldsComposite)
        throws ApplicationException {
        List<SampleTypeWrapper> sampleTypes;
        sampleTypes = SampleTypeWrapper.getSampleTypeForContainerTypes(
            appService, SessionManager.getInstance().getCurrentSiteWrapper(),
            cabinetNameContains);
        if (sampleTypes.size() == 0) {
            BioBankPlugin.openAsyncError("Sample types",
                "No sample type found for container with type containing '"
                    + cabinetNameContains + "' in the same.");
        }
        viewerSampleTypes = createComboViewerWithNoSelectionValidator(
            fieldsComposite, "Sample type", sampleTypes, null,
            "A sample type should be selected");
        viewerSampleTypes
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection stSelection = (IStructuredSelection) viewerSampleTypes
                        .getSelection();
                    sampleWrapper.setSampleType((SampleTypeWrapper) stSelection
                        .getFirstElement());
                }
            });
        if (sampleTypes.size() == 1) {
            viewerSampleTypes.getCombo().select(0);
            sampleWrapper.setSampleType(sampleTypes.get(0).getWrappedObject());
        }
    }

    private void createVisitCombo(Composite client) {
        Combo comboVisits = (Combo) createBoundWidgetWithLabel(client,
            Combo.class, SWT.NONE, "Visits", new String[0],
            visitSelectionValue, new NonEmptyStringValidator(
                "A visit should be selected"));
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        comboVisits.setLayoutData(gridData);

        viewerVisits = new ComboViewer(comboVisits);
        viewerVisits.setContentProvider(new ArrayContentProvider());
        viewerVisits.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                PatientVisitWrapper pv = (PatientVisitWrapper) element;
                return pv.getFormattedDateProcessed();
            }
        });
        comboVisits.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == 13) {
                    inventoryIdText.setFocus();
                }
            }
        });
        comboVisits.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PatientVisitWrapper pv = getSelectedPatientVisit();
                sampleWrapper.setPatientVisit(pv);
                appendLog("Visit selected " + pv.getFormattedDateProcessed()
                    + " - " + pv.getShipment().getClinic().getName());
            }
        });
    }

    protected void setVisitsList() {
        try {
            String pNumber = patientNumberText.getText();
            currentPatient = PatientWrapper.getPatientInSite(appService,
                pNumber, SessionManager.getInstance().getCurrentSiteWrapper());

            if (currentPatient == null)
                return;

            appendLog("-----");
            appendLog("Found patient with number " + currentPatient.getNumber());
            // show visits list
            List<PatientVisitWrapper> collection = currentPatient
                .getPatientVisitCollection();
            viewerVisits.setInput(collection);
            viewerVisits.getCombo().select(0);
            viewerVisits.getCombo().setListVisible(true);
        } catch (ApplicationException e) {
            BioBankPlugin.openError("Error getting the patient", e);
        }
    }

    protected void checkPositionAndSample() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    appendLog("----");
                    if (radioNew.getSelection()) {
                        appendLog("Checking inventoryID "
                            + sampleWrapper.getInventoryId());
                        sampleWrapper.checkInventoryIdUnique();
                    }
                    String positionString = positionText.getText();
                    initParentContainersFromPosition(positionString);
                    if (bin == null) {
                        resultShownValue.setValue(Boolean.FALSE);
                        hidePositions();
                        return;
                    }
                    appendLog("Checking position " + positionString);
                    sampleWrapper.setSamplePositionFromString(positionString,
                        bin);
                    if (sampleWrapper.isPositionFree(bin)) {
                        sampleWrapper.setParent(bin);

                        showPositions();

                        resultShownValue.setValue(Boolean.TRUE);
                        cancelConfirmWidget.setFocus();
                    } else {
                        String msg = "Position "
                            + sampleWrapper.getPositionString()
                            + " already in use in container " + bin.getLabel();
                        BioBankPlugin.openAsyncError("Position not free", msg);
                        appendLog("ERROR: " + msg);
                    }
                } catch (RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (BiobankCheckException bce) {
                    BioBankPlugin.openAsyncError(
                        "Error while checking position", bce);
                    appendLog("ERROR: " + bce.getMessage());
                    resultShownValue.setValue(Boolean.FALSE);
                } catch (Exception e) {
                    BioBankPlugin.openAsyncError(
                        "Error while checking position", e);
                }
                setDirty(true);
            }

        });
    }

    /**
     * In move mode, get informations from the existing sample
     * 
     * @throws Exception
     */
    protected void retrieveSampleInformations() throws Exception {
        resultShownValue.setValue(false);
        String inventoryId = inventoryIdText.getText();
        reset();
        sampleWrapper.setInventoryId(inventoryId);
        inventoryIdText.setText(inventoryId);

        appendLog("Getting informations for inventoryID "
            + sampleWrapper.getInventoryId());
        List<SampleWrapper> samples = SampleWrapper.getSamplesInSite(
            appService, sampleWrapper.getInventoryId(), SessionManager
                .getInstance().getCurrentSiteWrapper());
        if (samples.size() > 1) {
            throw new Exception(
                "Error while retrieving sample with inventoryId "
                    + sampleWrapper.getInventoryId()
                    + ": more than one sample found.");
        }
        if (samples.size() == 0) {
            throw new Exception("No sample found with inventoryId "
                + sampleWrapper.getInventoryId());
        }
        sampleWrapper = samples.get(0);
        currentPatient = sampleWrapper.getPatientVisit().getPatient();
        patientNumberText.setText(currentPatient.getNumber());
        List<PatientVisitWrapper> collection = currentPatient
            .getPatientVisitCollection();
        viewerVisits.setInput(collection);
        viewerVisits.setSelection(new StructuredSelection(sampleWrapper
            .getPatientVisit()));
        positionText.setText(sampleWrapper.getPositionString(true, false));
        viewerSampleTypes.setSelection(new StructuredSelection(sampleWrapper
            .getSampleType()));
        appendLog("Sample " + sampleWrapper.getInventoryId()
            + ": current position = " + sampleWrapper.getPositionString());
    }

    private void showPositions() {
        if (drawer == null || bin == null || cabinet == null) {
            cabinetWidget.setSelection(null);
            cabinetLabel.setText("Cabinet");
            drawerWidget.setSelection(null);
            drawerLabel.setText("Drawer");
        } else {
            cabinetWidget.setContainerType(cabinet.getContainerType());
            cabinetWidget.setSelection(drawer.getPosition());
            cabinetLabel.setText("Cabinet " + cabinet.getLabel());
            drawerWidget.setSelection(bin.getPosition());
            drawerLabel.setText("Drawer " + drawer.getLabel());
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
        appendLog("Checking parent container " + binLabel + " for type "
            + sampleWrapper.getSampleType().getName());
        List<ContainerWrapper> containers = ContainerWrapper
            .getContainersHoldingSampleType(appService, SessionManager
                .getInstance().getCurrentSiteWrapper(), binLabel, sampleWrapper
                .getSampleType());
        if (containers.size() == 1) {
            bin = containers.get(0);
            drawer = bin.getParent();
            cabinet = drawer.getParent();
        } else if (containers.size() == 0) {
            containers = ContainerWrapper.getContainersInSite(appService,
                SessionManager.getInstance().getCurrentSiteWrapper(), binLabel);
            String errorMsg = null;
            if (containers.size() > 0) {
                errorMsg = "Bin labelled " + binLabel
                    + " cannot hold samples of type "
                    + sampleWrapper.getSampleType().getName();
            } else {
                errorMsg = "Can't find bin labelled " + binLabel;
            }
            if (errorMsg != null) {
                BioBankPlugin.openError("Check position and sample", errorMsg);
                appendLog("ERROR: " + errorMsg);
            }
            return;
        } else {
            throw new Exception("More than one container found for " + binLabel
                + " --- should do something");
        }
    }

    @Override
    public void reset() throws Exception {
        sampleWrapper.resetToNewObject();
        cabinet = null;
        drawer = null;
        bin = null;
        cabinetWidget.setSelection(null);
        drawerWidget.setSelection(null);
        resultShownValue.setValue(Boolean.FALSE);
        selectedSampleTypeValue.setValue("");
        patientNumberText.setText("");
        viewerVisits.setInput(null);
        inventoryIdText.setText("");
        positionText.setText("");
        if (viewerSampleTypes.getCombo().getItemCount() > 1) {
            viewerSampleTypes.getCombo().deselectAll();
        }
    }

    @Override
    protected void saveForm() throws Exception {
        if (radioNew.getSelection()) {
            sampleWrapper.setLinkDate(new Date());
            sampleWrapper.setPatientVisit(getSelectedPatientVisit());
            sampleWrapper.setQuantityFromType();
        }
        sampleWrapper.persist();
        if (radioNew.getSelection()) {
            appendLog("Sample " + sampleWrapper.getInventoryId()
                + " saved in position " + sampleWrapper.getPositionString()
                + " for visit "
                + sampleWrapper.getPatientVisit().getFormattedDateProcessed()
                + "(patient " + currentPatient.getNumber() + ")");
        } else {
            appendLog("Sample " + sampleWrapper.getInventoryId()
                + " moved to position " + sampleWrapper.getPositionString());
        }
        setSaved(true);
    }

    private PatientVisitWrapper getSelectedPatientVisit() {
        if (viewerVisits.getSelection() != null
            && viewerVisits.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) viewerVisits
                .getSelection();
            if (selection.size() == 1)
                return (PatientVisitWrapper) selection.getFirstElement();
        }
        return null;
    }

    @Override
    protected String getOkMessage() {
        return "Add cabinet samples.";
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
                && status.getMessage().contentEquals(CHECK_CLICK_MESSAGE)) {
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
        return "Cabinet link/assign activity";
    }
}
