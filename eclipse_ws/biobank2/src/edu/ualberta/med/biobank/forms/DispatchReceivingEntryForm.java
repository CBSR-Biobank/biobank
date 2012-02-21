package edu.ualberta.med.biobank.forms;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentTempLoggerPeer;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.data.ShipmentProcessData;
import edu.ualberta.med.biobank.common.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.DispatchReceiveScanDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.widgets.trees.DispatchSpecimensTreeTable;

public class DispatchReceivingEntryForm extends AbstractDispatchEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchReceivingEntryForm";
    private DispatchSpecimensTreeTable specimensTree;

    private Button radioTempPass;
    private Button radioTempFail;
    private Button uploadButton;
    private String path;

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Dispatch sent on " + dispatch.getFormattedPackedAt()
            + " from " + dispatch.getSenderCenter().getNameShort());
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createMainSection();
        boolean editSpecimens = !dispatch.isInClosedState()
            && !dispatch.isInLostState();

        setFirstControl(form);

        if (editSpecimens)
            createSpecimensSelectionActions(page, true);
        specimensTree = new DispatchSpecimensTreeTable(page, dispatch,
            editSpecimens, true);
        specimensTree.addSelectionChangedListener(biobankListener);
    }

    private void createMainSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        BgcBaseText senderLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Sender");
        setTextValue(senderLabel, dispatch.getSenderCenter().getName());
        BgcBaseText receiverLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Receiver");
        setTextValue(receiverLabel, dispatch.getReceiverCenter().getName());
        BgcBaseText departedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Departed");
        setTextValue(departedLabel, dispatch.getFormattedPackedAt());
        BgcBaseText shippingMethodLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Shipping Method");
        setTextValue(shippingMethodLabel, dispatch.getShipmentInfo()
            .getShippingMethod() == null ? "" : dispatch.getShipmentInfo()
            .getShippingMethod().getName());
        BgcBaseText waybillLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Waybill");
        setTextValue(waybillLabel, dispatch.getShipmentInfo().getWaybill());
        BgcBaseText dateReceivedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Date received");
        setTextValue(dateReceivedLabel, dispatch.getShipmentInfo()
            .getFormattedDateReceived());

        if (dispatch.getShipmentInfo().getShipmentTempLogger() != null) {
            BgcBaseText deviceIdLabel = createReadOnlyLabelledField(client,
                SWT.NONE, "Logger Device ID");
            setTextValue(deviceIdLabel, dispatch.getShipmentInfo()
                .getShipmentTempLogger().getDeviceId());

            createBoundWidgetWithLabel(
                client,
                BgcBaseText.class,
                SWT.NONE,
                "Highest temperature during transport (Celcius)",
                null,
                dispatch.getShipmentInfo().getShipmentTempLogger(),
                ShipmentTempLoggerPeer.HIGH_TEMPERATURE.getName(),
                new NonEmptyStringValidator("Highest temperature should be set"));

            createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
                "Lowest temperature during transport (Celcius)", null, dispatch
                    .getShipmentInfo().getShipmentTempLogger(),
                ShipmentTempLoggerPeer.LOW_TEMPERATURE.getName(),
                new NonEmptyStringValidator("Lowest temperature should be set"));

            createPassFail(client);

            createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
                "Number of minutes above maximum threshold", null, dispatch
                    .getShipmentInfo().getShipmentTempLogger(),
                ShipmentTempLoggerPeer.MINUTES_ABOVE_MAX.getName(), null);

            createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
                "Number of minutes below maximum threshold", null, dispatch
                    .getShipmentInfo().getShipmentTempLogger(),
                ShipmentTempLoggerPeer.MINUTES_BELOW_MAX.getName(), null);

            createUpload(client);

        }

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            "Comments", null, dispatch, DispatchPeer.COMMENT.getName(), null);

    }

    protected void createPassFail(Composite client) {

        widgetCreator.createLabel(client, "Shipment temperature result");

        Composite composite = new Composite(client, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 30;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);

        radioTempPass = new Button(composite, SWT.RADIO);
        radioTempPass.setText("Pass");
        radioTempFail = new Button(composite, SWT.RADIO);
        radioTempFail.setText("Fail");

        if (dispatch.getShipmentInfo().getShipmentTempLogger()
            .getTemperatureResult() != null) {
            if (dispatch.getShipmentInfo().getShipmentTempLogger()
                .getTemperatureResult()) {
                radioTempPass.setSelection(true);
            } else {
                radioTempFail.setSelection(true);
            }
        }

        radioTempPass.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioTempPass.getSelection()) {
                    dispatch.getShipmentInfo().getShipmentTempLogger()
                        .setTemperatureResult(true);
                    setDirty(true);
                }
            }
        });

        radioTempFail.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioTempFail.getSelection()) {
                    dispatch.getShipmentInfo().getShipmentTempLogger()
                        .setTemperatureResult(false);
                    setDirty(true);
                }
            }
        });
    }

    private void createUpload(Composite parent) {

        widgetCreator.createLabel(parent, "Upload tempurature logger report");

        Composite composite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        layout.marginLeft = -5;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(composite);

        final BgcBaseText fileToUpload = widgetCreator.createReadOnlyField(
            composite, SWT.NONE, "PDF File", true);

        uploadButton = toolkit.createButton(composite, "Upload", SWT.NONE);
        uploadButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                String[] filterExt = new String[] { "*.pdf" };
                path = runFileDialog("home", filterExt);

                if (path != null)
                    fileToUpload.setText(path);
                File fl = new File(path);
                try {
                    dispatch.getShipmentInfo().getShipmentTempLogger()
                        .setFile(FileUtils.readFileToByteArray(fl));
                    // Make sure the persistence is called
                    setDirty(true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private String runFileDialog(String name, String[] exts) {
        FileDialog fd = new FileDialog(form.getShell(), SWT.OPEN);
        fd.setOverwrite(true);
        fd.setText("Select PDF");
        fd.setFilterExtensions(exts);
        fd.setFileName(name);
        return fd.open();
    }

    @Override
    protected void openScanDialog() {
        DispatchReceiveScanDialog dialog = new DispatchReceiveScanDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            dispatch, dispatch.getReceiverCenter());
        dialog.open();
        if (dispatch.hasNewSpecimens() || dispatch.hasSpecimenStatesChanged())
            setDirty(true);
        reloadSpecimens();
    }

    @Override
    protected void doSpecimenTextAction(String inventoryId) {
        try {
            CellProcessResult res = appService.processCellStatus(new Cell(-1,
                -1, inventoryId, null), new ShipmentProcessData(null, dispatch,
                false, false), SessionManager.getUser());
            SpecimenWrapper specimen = null;
            if (res.getCell().getSpecimenId() != null) {
                specimen = new SpecimenWrapper(appService);
                specimen.getWrappedObject()
                    .setId(res.getCell().getSpecimenId());
                specimen.reload();
            }
            switch (res.getCell().getStatus()) {
            case IN_SHIPMENT_EXPECTED:
                dispatch.receiveSpecimens(Arrays.asList(specimen));
                reloadSpecimens();
                setDirty(true);
                break;
            case IN_SHIPMENT_RECEIVED:
                BgcPlugin.openInformation("Specimen already accepted",
                    "Specimen with inventory id " + inventoryId
                        + " is already in received list.");
                break;
            case EXTRA:
                BgcPlugin.openInformation("Specimen not found",
                    "Specimen with inventory id " + inventoryId
                        + " has not been found in this dispatch."
                        + " It will be moved into the extra-pending list.");
                if (specimen == null) {
                    BgcPlugin.openAsyncError("Problem with specimen",
                        "Specimen is extra but object is null");
                    break;
                }
                dispatch.addSpecimens(Arrays.asList(specimen),
                    DispatchSpecimenState.EXTRA);
                reloadSpecimens();
                setDirty(true);
                break;
            default:
                BgcPlugin.openInformation("Problem with specimen", res
                    .getCell().getInformation());
            }
        } catch (Exception e) {
            BgcPlugin.openAsyncError("Error receiving the specimen", e);
        }
    }

    @Override
    protected String getOkMessage() {
        return "Receiving dispatch";
    }

    @Override
    public String getNextOpenedFormID() {
        return DispatchViewForm.ID;
    }

    @Override
    protected String getTextForPartName() {
        return "Dispatch sent on " + dispatch.getShipmentInfo().getPackedAt();
    }

    @Override
    protected void reloadSpecimens() {
        specimensTree.refresh();
    }

}
