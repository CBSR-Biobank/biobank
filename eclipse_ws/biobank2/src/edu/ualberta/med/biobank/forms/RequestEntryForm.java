package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
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
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.common.action.info.RequestReadInfo;
import edu.ualberta.med.biobank.common.action.request.RequestDispatchAction;
import edu.ualberta.med.biobank.common.action.request.RequestGetInfoAction;
import edu.ualberta.med.biobank.common.action.request.RequestStateChangeAction;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.RequestReceiveScanDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import edu.ualberta.med.biobank.treeview.request.RequestAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import edu.ualberta.med.biobank.widgets.infotables.RequestDispatchInfoTable;
import edu.ualberta.med.biobank.widgets.trees.RequestSpecimensTreeTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class RequestEntryForm extends BiobankViewForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.RequestEntryFormBase";
    private final RequestWrapper request = new RequestWrapper(
        SessionManager.getAppService());
    private RequestSpecimensTreeTable specimensTree;
    private RequestDispatchInfoTable dispatchTable;
    private BgcBaseText newSpecimenText;
    private Button addButton;
    private Button openScanButton;

    @Override
    protected void init() throws Exception {
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
        Assert.isNotNull(adapter, "Adapter should be no null");
        Assert.isTrue((adapter instanceof RequestAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        setRequestInfo(adapter.getId());

        setPartName("Request "
            + request.getId().toString());
    }

    private void setRequestInfo(Integer id) throws ApplicationException {
        if (id == null) {
            Request r = new Request();
            request.setWrappedObject(r);
        } else {
            RequestReadInfo reqInfo = SessionManager.getAppService().doAction(
                new RequestGetInfoAction(id));
            request.setWrappedObject(reqInfo.request);
            SessionManager.logLookup(reqInfo.request);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(
            "Requested on {0} {1}", DateFormatter
                .formatAsDateTime(request.getSubmitted()),
            request.getResearchGroup()
                .getNameShort()));
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

        BgcBaseText researchGroupLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Research Group");

        setTextValue(researchGroupLabel, request.getResearchGroup()
            .getNameShort());

        BgcBaseText submittedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Date Submitted");
        setTextValue(submittedLabel,
            DateFormatter.formatAsDateTime(request.getSubmitted()));

        Section s = createSection("Specimens");
        Composite c = toolkit.createComposite(s);
        s.setClient(c);
        c.setLayout(new GridLayout());
        c.setLayoutData(new GridData());
        createSpecimensSelectionActions(c);

        specimensTree = new RequestSpecimensTreeTable(c, request);
        specimensTree.addListener(SWT.CHANGED, new Listener() {

            @Override
            public void handleEvent(Event event) {
                SpecimenTransitView.reloadCurrent();
            }
        });

        Section s2 =
            createSection("Dispatches");
        Composite dispatchCreation = toolkit.createComposite(s2);
        s2.setClient(dispatchCreation);
        addSectionToolbar(s2, "New dispatch",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    try {
                        buildNewDispatch();
                    } catch (Exception e1) {
                        BgcPlugin.openAsyncError("Error adding dispatch",
                            e1);
                    }
                }
            });
        dispatchCreation.setLayout(new GridLayout(5, false));
        toolkit.createLabel(dispatchCreation,
            "Enter/Scan inventory ID to add:");
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
                        throw new Exception(
                            "Specimen not found");
                    specimen = (RequestSpecimenWrapper) specNode.getSpecimen();
                    if (specimen != null) {
                        addToDispatch(getDispatchSelection(),
                            Arrays.asList(specimen));
                        specimensTree.dispatch(specNode);
                    }
                } catch (Exception e1) {
                    BgcPlugin.openAsyncError(
                        "Error", e1.getMessage());
                }
                newSpecimenText.setText("");
            }
        });

        toolkit.createLabel(dispatchCreation,
            "Add Pallet:");
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
        dispatchTable
            .addClickListener(new IInfoTableDoubleClickItemListener<DispatchWrapper>() {

                @Override
                public void doubleClick(InfoTableEvent<DispatchWrapper> event) {
                    DispatchWrapper d =
                        ((DispatchWrapper) ((InfoTableSelection) event
                            .getSelection()).getObject());
                    new DispatchAdapter(null, d).openViewForm();
                }

            });
        dispatchTable
            .addEditItemListener(new IInfoTableEditItemListener<DispatchWrapper>() {

                @Override
                public void editItem(InfoTableEvent<DispatchWrapper> event) {
                    DispatchWrapper d =
                        ((DispatchWrapper) ((InfoTableSelection) event
                            .getSelection()).getObject());
                    new DispatchAdapter(null, d).openEntryForm();
                }
            });
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

    protected void createSpecimensSelectionActions(Composite composite) {
        Composite addComposite = toolkit.createComposite(composite);
        addComposite.setLayout(new GridLayout(5, false));
        toolkit.createLabel(addComposite,
            "Enter inventory ID to add:");
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
                        throw new Exception(NLS.bind(
                            "Specimen {0} is not a valid choice.",
                            newSpecimenText.getText()));
                    else if (((TreeItemAdapter) updateNode).getSpecimen()
                        .getSpecimenState()
                        .equals(RequestSpecimenState.AVAILABLE_STATE)) {
                        RequestSpecimenWrapper spec =
                            (RequestSpecimenWrapper) ((TreeItemAdapter) updateNode)
                                .getSpecimen();
                        if (spec.getSpecimen().getInventoryId()
                            .equals(newSpecimenText.getText())) {
                            if (spec.getClaimedBy() == null
                                || !spec.getClaimedBy().equals(
                                    SessionManager.getUser().getLogin()))
                                throw new Exception(
                                    "You must claim this specimen before pulling is permitted.");
                            SessionManager.getAppService().doAction(
                                new RequestStateChangeAction(Arrays.asList(spec
                                    .getId()),
                                    RequestSpecimenState.PULLED_STATE));
                            spec.setState(RequestSpecimenState.PULLED_STATE);
                            specimensTree.pull(updateNode);
                        }
                    } else
                        throw new Exception(
                            "This specimen is not available for processing.");

                } catch (Exception e1) {
                    BgcPlugin.openAsyncError(
                        "Error", e1.getMessage());
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
            try {
                List<RequestSpecimenWrapper> rspecs =
                    new ArrayList<RequestSpecimenWrapper>();
                for (SpecimenWrapper spec : dialog.getSpecimens()) {
                    for (RequestSpecimenWrapper rs : specimensTree
                        .getWrappers())
                        if (rs.getSpecimen().equals(spec))
                            rspecs.add(rs);
                }
                addToDispatch(getDispatchSelection(), rspecs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        specimensTree.rebuild();
    }

    protected void buildNewDispatch() throws Exception {
        // create the dispatch
        DispatchWrapper d = new DispatchWrapper(SessionManager.getAppService());
        d.setSenderCenter(SessionManager.getUser()
            .getCurrentWorkingCenter());
        d.setReceiverCenter(request.getResearchGroup());
        d.setState(DispatchState.CREATION.getId());
        addToDispatch(d, null);
        reload();
        SpecimenTransitView.reloadCurrent();
    }

    protected void addToDispatch(DispatchWrapper dispatch,
        List<RequestSpecimenWrapper> specs) throws Exception {

        Set<DispatchSpecimenInfo> dsInfos = new HashSet<DispatchSpecimenInfo>();
        if (specs != null) {
            for (RequestSpecimenWrapper rs : specs)
                if (rs.getSpecimenState().equals(
                    RequestSpecimenState.PULLED_STATE)) {
                    dsInfos.add(new DispatchSpecimenInfo(null, rs.getSpecimen()
                        .getId(), DispatchSpecimenState.NONE.getId()));
                } else
                    throw new Exception(
                        "Error Adding: Specimen has not been pulled.");
        }
        DispatchSaveInfo dInfo =
            new DispatchSaveInfo(dispatch.getId(), request.getResearchGroup()
                .getId(),
                SessionManager.getUser()
                    .getCurrentWorkingCenter().getId(),
                DispatchState.CREATION.getId(), "");

        List<Integer> ids =
            new ArrayList<Integer>();
        if (specs != null)
            for (RequestSpecimenWrapper rs : specs) {
                ids.add(rs.getId());
            }
        RequestDispatchAction update =
            new RequestDispatchAction(request.getId(), ids,
                RequestSpecimenState.DISPATCHED_STATE, dInfo, dsInfos);
        SessionManager.getAppService().doAction(update);
        reload();
    }

    @Override
    public void setValues() throws Exception {
        specimensTree.refresh();
        dispatchTable.setList(request.getDispatchCollection(false));
        dispatchTable.setSelection(null);
        setEnabledActions();
    }
}
