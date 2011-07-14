package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.RequestReceiveScanDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;
import edu.ualberta.med.biobank.treeview.request.RequestAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import edu.ualberta.med.biobank.widgets.infotables.RequestDispatchInfoTable;
import edu.ualberta.med.biobank.widgets.trees.RequestSpecimensTreeTable;

public class RequestEntryForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.RequestEntryFormBase";
    private RequestWrapper request;
    private RequestSpecimensTreeTable specimensTree;
    private RequestDispatchInfoTable dispatchTable;
    private BgcBaseText newSpecimenText;
    private Button addButton;
    private Button openScanButton;

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

        BgcBaseText studyLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Study");
        setTextValue(studyLabel, request.getStudy().getNameShort());

        BgcBaseText researchGroupLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Research Group");

        request.getStudy().getResearchGroup().getNameShort();

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
        specimensTree.addListener(SWT.CHANGED, new Listener() {

            @Override
            public void handleEvent(Event event) {
                SpecimenTransitView.reloadCurrent();
            }
        });

        Section s2 = createSection("Dispatches");
        Composite dispatchCreation = toolkit.createComposite(s2);
        s2.setClient(dispatchCreation);
        addSectionToolbar(s2, Messages.RequestEntryForm_dispatch_add_title,
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    buildNewDispatch();
                }
            });
        dispatchCreation.setLayout(new GridLayout(5, false));
        toolkit
            .createLabel(dispatchCreation, "Enter/Scan inventory ID to add:");
        newSpecimenText = new BgcBaseText(dispatchCreation, SWT.NONE, toolkit);
        addButton = toolkit.createButton(dispatchCreation, "", SWT.PUSH);
        addButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_ADD));
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                RequestSpecimenWrapper specimen;
                TreeItemAdapter specNode;
                try {
                    specNode = (TreeItemAdapter) specimensTree
                        .search(newSpecimenText.getText());
                    if (specNode == null)
                        throw new Exception("Specimen not found");
                    specimen = (RequestSpecimenWrapper) specNode.getSpecimen();
                    if (specimen != null) {
                        addToDispatch(getDispatchSelection(),
                            Arrays.asList(specimen));
                        specimensTree.dispatch(specNode);
                    }
                } catch (Exception e1) {
                    BgcPlugin.openAsyncError("Error", e1.getMessage());
                }
                newSpecimenText.setText("");
                dispatchTable.reloadCollection(
                    request.getDispatchCollection(false),
                    getDispatchSelection());
            }
        });

        toolkit.createLabel(dispatchCreation, "Add Pallet:");
        openScanButton = toolkit.createButton(dispatchCreation, "", SWT.PUSH);
        openScanButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_DISPATCH_SHIPMENT_ADD_SPECIMEN));
        openScanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openScanDialog();
                specimensTree.refresh();
                dispatchTable.reloadCollection(
                    request.getDispatchCollection(false),
                    getDispatchSelection());
            }
        });

        dispatchTable = new RequestDispatchInfoTable(dispatchCreation,
            request.getDispatchCollection(false));
        dispatchTable.addClickListener(collectionDoubleClickListener);
        dispatchTable.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setEnabledActions();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub

            }
        });
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 5;
        dispatchTable.setLayoutData(gd);

        openScanButton.setEnabled(false);
        addButton.setEnabled(false);
        newSpecimenText.setEnabled(false);

    }

    protected void setEnabledActions() {
        Boolean b = getDispatchSelection() != null
            && getDispatchSelection().isInCreationState();
        openScanButton.setEnabled(b);
        addButton.setEnabled(b);
        newSpecimenText.setEnabled(b);
    }

    protected DispatchWrapper getDispatchSelection() {
        return dispatchTable.getSelection();
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
        addButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_ADD));
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    Node updateNode = specimensTree.search(newSpecimenText
                        .getText());
                    if (updateNode == null)
                        throw new Exception("Specimen "
                            + newSpecimenText.getText()
                            + " is not a valid choice.");
                    else if (((TreeItemAdapter) updateNode).getSpecimen()
                        .getSpecimenState()
                        .equals(RequestSpecimenState.AVAILABLE_STATE)) {
                        RequestSpecimenWrapper spec = (RequestSpecimenWrapper) ((TreeItemAdapter) updateNode)
                            .getSpecimen();
                        if (spec.getSpecimen().getInventoryId()
                            .equals(newSpecimenText.getText())) {
                            if (spec.getClaimedBy() == null
                                || !spec.getClaimedBy().equals(
                                    SessionManager.getUser().getFirstName()))
                                throw new Exception(
                                    "You must claim this specimen before pulling is permitted.");
                            request.flagSpecimens(Arrays.asList(spec));
                            specimensTree.pull(updateNode);
                        }
                    } else
                        throw new Exception(
                            "This specimen has been already been processed.");

                } catch (Exception e1) {
                    BgcPlugin.openAsyncError("Error", e1.getMessage());
                }
                newSpecimenText.setFocus();
                newSpecimenText.setText("");
            }
        });
    }

    protected void openScanDialog() {
        RequestReceiveScanDialog dialog = new RequestReceiveScanDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            request, SessionManager.getUser().getCurrentWorkingCenter());
        dialog.open();
        if (dialog.hasReceivedSpecimens()) {
            // setDirty(true);
        }
        specimensTree.refresh();
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

    protected void buildNewDispatch() {
        DispatchWrapper dispatch = new DispatchWrapper(
            SessionManager.getAppService());

        // FIXME: SHOULD BE IN ONE TRANSACTION
        try {

            dispatch.setSenderCenter(SessionManager.getUser()
                .getCurrentWorkingCenter());

            dispatch.setState(DispatchState.CREATION);
            dispatch.setReceiverCenter(request.getStudy().getResearchGroup());
            dispatch.persist();
            request.reload();
            request.addToDispatchCollection(Arrays.asList(dispatch));
            request.persist();
            dispatchTable.reloadCollection(
                request.getDispatchCollection(false), getDispatchSelection());
            SpecimenTransitView.reloadCurrent();
        } catch (Exception e) {
            BgcPlugin.openAsyncError("Unable to create dispatch", e);
        }
    }

    protected void addToDispatch(DispatchWrapper dispatch,
        List<RequestSpecimenWrapper> specs) throws Exception {
        // FIXME: SHOULD BE IN ONE TRANSACTION
        List<SpecimenWrapper> dispatchSpecimens = new ArrayList<SpecimenWrapper>();
        for (RequestSpecimenWrapper rspec : specs) {
            if (rspec.getSpecimenState().equals(
                RequestSpecimenState.PULLED_STATE)) {
                rspec.setState(RequestSpecimenState.DISPATCHED_STATE);
                rspec.persist();
                dispatchSpecimens.add(rspec.getSpecimen());
            } else
                throw new Exception(
                    "Error Adding: Specimen has not been pulled.");
        }
        dispatch.addSpecimens(dispatchSpecimens, DispatchSpecimenState.NONE);
        dispatch.persist();
    }

    @Override
    public void reload() throws Exception {
        request.reload();
        specimensTree.refresh();
        dispatchTable.setCollection(request.getDispatchCollection(false));
        dispatchTable.setSelection(null);
        setEnabledActions();
    }
}
