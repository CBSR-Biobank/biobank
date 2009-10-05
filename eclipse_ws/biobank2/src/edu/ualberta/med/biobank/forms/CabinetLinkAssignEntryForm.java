package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.databinding.beans.PojoObservables;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.validators.CabinetLabelValidator;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.CabinetDrawerWidget;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.ViewContainerWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class CabinetLinkAssignEntryForm extends AbstractPatientAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.CabinetLinkAssignEntryForm";

    private PatientWrapper currentPatient;

    private Label cabinetLabel;
    private Label drawerLabel;
    private ViewContainerWidget cabinetWidget;
    private CabinetDrawerWidget drawerWidget;

    private Text patientNumberText;
    private CCombo comboVisits;
    private ComboViewer viewerVisits;
    private ComboViewer comboViewerSampleTypes;
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

    private static final String CHECK_CLICK_MESSAGE = "Click on check";

    @Override
    protected void init() {
        super.init();
        setPartName("Cabinet Link/Assign");
        sampleWrapper = new SampleWrapper(appService, new Sample());
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
    }

    private void createLocationSection() {
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

        cabinetWidget = new ViewContainerWidget(client);
        toolkit.adapt(cabinetWidget);
        cabinetWidget.setGridSizes(4, 1, 150, 150);
        cabinetWidget.setFirstColSign('A');
        cabinetWidget.setShowColumnFirst(true);
        GridData gdDrawer = new GridData();
        gdDrawer.verticalAlignment = SWT.TOP;
        cabinetWidget.setLayoutData(gdDrawer);

        drawerWidget = new CabinetDrawerWidget(client);
        toolkit.adapt(drawerWidget);
        GridData gdBin = new GridData();
        gdBin.verticalSpan = 2;
        drawerWidget.setLayoutData(gdBin);

    }

    private void createFieldsSection() {
        Composite fieldsComposite = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        fieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(fieldsComposite);
        GridData gd = new GridData();
        gd.widthHint = 400;
        gd.verticalAlignment = SWT.TOP;
        fieldsComposite.setLayoutData(gd);

        patientNumberText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Patient Number", new String[0],
            patientNumberValue, NonEmptyString.class, "Enter a patient number");
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
        createVisitCombo(fieldsComposite);

        inventoryIdText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Inventory ID", new String[0],
            PojoObservables.observeValue(sampleWrapper, "inventoryId"),
            NonEmptyString.class, "Enter Inventory Id");
        inventoryIdText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        positionText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Position", new String[0], positionValue,
            new CabinetLabelValidator("Enter a position (eg 01AA01AB)"));
        positionText.addListener(SWT.DefaultSelection, new Listener() {
            public void handleEvent(Event e) {
                if (checkPositionButton.isEnabled()) {
                    checkPositionAndSample();
                }
            }
        });

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

    private void createTypeCombo(Composite fieldsComposite) {
        List<SampleType> sampleTypes;
        try {
            sampleTypes = SampleTypeWrapper.getSampleTypeForContainerTypes(
                appService, SessionManager.getInstance()
                    .getCurrentSiteWrapper(), cabinetNameContains);
        } catch (ApplicationException e) {
            BioBankPlugin.openError("Initialisation failed", e);
            sampleTypes = new ArrayList<SampleType>();
        }
        comboViewerSampleTypes = createCComboViewerWithNoSelectionValidator(
            fieldsComposite, "Sample type", sampleTypes, null,
            "A sample type should be selected");
        comboViewerSampleTypes
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection stSelection = (IStructuredSelection) comboViewerSampleTypes
                        .getSelection();
                    sampleWrapper.setSampleType((SampleType) stSelection
                        .getFirstElement());
                }
            });
        if (sampleTypes.size() == 1) {
            comboViewerSampleTypes.getCCombo().select(0);
            sampleWrapper.setSampleType(sampleTypes.get(0));
        }
    }

    private void createVisitCombo(Composite client) {
        comboVisits = (CCombo) createBoundWidgetWithLabel(client, CCombo.class,
            SWT.READ_ONLY | SWT.BORDER | SWT.FLAT, "Visits", new String[0],
            visitSelectionValue, NonEmptyString.class,
            "A visit should be selected");
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
                return pv.getFormattedDateDrawn();
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
                sampleWrapper.setPatientVisit(pv.getWrappedObject());
                appendLog("Visit selected " + pv.getFormattedDateProcessed()
                    + " - " + pv.getClinic().getName());
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
            comboVisits.select(0);
            comboVisits.setListVisible(true);
        } catch (ApplicationException e) {
            BioBankPlugin.openError("Error getting the patient", e);
        }
    }

    protected void checkPositionAndSample() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    appendLog("----");
                    appendLog("Checking inventoryID "
                        + sampleWrapper.getInventoryId());
                    sampleWrapper.checkInventoryIdUnique();
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
                    sampleWrapper.checkPosition(bin);
                    sampleWrapper.getSamplePosition().setContainer(
                        bin.getWrappedObject());

                    showPositions();

                    resultShownValue.setValue(Boolean.TRUE);
                    cancelConfirmWidget.setFocus();
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

    private void showPositions() {
        if (drawer == null || bin == null || cabinet == null) {
            cabinetWidget.setSelectedBox(null);
            cabinetLabel.setText("Cabinet");
            drawerWidget.setSelectedBin(-1);
            drawerLabel.setText("Drawer");
        } else {
            Point drawerPosition = new Point(drawer.getPosition().getRow(),
                drawer.getPosition().getCol());
            cabinetWidget.setSelectedBox(drawerPosition);
            cabinetLabel.setText("Cabinet " + cabinet.getLabel());
            drawerWidget.setSelectedBin(bin.getPosition().getRow());
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
            drawer = bin.getPosition().getParentContainer();
            cabinet = drawer.getPosition().getParentContainer();
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
    public void resetForm() {
        sampleWrapper.setWrappedObject(new Sample());
        cabinet = null;
        drawer = null;
        bin = null;
        cabinetWidget.setSelectedBox(null);
        drawerWidget.setSelectedBin(0);
        resultShownValue.setValue(Boolean.FALSE);
        selectedSampleTypeValue.setValue("");
        inventoryIdText.setText("");
        positionText.setText("");
    }

    @Override
    protected void saveForm() throws Exception {
        sampleWrapper.setLinkDate(new Date());
        sampleWrapper.setPatientVisit(getSelectedPatientVisit()
            .getWrappedObject());
        sampleWrapper.setQuantityFromType();
        sampleWrapper.persist();
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
            checkPositionButton.setEnabled(true);
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            cancelConfirmWidget.setConfirmEnabled(false);
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
