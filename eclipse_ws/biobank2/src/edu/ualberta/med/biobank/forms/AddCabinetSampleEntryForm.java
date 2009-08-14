package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.ModelUtils;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.validators.CabinetLabelValidator;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.CabinetDrawerWidget;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.ViewContainerWidget;

public class AddCabinetSampleEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.CabinetLinkEntryForm";

    private Patient currentPatient;

    private Label cabinetLabel;
    private Label drawerLabel;
    private ViewContainerWidget cabinetWidget;
    private CabinetDrawerWidget drawerWidget;

    private ComboViewer comboViewerSampleTypes;
    private Text inventoryIdText;
    private Text positionText;
    private Button showPosition;

    private CancelConfirmWidget cancelConfirmWidget;

    private IObservableValue cabinetPosition = new WritableValue("",
        String.class);
    private IObservableValue resultShown = new WritableValue(Boolean.FALSE,
        Boolean.class);
    private IObservableValue selectedSampleType = new WritableValue("",
        String.class);

    private ContainerType binType;
    private Sample sample = new Sample();
    private Container cabinet;
    private Container drawer;
    private Container bin;

    private boolean isSaved = false;

    @Override
    protected void init() {
        setPartName("Cabinet Link/Process");
    }

    @Override
    protected void createFormContent() {
        form.setText("Link and Process Cabinet Samples");
        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);

        createLocationSection();
        addSeparator();
        createFieldsSection();
        addSeparator();
        cancelConfirmWidget = new CancelConfirmWidget(form.getBody(), this,
            true);

        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            resultShown, "Show results to check values");
    }

    private void createLocationSection() {
        // get storage type Cabinet from database
        binType = ModelUtils.getBinType(appService);
        if (binType == null) {
            BioBankPlugin.openAsyncError("Cabinet error",
                "Cannot find a storage type for bin !");
            form.setEnabled(false);
        }

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

    private void createFieldsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        GridData gd = new GridData();
        gd.widthHint = 200;
        client.setLayoutData(gd);
        toolkit.paintBordersFor(client);

        Combo comboSampleType = (Combo) createBoundWidgetWithLabel(client,
            Combo.class, SWT.NONE, "Sample type", new String[0],
            selectedSampleType, NonEmptyString.class,
            "A sample type should be selected");
        comboViewerSampleTypes = new ComboViewer(comboSampleType);
        comboViewerSampleTypes.setContentProvider(new ArrayContentProvider());
        comboViewerSampleTypes.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                SampleType st = (SampleType) element;
                return st.getName();
            }
        });
        if (binType != null) {
            comboViewerSampleTypes.setInput(binType.getSampleTypeCollection());
        }

        inventoryIdText = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.NONE, "Inventory ID", new String[0], PojoObservables
                .observeValue(sample, "inventoryId"), NonEmptyString.class,
            "Enter Inventory Id (eg cdfg or DYUO)");
        inventoryIdText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        positionText = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.NONE, "Position", new String[0], cabinetPosition,
            CabinetLabelValidator.class, "Enter a position (eg 01AA01AB)");
        positionText.removeKeyListener(keyListener);
        positionText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        showPosition = toolkit.createButton(client, "Show Position", SWT.PUSH);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.CENTER;
        showPosition.setLayoutData(gd);
        showPosition.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showPositionResult();
            }
        });
    }

    protected void showPositionResult() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    String positionString = cabinetPosition.getValue()
                        .toString();

                    // FIXME this test doesn't work, no container has this
                    // position, this is the sample position !
                    Container sc = ModelUtils.getContainerWithLabel(appService,
                        positionString, null);
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

                        IStructuredSelection stSelection = (IStructuredSelection) comboViewerSampleTypes
                            .getSelection();
                        sample.setSampleType((SampleType) stSelection
                            .getFirstElement());

                        resultShown.setValue(Boolean.TRUE);
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

    protected SamplePosition getSamplePosition(String positionString)
        throws Exception {
        int end = 2;
        String cabinetString = positionString.substring(0, end);
        cabinet = ModelUtils.getContainerWithLabel(appService, cabinetString,
            "Cabinet");
        if (cabinet == null) {
            return null;
        }
        end += 2;
        String drawerString = positionString.substring(0, end);
        drawer = ModelUtils.getContainerWithLabel(appService, drawerString,
            "Drawer");
        if (drawer == null
            || !drawer.getPosition().getParentContainer().getId().equals(
                cabinet.getId())) {
            return null;
        }
        end += 2;
        String binString = positionString.substring(0, end);
        bin = ModelUtils.getContainerWithLabel(appService, binString, "Bin");
        if (bin == null
            || !bin.getPosition().getParentContainer().getId().equals(
                drawer.getId())) {
            return null;
        }
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
    protected void saveForm() throws RuntimeException {
        // BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
        // public void run() {
        // try {
        // sample.setPatientVisit(patientVisit);
        // appService.executeQuery(new InsertExampleQuery(sample));
        // activityToPrint = true;
        // } catch (RemoteConnectFailureException exp) {
        // BioBankPlugin.openRemoteConnectErrorMessage();
        // } catch (Exception e) {
        // SessionManager.getLogger().error(
        // "Error when saving cabinet position", e);
        // }
        // }
        // });
        isSaved = true;
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
            cancelConfirmWidget.setEnabled(false);
            if (status.getMessage() != null
                && status.getMessage().contains("check values")) {
                showPosition.setEnabled(true);
            } else {
                showPosition.setEnabled(false);
            }
        }
    }

    /**
     * Called from the BiobankPartListener
     */
    public void onClose() {
        if (!isSaved && BioBankPlugin.isAskPrint()) {
            boolean doPrint = MessageDialog.openQuestion(PlatformUI
                .getWorkbench().getActiveWorkbenchWindow().getShell(), "Print",
                "Do you want to print information ?");
            if (doPrint) {
                // FIXME implement print functionnality
            }
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return null;
    }

}
