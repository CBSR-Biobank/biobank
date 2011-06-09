package edu.ualberta.med.biobank.forms;

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
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.common.util.RequestState;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.RequestReceiveScanDialog;
import edu.ualberta.med.biobank.treeview.request.RequestAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.RequestSpecimensTreeTable;
import edu.ualberta.med.biobank.widgets.infotables.RequestDispatchInfoTable;

/* FIXME: HAMFISTED REFRESHES: SHOULD BE IMPROVED */

public class RequestEntryForm extends BiobankFormBase {

    public static final String ID = "edu.ualberta.med.biobank.forms.RequestEntryFormBase";
    private RequestWrapper request;
    private RequestSpecimensTreeTable specimensTree;
    private RequestDispatchInfoTable dispatchTable;
    private BiobankText newSpecimenText;
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

        BiobankText orderNumberLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Request Number");
        setTextValue(orderNumberLabel, request.getId());
        BiobankText requestStateLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "State");
        setTextValue(requestStateLabel,
            RequestState.getState(request.getState()));

        BiobankText studyLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Study");
        setTextValue(studyLabel, request.getStudy().getNameShort());

        BiobankText researchGroupLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Research Group");
        setTextValue(researchGroupLabel, request.getStudy().getResearchGroup()
            .getNameShort());
        BiobankText submittedLabel = createReadOnlyLabelledField(client,
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
        addSectionToolbar(s2,
            Messages.getString("RequestEntryForm.dispatch.add.title"),
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    buildNewDispatch();
                }
            });
        dispatchCreation.setLayout(new GridLayout(5, false));
        toolkit
            .createLabel(dispatchCreation, "Enter/Scan inventory ID to add:");
        newSpecimenText = new BiobankText(dispatchCreation, SWT.NONE, toolkit);
        addButton = toolkit.createButton(dispatchCreation, "", SWT.PUSH);
        addButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_ADD));
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SpecimenWrapper specimen;
                try {
                    specimen = SpecimenWrapper.getSpecimen(
                        SessionManager.getAppService(),
                        newSpecimenText.getText(), SessionManager.getUser());
                    if (specimen != null)
                        addToDispatch(getDispatchSelection(),
                            Arrays.asList(specimen));
                } catch (Exception e1) {
                    BiobankPlugin.openAsyncError("Database Error", e1);
                }
                newSpecimenText.setText("");
                specimensTree.refresh();
                dispatchTable.reloadCollection(
                    request.getDispatchCollection(false),
                    getDispatchSelection());
            }
        });

        toolkit.createLabel(dispatchCreation, "Add Pallet:");
        openScanButton = toolkit.createButton(dispatchCreation, "", SWT.PUSH);
        openScanButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_DISPATCH_SHIPMENT_ADD_SPECIMEN));
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
        openScanButton.setEnabled(getDispatchSelection() != null);
        addButton.setEnabled(getDispatchSelection() != null);
        newSpecimenText.setEnabled(getDispatchSelection() != null);
    }

    protected DispatchWrapper getDispatchSelection() {
        return dispatchTable.getSelection();
    }

    @SuppressWarnings("unused")
    protected void createSpecimensSelectionActions(Composite composite,
        boolean setAsFirstControl) {
        Composite addComposite = toolkit.createComposite(composite);
        addComposite.setLayout(new GridLayout(5, false));
        toolkit.createLabel(addComposite, "Enter/Scan inventory ID to pull:");
        final BiobankText newSpecimenText = new BiobankText(addComposite,
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
                    BiobankPlugin.openAsyncError("Save Error", e1);
                }
                newSpecimenText.setFocus();
                newSpecimenText.setText("");
                specimensTree.refresh();
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
            BiobankPlugin.openAsyncError("Unable to create dispatch", e);
        }
    }

    protected void addToDispatch(DispatchWrapper dispatch,
        List<SpecimenWrapper> selectionWrappers) {
        // FIXME: SHOULD BE IN ONE TRANSACTION
        try {
            Boolean found = false;
            List<RequestSpecimenWrapper> specs = request
                .getProcessedRequestSpecimenCollection();
            for (SpecimenWrapper spec : selectionWrappers) {
                for (RequestSpecimenWrapper rspec : specs) {
                    if (spec.equals(rspec.getSpecimen())) {
                        rspec.setState(RequestSpecimenState.DISPATCHED_STATE);
                        rspec.persist();
                        found = true;
                    }
                }
                if (found == false)
                    throw new Exception(
                        "Error Adding: Specimen has not been pulled.");
                found = false;
            }
            dispatch
                .addSpecimens(selectionWrappers, DispatchSpecimenState.NONE);
            dispatch.persist();
        } catch (Exception e) {
            BiobankPlugin.openAsyncError("Add Failed", e);
        }

    }
}
