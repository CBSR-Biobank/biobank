package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.DispatchAliquotState;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.treeview.order.OrderAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;

public class OrderEntryFormBase extends BiobankFormBase {

    public static final String ID = "edu.ualberta.med.biobank.forms.OrderEntryFormBase";

    // private OrderAliquotsTreeTable aliquotsTree;

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Order placed on " + "2010-04-07" + " from "
            + "KDCS Research Group");
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createMainSection();

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
        setTextValue(studyLabel, "KDCS");

        BiobankText senderLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Research Group");
        setTextValue(senderLabel, "KDCS Research Group");
        BiobankText receiverLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Receiver");
        setTextValue(receiverLabel, "CBSR");
        BiobankText departedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Order Placed");
        setTextValue(departedLabel, "2010-04-07");
        BiobankText shippingMethodLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Order Number");
        setTextValue(shippingMethodLabel, "123532132");
        BiobankText dateReceivedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Date received");
        setTextValue(dateReceivedLabel, "2010-04-09");
        createReadOnlyLabelledField(client, SWT.NONE, "Comments");
        Section s = createSection("Aliquots");

        Composite c = new Composite(s, SWT.NONE);
        c.setLayout(new GridLayout());
        createAliquotsSelectionActions(c, false);
        // aliquotsTree = new OrderAliquotsTreeTable(c, null, true, true);

        s.setClient(c);

        Button button = new Button(c, SWT.PUSH);
        button.setText("Action Button");
    }

    public enum ResType {
        OK, NOT_IN_SHIPMENT, NOT_IN_DB, DUPLICATE, RECEIVED, EXTRA;
    }

    public static class AliquotInfo {
        public AliquotWrapper aliquot;
        public ResType type;

        public AliquotInfo(AliquotWrapper aliquot, ResType type) {
            this.aliquot = aliquot;
            this.type = type;
        }
    }

    public static AliquotInfo getInfoForInventoryId(DispatchWrapper shipment,
        String inventoryId) {
        DispatchAliquotWrapper dsa = shipment.getDispatchAliquot(inventoryId);
        if (dsa == null) {
            // aliquot not in shipment. Check if exists in DB:
            AliquotWrapper aliquot = null;
            try {
                aliquot = AliquotWrapper.getAliquot(shipment.getAppService(),
                    inventoryId, SessionManager.getUser());
            } catch (Exception ae) {
                BioBankPlugin.openAsyncError("Error retrieving aliquot", ae);
            }
            if (aliquot == null) {
                return new AliquotInfo(null, ResType.NOT_IN_DB);
            }
            return new AliquotInfo(aliquot, ResType.NOT_IN_SHIPMENT);
        }
        if (DispatchAliquotState.RECEIVED_STATE.isEquals(dsa.getState())) {
            return new AliquotInfo(dsa.getAliquot(), ResType.RECEIVED);
        }
        if (DispatchAliquotState.EXTRA.isEquals(dsa.getState())) {
            return new AliquotInfo(dsa.getAliquot(), ResType.EXTRA);
        }
        return new AliquotInfo(dsa.getAliquot(), ResType.OK);
    }

    @SuppressWarnings("unused")
    protected void createAliquotsSelectionActions(Composite composite,
        boolean setAsFirstControl) {
        Composite addComposite = toolkit.createComposite(composite);
        addComposite.setLayout(new GridLayout(5, false));
        toolkit.createLabel(addComposite, "Enter inventory ID to add:");
        final BiobankText newAliquotText = new BiobankText(addComposite,
            SWT.NONE, toolkit);
        Button addButton = toolkit.createButton(addComposite, "", SWT.PUSH);
        addButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_ADD));
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                newAliquotText.setFocus();
                newAliquotText.setText("");
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
            }
        });
    }

    @Override
    protected void init() throws Exception {
        Assert.isNotNull(adapter, "Adapter should be no null");
        Assert.isTrue((adapter instanceof OrderAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        setPartName("New Order");
    }
}
