package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.RequestState;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
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
        BiobankText submittedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Date Submitted");
        setTextValue(submittedLabel,
            DateFormatter.formatAsDateTime(request.getSubmitted()));
        createReadOnlyLabelledField(client, SWT.NONE, "Comments");
        Section s = createSection("Aliquots");
        Composite c = toolkit.createComposite(s);
        s.setClient(c);
        c.setLayout(new GridLayout());
        c.setLayoutData(new GridData());
        createAliquotsSelectionActions(c, false);

        aliquotsTree = new RequestAliquotsTreeTable(c, request);

        button = new Button(c, SWT.PUSH);
        button.setVisible(false);
        button.setText("Dispatch Specimens");

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
        addButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_ADD));
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    request.receiveAliquot(newAliquotText.getText());
                } catch (Exception e1) {
                    BiobankPlugin.openAsyncError("Save Error", e1);
                }
                newAliquotText.setFocus();
                newAliquotText.setText("");
                aliquotsTree.refresh();
                try {
                    button.setEnabled(request.isAllProcessed());
                } catch (Exception ex) {
                    BiobankPlugin.openAsyncError("Query error", ex);
                }
            }
        });
        toolkit.createLabel(addComposite, "or open scan dialog:");
        Button openScanButton = toolkit
            .createButton(addComposite, "", SWT.PUSH);
        openScanButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_DISPATCH_SHIPMENT_ADD_ALIQUOT));
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
            request, request.getRequester());
        dialog.open();
        if (dialog.hasReceivedAliquots()) {
            // setDirty(true);
        }
        aliquotsTree.refresh();
        try {
            button.setEnabled(request.isAllProcessed());
        } catch (Exception e) {
            BiobankPlugin.openAsyncError("Query error", e);
        }
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
        RequestSpecimenWrapper dsa = ((RequestWrapper) currentShipment)
            .getRequestSpecimen(inventoryId);
        if (dsa == null) {
            // aliquot not in shipment. Check if exists in DB:
            SpecimenWrapper aliquot = null;
            try {
                aliquot = SpecimenWrapper.getSpecimen(
                    currentShipment.getAppService(), inventoryId,
                    SessionManager.getUser());
            } catch (Exception ae) {
                BiobankPlugin.openAsyncError("Error retrieving aliquot", ae);
            }
            if (aliquot == null) {
                return new AliquotInfo(null, ResType.NOT_IN_DB);
            }
            return new AliquotInfo(aliquot, ResType.NOT_IN_SHIPMENT);
        }
        if (DispatchSpecimenState.RECEIVED.isEquals(dsa.getState())) {
            return new AliquotInfo(dsa.getSpecimen(), ResType.RECEIVED);
        }
        if (DispatchSpecimenState.EXTRA.isEquals(dsa.getState())) {
            return new AliquotInfo(dsa.getSpecimen(), ResType.EXTRA);
        }
        return new AliquotInfo(dsa.getSpecimen(), ResType.OK);
    }
}
