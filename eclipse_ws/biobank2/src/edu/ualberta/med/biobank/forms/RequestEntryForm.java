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
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.RequestState;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.RequestReceiveScanDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.request.RequestAdapter;
import edu.ualberta.med.biobank.widgets.trees.RequestSpecimensTreeTable;

public class RequestEntryForm extends BiobankFormBase {

    public static final String ID = "edu.ualberta.med.biobank.forms.RequestEntryFormBase";
    private RequestWrapper request;
    private RequestSpecimensTreeTable specimensTree;
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

        BgcBaseText orderNumberLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Request Number");
        setTextValue(orderNumberLabel, request.getId());
        BgcBaseText requestStateLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "State");
        setTextValue(requestStateLabel,
            RequestState.getState(request.getState()));

        BgcBaseText studyLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Study");
        setTextValue(studyLabel, request.getStudy());

        BgcBaseText researchGroupLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Research Group");
        setTextValue(researchGroupLabel, request.getStudy().getResearchGroup()
            .getNameShort());
        BgcBaseText submittedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Date Submitted");
        setTextValue(submittedLabel,
            DateFormatter.formatAsDateTime(request.getSubmitted()));
        createReadOnlyLabelledField(client, SWT.NONE, "Comments");
        Section s = createSection("Specimens");
        Composite c = toolkit.createComposite(s);
        s.setClient(c);
        c.setLayout(new GridLayout());
        c.setLayoutData(new GridData());
        createSpecimensSelectionActions(c, false);

        specimensTree = new RequestSpecimensTreeTable(c, request);

        button = new Button(c, SWT.PUSH);
        button.setVisible(false);
        button.setText("Dispatch Specimens");

    }

    @SuppressWarnings("unused")
    protected void createSpecimensSelectionActions(Composite composite,
        boolean setAsFirstControl) {
        Composite addComposite = toolkit.createComposite(composite);
        addComposite.setLayout(new GridLayout(5, false));
        toolkit.createLabel(addComposite, "Enter inventory ID to add:");
        final BgcBaseText newSpecimenText = new BgcBaseText(addComposite,
            SWT.NONE, toolkit);
        Button addButton = toolkit.createButton(addComposite, "", SWT.PUSH);
        addButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_ADD));
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    request.receiveSpecimen(newSpecimenText.getText());
                } catch (Exception e1) {
                    BgcPlugin.openAsyncError("Save Error", e1);
                }
                newSpecimenText.setFocus();
                newSpecimenText.setText("");
                specimensTree.refresh();
                try {
                    button.setEnabled(request.isAllProcessed());
                } catch (Exception ex) {
                    BgcPlugin.openAsyncError("Query error", ex);
                }
            }
        });
        toolkit.createLabel(addComposite, "or open scan dialog:");
        Button openScanButton = toolkit
            .createButton(addComposite, "", SWT.PUSH);
        openScanButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_DISPATCH_SHIPMENT_ADD_SPECIMEN));
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
        if (dialog.hasReceivedSpecimens()) {
            // setDirty(true);
        }
        specimensTree.refresh();
        try {
            button.setEnabled(request.isAllProcessed());
        } catch (Exception e) {
            BgcPlugin.openAsyncError("Query error", e);
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

}
