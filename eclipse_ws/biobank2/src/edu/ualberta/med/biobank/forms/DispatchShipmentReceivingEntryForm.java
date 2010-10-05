package edu.ualberta.med.biobank.forms;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.DispatchReceiveScanDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchShipmentAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.DispatchAliquotListInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchShipmentReceivingEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(DispatchShipmentReceivingEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchShipmentReceivingEntryForm";

    @SuppressWarnings("unused")
    private DispatchShipmentAdapter shipmentAdapter;

    private DispatchShipmentWrapper shipment;

    private DispatchAliquotListInfoTable aliquotsNonProcessedTable;

    private DispatchAliquotListInfoTable aliquotsReceivedTable;

    private DispatchAliquotListInfoTable aliquotsExtraTable;

    private DispatchAliquotListInfoTable aliquotsMissingTable;

    @Override
    protected void init() throws Exception {
        Assert.isNotNull(adapter, "Adapter should be no null");
        Assert.isTrue((adapter instanceof DispatchShipmentAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipmentAdapter = (DispatchShipmentAdapter) adapter;
        shipment = (DispatchShipmentWrapper) adapter.getModelObject();
        retrieveShipment();
        setPartName("Dispatch Shipment sent on " + shipment.getDateShipped());
    }

    private void retrieveShipment() {
        try {
            shipment.reload();
        } catch (Exception ex) {
            logger.error(
                "Error while retrieving shipment " + shipment.getWaybill(), ex);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Shipment sent on " + shipment.getFormattedDateShipped()
            + " from " + shipment.getSender().getNameShort());
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createMainSection();
        createAliquotAcceptSection();
        createAliquotsNonProcessedSection();
        createAliquotsReceivedSection();
        createAliquotsExtraSection();
        createAliquotsMissingSection();
    }

    private void createAliquotAcceptSection() {
        Composite addComposite = toolkit.createComposite(page);
        addComposite.setLayout(new GridLayout(5, false));
        toolkit.createLabel(addComposite, "Enter inventory ID to accept:");
        final BiobankText newAliquotText = new BiobankText(addComposite,
            SWT.NONE, toolkit);
        newAliquotText.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                acceptAliquot(newAliquotText.getText());
                newAliquotText.setFocus();
                newAliquotText.setText("");
            }
        });
        setFirstControl(newAliquotText);
        Button addButton = toolkit.createButton(addComposite, "", SWT.PUSH);
        addButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_ADD));
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                acceptAliquot(newAliquotText.getText());
            }
        });
        toolkit.createLabel(addComposite, "or open scan dialog:");
        Button openScanButton = toolkit
            .createButton(addComposite, "", SWT.PUSH);
        openScanButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_DISPATCH_SHIPMENT_ADD_ALIQUOT));
        openScanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openScanDialog();
            }
        });
    }

    private void openScanDialog() {
        DispatchReceiveScanDialog dialog = new DispatchReceiveScanDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            shipment);
        dialog.open();
        if (dialog.hasAcceptedAliquots()) {
            setDirty(true);
        }
        reloadAliquotsTables();
    }

    private void createAliquotsNonProcessedSection() {
        Composite parent = createSectionWithClient("Non processed aliquots");
        aliquotsNonProcessedTable = new DispatchAliquotListInfoTable(parent,
            shipment, false) {
            @Override
            public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                return shipment
                    .getNonProcessedDispatchShipmentAliquotCollection();
            }

        };
        aliquotsNonProcessedTable.adaptToToolkit(toolkit, true);
        aliquotsNonProcessedTable
            .addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createAliquotsReceivedSection() {
        Composite parent = createSectionWithClient("Aliquots received");
        aliquotsReceivedTable = new DispatchAliquotListInfoTable(parent,
            shipment, false) {
            @Override
            public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                return shipment.getReceivedDispatchShipmentAliquots();
            }
        };
        aliquotsReceivedTable.adaptToToolkit(toolkit, true);
        aliquotsReceivedTable
            .addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createAliquotsExtraSection() {
        Composite parent = createSectionWithClient("Extra Aliquots");
        aliquotsExtraTable = new DispatchAliquotListInfoTable(parent, shipment,
            false) {
            @Override
            public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                return shipment.getExtraDispatchShipmentAliquots();
            }

        };
        aliquotsExtraTable.adaptToToolkit(toolkit, true);
        aliquotsExtraTable
            .addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createAliquotsMissingSection() {
        Composite parent = createSectionWithClient("Missing Aliquots");
        aliquotsMissingTable = new DispatchAliquotListInfoTable(parent,
            shipment, false) {
            @Override
            public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                return shipment.getMissingDispatchShipmentAliquots();
            }

        };
        aliquotsMissingTable.adaptToToolkit(toolkit, true);
        aliquotsMissingTable
            .addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createMainSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        BiobankText studyLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Study");
        setTextValue(studyLabel, shipment.getStudy().getName());
        BiobankText senderLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Sender");
        setTextValue(senderLabel, shipment.getSender().getName());
        BiobankText receiverLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Receiver");
        setTextValue(receiverLabel, shipment.getReceiver().getName());
        BiobankText dateShippedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Date Shipped");
        setTextValue(dateShippedLabel, shipment.getFormattedDateShipped());
        BiobankText shippingMethodLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Shipping Method");
        setTextValue(shippingMethodLabel,
            shipment.getShippingMethod() == null ? "" : shipment
                .getShippingMethod().getName());
        BiobankText waybillLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Waybill");
        setTextValue(waybillLabel, shipment.getWaybill());
        BiobankText dateReceivedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Date received");
        setTextValue(dateReceivedLabel, shipment.getFormattedDateReceived());

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            "Comments", null, shipment, "comment", null);
    }

    @Override
    protected void saveForm() throws Exception {
        shipment.persist();
    }

    @Override
    protected String getOkMessage() {
        return "Receiving dispatch shipment";
    }

    @Override
    public String getNextOpenedFormID() {
        return DispatchShipmentViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        reloadAliquotsTables();
    }

    private void reloadAliquotsTables() {
        // TODO
        // aliquotsExpectedTable.reloadCollection(shipment.getDispatchedAliquots(
        // true, true));
        // aliquotsReceivedTable.reloadCollection(shipment.getActiveAliquots(true,
        // true));
        // aliquotsFlaggedTable.reloadCollection(shipment.getFlaggedAliquots(true,
        // true));
        page.layout(true, true);
        book.reflow(true);
    }

    public enum ResType {
        OK, NOT_IN_SHIPMENT, FLAGGED, NOT_IN_DB, DUPLICATE, ACCEPTED;
    }

    public static class AliquotInfo {
        public AliquotWrapper aliquot;
        public ResType type;

        public AliquotInfo(AliquotWrapper aliquot, ResType type) {
            this.aliquot = aliquot;
            this.type = type;
        }
    }

    public static AliquotInfo getInfoForInventoryId(
        DispatchShipmentWrapper shipment, String inventoryId) {
        AliquotWrapper aliquot = shipment.getAliquot(inventoryId);
        if (aliquot == null) {
            // aliquot not in shipment. Check if exists in DB:
            List<AliquotWrapper> aliquots = null;
            try {
                aliquots = AliquotWrapper.getAliquots(shipment.getAppService(),
                    inventoryId);
            } catch (ApplicationException ae) {
                BioBankPlugin.openAsyncError("Error retrieving aliquot", ae);
            }
            if (aliquots == null || aliquots.size() == 0) {
                return new AliquotInfo(null, ResType.NOT_IN_DB);
            }
            if (aliquots.size() > 1) {
                BioBankPlugin.openError("Duplicate aliquot !",
                    "This aliquot exists more that once in the database !");
                return new AliquotInfo(null, ResType.DUPLICATE);
            }
            return new AliquotInfo(aliquots.get(0), ResType.NOT_IN_SHIPMENT);
        }
        if (aliquot.isFlagged())
            return new AliquotInfo(aliquot, ResType.FLAGGED);
        if (aliquot.isActive())
            return new AliquotInfo(aliquot, ResType.ACCEPTED);
        return new AliquotInfo(aliquot, ResType.OK);
    }

    protected void acceptAliquot(String inventoryId) {
        AliquotInfo info = getInfoForInventoryId(shipment, inventoryId);
        switch (info.type) {
        case OK:
            boolean yes = BioBankPlugin.openConfirm(
                "Aliquot found",
                "Following aliquot has been found: \n"
                    + info.aliquot.getInventoryId() + " with type "
                    + info.aliquot.getSampleType().getNameShort()
                    + " in patient "
                    + info.aliquot.getPatientVisit().getPatient().getPnumber()
                    + ".\nDo you wish to accept it ?");
            if (yes) {
                try {
                    shipment.receiveAliquots(Arrays.asList(info.aliquot));
                    reloadAliquotsTables();
                    setDirty(true);
                } catch (Exception e) {
                    BioBankPlugin.openAsyncError("Error", e);
                }
            }
            break;
        case ACCEPTED:
            BioBankPlugin.openInformation("Aliquot already accepted",
                "Aliquot with inventory id " + inventoryId
                    + " was already accepted.");
            break;
        case FLAGGED:
            BioBankPlugin
                .openInformation(
                    "Aliquot Flagged",
                    "Aliquot with inventory id "
                        + inventoryId
                        + " was found in this shipment but is flagged."
                        + " \nPlease see comments on this aliquot to know how to proceed.");
            break;
        case NOT_IN_SHIPMENT:
            boolean flagIt = BioBankPlugin.openConfirm("Aliquot not found",
                "Aliquot with inventory id " + inventoryId
                    + " has not been found in this shipment."
                    + " Do you want to add it and flagged it ?");
            if (flagIt) {
                try {
                    shipment.addNotInShipmentAliquots(Arrays
                        .asList(info.aliquot));
                    reloadAliquotsTables();
                    setDirty(true);
                } catch (Exception e) {
                    BioBankPlugin.openAsyncError(
                        "Error setting status on aliquot", e);
                }
            }
            break;
        case NOT_IN_DB:
            BioBankPlugin.openError("Aliquot not found",
                "This aliquot does not exists in the database.");
            break;
        case DUPLICATE:
            BioBankPlugin.openError("Duplicate aliquot !",
                "This aliquot exists more that once in the database !");
            break;
        }
    }

}
