package edu.ualberta.med.biobank.forms;

import java.util.Collection;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
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
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.CabinetDrawerWidget;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.ViewContainerWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.example.InsertExampleQuery;

public class CabinetLinkAssignEntryForm extends AbstractPatientAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.CabinetLinkAssignEntryForm";

    private Patient currentPatient;

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
    private Button showPosition;

    private CancelConfirmWidget cancelConfirmWidget;

    private IObservableValue patientNumberValue = new WritableValue("",
        String.class);
    private IObservableValue visitSelectionValue = new WritableValue("",
        String.class);
    private IObservableValue cabinetPosition = new WritableValue("",
        String.class);
    private IObservableValue resultShown = new WritableValue(Boolean.FALSE,
        Boolean.class);
    private IObservableValue selectedSampleType = new WritableValue("",
        String.class);

    private Sample sample = new Sample();
    private Container cabinet;
    private Container drawer;
    private Container bin;

    @Override
    protected void init() {
        setPartName("Cabinet Link/Process");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Link and Process Cabinet Samples");
        GridLayout layout = new GridLayout(2, false);
        form.getBody().setLayout(layout);

        createFieldsSection();
        createLocationSection();

        cancelConfirmWidget = new CancelConfirmWidget(form.getBody(), this,
            true);

        cancelConfirmWidget.showCloseButton(true);

        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            resultShown, "Show results to check values");
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
        gdBin.widthHint = CabinetDrawerWidget.WIDTH;
        gdBin.heightHint = CabinetDrawerWidget.HEIGHT;
        gdBin.verticalSpan = 2;
        drawerWidget.setLayoutData(gdBin);

    }

    private void createFieldsSection() throws Exception {
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
            PojoObservables.observeValue(sample, "inventoryId"),
            NonEmptyString.class, "Enter Inventory Id (eg cdfg or DYUO)");
        inventoryIdText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        positionText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, "Position", new String[0], cabinetPosition,
            NonEmptyString.class, "Enter a position (eg 01AA01AB)");
        positionText.removeKeyListener(keyListener);
        positionText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        positionText.addListener(SWT.DefaultSelection, new Listener() {
            public void handleEvent(Event e) {
                if (showPosition.isEnabled()) {
                    showPositionResult();
                }
            }
        });

        Combo comboSampleType = (Combo) createBoundWidgetWithLabel(
            fieldsComposite, Combo.class, SWT.NONE, "Sample type",
            new String[0], selectedSampleType, null,
            "A sample type should be selected");
        // FIXME the Validator should be there after the show position is set !
        // FIXME change place of this combo ?
        comboViewerSampleTypes = new ComboViewer(comboSampleType);
        comboViewerSampleTypes.setContentProvider(new ArrayContentProvider());
        comboViewerSampleTypes.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                SampleType st = (SampleType) element;
                return st.getName();
            }
        });

        showPosition = toolkit.createButton(fieldsComposite, "Show Position",
            SWT.PUSH);
        gd = new GridData();
        gd.horizontalSpan = 2;
        showPosition.setLayoutData(gd);
        showPosition.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showPositionResult();
            }
        });
    }

    private void createVisitCombo(Composite client) throws Exception {
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
                PatientVisit pv = (PatientVisit) element;
                return BioBankPlugin.getDateTimeFormatter().format(
                    pv.getDateDrawn());
            }
        });
        comboVisits.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == 13) {
                    comboViewerSampleTypes.getCombo().setFocus();
                }
            }
        });
    }

    protected void setVisitsList() {
        String pNumber = patientNumberText.getText();
        currentPatient = null;
        try {
            currentPatient = PatientWrapper.getPatientInSite(appService,
                pNumber, SessionManager.getInstance().getCurrentSite());
        } catch (ApplicationException e) {
            BioBankPlugin.openError("Error getting the patient", e);
        }
        if (currentPatient != null) {
            // show visits list
            Collection<PatientVisit> collection = currentPatient
                .getPatientVisitCollection();
            viewerVisits.setInput(collection);
            comboVisits.select(0);
            comboVisits.setListVisible(true);
        }
    }

    protected void showPositionResult() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    String positionString = cabinetPosition.getValue()
                        .toString();

                    // FIXME this test doesn't work, no container has this
                    // position, this is the sample position !
                    Container sc = ContainerWrapper.getContainerWithTypeInSite(
                        appService, SessionManager.getInstance()
                            .getCurrentSite(), positionString, null);
                    if (sc == null) {
                        SamplePosition sp = getSamplePosition(positionString);
                        if (sp == null) {
                            BioBankPlugin.openError("Cabinet error",
                                "Parent containers not found");
                            return;
                        }
                        Point drawerPosition = new Point(drawer.getPosition()
                            .getPositionDimensionOne(), drawer.getPosition()
                            .getPositionDimensionTwo());
                        cabinetWidget.setSelectedBox(drawerPosition);
                        cabinetLabel.setText("Cabinet " + cabinet.getLabel());
                        drawerWidget.setSelectedBin(bin.getPosition()
                            .getPositionDimensionOne());
                        drawerLabel.setText("Drawer " + drawer.getLabel());

                        sp.setSample(sample);
                        sample.setSamplePosition(sp);

                        showSampleType();

                        resultShown.setValue(Boolean.TRUE);
                        cancelConfirmWidget.setFocus();
                    } else {
                        BioBankPlugin.openError("Cabinet error",
                            "This position is already in use");
                    }
                } catch (RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (Exception e) {
                    SessionManager.getLogger().error(
                        "Error while showing positions", e);
                }
                setDirty(true);
            }

        });
    }

    protected void showSampleType() {
        Collection<SampleType> sampleTypes = null;
        ContainerType parentContainerType = sample.getSamplePosition()
            .getContainer().getContainerType();
        sampleTypes = parentContainerType.getSampleTypeCollection();
        if (sampleTypes == null || sampleTypes.size() == 0) {
            BioBankPlugin.openAsyncError("Cabinet error",
                "Cannot find a sample types!");
            form.setEnabled(false);
        }
        comboViewerSampleTypes.setInput(sampleTypes);
        if (sampleTypes.size() == 1) {
            comboViewerSampleTypes.getCombo().select(0);
        }
    }

    protected SamplePosition getSamplePosition(String positionString)
        throws Exception {
        // int end = 2;
        bin = ContainerWrapper.getContainerWithTypeInSite(appService,
            SessionManager.getInstance().getCurrentSite(), positionString
                .substring(0, 6), "Bin");
        drawer = bin.getPosition().getParentContainer();
        cabinet = drawer.getPosition().getParentContainer();

        // FIXME use label scheme for position !!!!
        SamplePosition sp = new SamplePosition();
        sp.setContainer(bin);
        char letter = positionString.substring(6, 7).toCharArray()[0];
        // positions start at A
        sp.setPositionDimensionOne(letter - 'A');
        letter = positionString.substring(7).toCharArray()[0];
        sp.setPositionDimensionTwo(letter - 'A');
        return sp;
    }

    @Override
    public void cancelForm() {
        sample = null;
        cabinet = null;
        drawer = null;
        bin = null;
        cabinetWidget.setSelectedBox(null);
        drawerWidget.setSelectedBin(0);
        resultShown.setValue(Boolean.FALSE);
        selectedSampleType.setValue("");
        inventoryIdText.setText("");
        positionText.setText("");
    }

    @Override
    protected void saveForm() throws Exception {
        PatientVisit patientVisit = getSelectedPatientVisit();
        sample.setPatientVisit(patientVisit);
        IStructuredSelection stSelection = (IStructuredSelection) comboViewerSampleTypes
            .getSelection();
        sample.setSampleType((SampleType) stSelection.getFirstElement());
        appService.executeQuery(new InsertExampleQuery(sample));
        setSaved(true);
    }

    private PatientVisit getSelectedPatientVisit() {
        if (viewerVisits.getSelection() != null
            && viewerVisits.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) viewerVisits
                .getSelection();
            if (selection.size() == 1)
                return (PatientVisit) selection.getFirstElement();
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
            showPosition.setEnabled(true);
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            cancelConfirmWidget.setConfirmEnabled(false);
            if (status.getMessage() != null
                && status.getMessage().contains("check values")) {
                showPosition.setEnabled(true);
            } else {
                showPosition.setEnabled(false);
            }
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return ID;
    }

    @Override
    protected void print() {
        // FIXME implement print functionnality
        System.out.println("PRINT activity");
    }

}
