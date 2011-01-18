package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.DispatchAliquotState;
import edu.ualberta.med.biobank.common.util.RequestState;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.dialogs.BiobankDialog;
import edu.ualberta.med.biobank.dialogs.RequestShippedDialog;
import edu.ualberta.med.biobank.dialogs.dispatch.RequestReceiveScanDialog;
import edu.ualberta.med.biobank.forms.DispatchReceivingEntryForm.AliquotInfo;
import edu.ualberta.med.biobank.forms.DispatchReceivingEntryForm.ResType;
import edu.ualberta.med.biobank.treeview.request.RequestAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.RequestAliquotsTreeTable;

public class RequestEntryFormBase extends BiobankFormBase {

    public static final String ID = "edu.ualberta.med.biobank.forms.RequestEntryFormBase";
    private RequestWrapper request;
    private RequestAliquotsTreeTable aliquotsTree;
    private Button button;

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Requested on "
            + DateFormatter.formatAsDateTime(request.getSubmitted()) + " "
            + request.getStudy().getNameShort());
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

        BiobankText orderNumberLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Request Number");
        setTextValue(orderNumberLabel, request.getId());
        BiobankText requestStateLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "State");
        setTextValue(requestStateLabel,
            RequestState.getState(request.getState()));

        BiobankText studyLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Study");
        setTextValue(studyLabel, request.getStudy());

        BiobankText researchGroupLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Research Group");
        setTextValue(researchGroupLabel, request.getStudy().getResearchGroup()
            .getNameShort());
        BiobankText siteLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Site");
        setTextValue(siteLabel, request.getSite().getNameShort());
        BiobankText submittedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Date Submitted");
        setTextValue(submittedLabel,
            DateFormatter.formatAsDateTime(request.getSubmitted()));
        BiobankText acceptedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Date Accepted");
        setTextValue(acceptedLabel,
            DateFormatter.formatAsDateTime(request.getAccepted()));
        BiobankText shippedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Date Shipped");
        setTextValue(shippedLabel,
            DateFormatter.formatAsDateTime(request.getShipped()));
        BiobankText waybillLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Waybill");
        setTextValue(waybillLabel, request.getWaybill());
        createReadOnlyLabelledField(client, SWT.NONE, "Comments");
        Section s = createSection("Aliquots");
        Composite c = toolkit.createComposite(s);
        s.setClient(c);
        c.setLayout(new GridLayout());
        c.setLayoutData(new GridData());
        if (request.isInAcceptedState())
            createAliquotsSelectionActions(c, false);

        aliquotsTree = new RequestAliquotsTreeTable(c, request);

        button = new Button(c, SWT.PUSH);
        Integer orderState = ((RequestWrapper) adapter.getModelObject())
            .getState();
        if (RequestState.getState(orderState).equals(RequestState.APPROVED)) {
            button.setText("Accept Order");
            button.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    request.setInAcceptedState();
                    ((RequestAdapter) adapter).persistAndRebuild();
                }

            });
        } else if (RequestState.getState(orderState).equals(
            RequestState.ACCEPTED)) {
            button.setText("Mark as Filled");
            button.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    request.setInFilledState();
                    ((RequestAdapter) adapter).persistAndRebuild();
                }

            });
            button.setEnabled(request.isAllProcessed());
            button
                .setToolTipText("All aliquots must be processed or unavailable to completely fill this request");
        } else if (RequestState.getState(orderState)
            .equals(RequestState.FILLED)) {
            button.setText("Mark as Shipped");
            button.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    BiobankDialog rfd = new RequestShippedDialog(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        request);
                    if (rfd.open() == Dialog.OK) {
                        request.setInShippedState();
                        ((RequestAdapter) adapter).persistAndRebuild();
                    }
                }

            });
        } else if (RequestState.getState(orderState).equals(
            RequestState.SHIPPED)) {
            button.setText("Close");
            button.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    request.setInCloseState();
                    ((RequestAdapter) adapter).persistAndRebuild();
                }

            });
        } else
            button.setVisible(false);

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
                try {
                    request.receiveAliquot(newAliquotText.getText());
                } catch (Exception e1) {
                    BioBankPlugin.openAsyncError("Save Error", e1);
                }
                newAliquotText.setFocus();
                newAliquotText.setText("");
                aliquotsTree.refresh();
                button.setEnabled(request.isAllProcessed());
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

    protected void openScanDialog() {
        RequestReceiveScanDialog dialog = new RequestReceiveScanDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            request, request.getSite());
        dialog.open();
        if (dialog.hasReceivedAliquots()) {
            // setDirty(true);
        }
        aliquotsTree.refresh();
        button.setEnabled(request.isAllProcessed());
    }

    @Override
    protected void init() throws Exception {
        Assert.isNotNull(adapter, "Adapter should be no null");
        Assert.isTrue((adapter instanceof RequestAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        this.request = (RequestWrapper) adapter.getModelObject();
        setPartName("Request " + request.getId().toString());
    }

    public static AliquotInfo getInfoForInventoryId(
        ModelWrapper<?> currentShipment, String inventoryId) {
        RequestAliquotWrapper dsa = ((RequestWrapper) currentShipment)
            .getRequestAliquot(inventoryId);
        if (dsa == null) {
            // aliquot not in shipment. Check if exists in DB:
            AliquotWrapper aliquot = null;
            try {
                aliquot = AliquotWrapper.getAliquot(
                    currentShipment.getAppService(), inventoryId,
                    SessionManager.getUser());
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
}
