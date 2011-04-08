package edu.ualberta.med.biobank.forms;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper.CheckStatus;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.DispatchCreateScanDialog;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DispatchAliquotsTreeTable;
import edu.ualberta.med.biobank.widgets.infotables.DispatchAliquotListInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableSelection;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchSendingEntryForm extends AbstractDispatchEntryForm {

    // private static BiobankLogger logger = BiobankLogger
    // .getLogger(DispatchSendingEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchSendingEntryForm";

    public static final String MSG_NEW_DISPATCH_OK = "Creating a new dispatch record.";

    public static final String MSG_DISPATCH_OK = "Editing an existing dispatch record.";

    private ComboViewer destSiteComboViewer;

    protected DispatchAliquotListInfoTable aliquotsNonProcessedTable;

    private DispatchAliquotsTreeTable aliquotsTreeTable;

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        // if the shipment is new, and if the combos hold only one element,
        // there will be default selections but dirty will be set to false by
        // default anyway
        if (dispatch.isNew()) {
            setDirty(true);
        }

    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Dispatch Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        if (dispatch.isNew()) {
            dispatch.setSenderCenter(SessionManager.getUser()
                .getCurrentWorkingCenter());
            dispatch.setState(DispatchState.CREATION);
        }

        setFirstControl(client);

        createReceiverCombo(client);

        if (!dispatch.isNew() && !dispatch.isInCreationState()) {
            ShippingMethodWrapper selectedShippingMethod = dispatch
                .getShipmentInfo().getShippingMethod();
            widgetCreator.createComboViewer(client, "Shipping Method",
                ShippingMethodWrapper.getShippingMethods(SessionManager
                    .getAppService()), selectedShippingMethod, null,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        dispatch.getShipmentInfo().setShippingMethod(
                            (ShippingMethodWrapper) selectedObject);
                    }
                });

            createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
                "Waybill", null, dispatch.getShipmentInfo(),
                ShipmentInfoPeer.WAYBILL.getName(), null);
        }

        createBoundWidgetWithLabel(
            client,
            BiobankText.class,
            SWT.MULTI,
            "Comments",
            null,
            BeansObservables.observeValue(dispatch,
                DispatchPeer.COMMENT.getName()), null);

        createAliquotsSelectionSection();

    }

    private void createReceiverCombo(Composite client) {
        if (dispatch.isInTransitState()) {
            BiobankText receiverLabel = createReadOnlyLabelledField(client,
                SWT.NONE, "Receiver");
            setTextValue(receiverLabel, dispatch.getReceiverCenter()
                .getNameShort());
        } else {
            try {
                destSiteComboViewer = createComboViewer(client, "Receiver",
                    CenterWrapper.getOtherCenters(appService, SessionManager
                        .getUser().getCurrentWorkingCenter()), null,
                    "Dispatch must have a receiver",
                    new ComboSelectionUpdate() {
                        @Override
                        public void doSelection(Object selectedObject) {
                            dispatch
                                .setReceiverCenter((CenterWrapper<?>) selectedObject);
                            setDirty(true);
                        }
                    });
                if (dispatch.getReceiverCenter() != null)
                    destSiteComboViewer.setSelection(new StructuredSelection(
                        dispatch.getReceiverCenter()));
            } catch (ApplicationException e) {
                BiobankPlugin.openAsyncError("Error",
                    "Unable to retrieve Centers");
            }
        }
    }

    @Override
    public void formClosed() throws Exception {
        // FIXME why formClosed is overridden ? reload is already done in
        // default
        // method
        reset();
    }

    private void createAliquotsSelectionSection() {
        if (dispatch.isInCreationState()) {
            Section section = createSection("Aliquot added");
            Composite composite = toolkit.createComposite(section);
            composite.setLayout(new GridLayout(1, false));
            section.setClient(composite);
            if (dispatch.isInCreationState()) {
                addSectionToolbar(section, "Add aliquots to this dispatch",
                    new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            openScanDialog();
                        }
                    }, null, BiobankPlugin.IMG_DISPATCH_SHIPMENT_ADD_ALIQUOT);

                createAliquotsSelectionActions(composite, false);
                createAliquotsNonProcessedSection(true);
            }
        } else {
            aliquotsTreeTable = new DispatchAliquotsTreeTable(page, dispatch,
                !dispatch.isInClosedState() && !dispatch.isInLostState(), true);
            aliquotsTreeTable.addSelectionChangedListener(biobankListener);
        }
    }

    protected void createAliquotsNonProcessedSection(boolean edit) {
        String title = "Non processed aliquots";
        if (dispatch.isInCreationState()) {
            title = "Added aliquots";
        }
        Composite parent = createSectionWithClient(title);
        aliquotsNonProcessedTable = new DispatchAliquotListInfoTable(parent,
            dispatch, edit) {
            @Override
            public List<DispatchSpecimenWrapper> getInternalDispatchAliquots() {
                return dispatch.getNonProcessedDispatchSpecimenCollection();
            }

        };
        aliquotsNonProcessedTable.adaptToToolkit(toolkit, true);
        aliquotsNonProcessedTable.addClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object selection = event.getSelection();
                if (selection instanceof InfoTableSelection) {
                    InfoTableSelection tableSelection = (InfoTableSelection) selection;
                    DispatchSpecimenWrapper dsa = (DispatchSpecimenWrapper) tableSelection
                        .getObject();
                    if (dsa != null) {
                        SessionManager.openViewForm(dsa.getSpecimen());
                    }
                }
            }
        });
        aliquotsNonProcessedTable.addSelectionChangedListener(biobankListener);
    }

    @Override
    protected void openScanDialog() {
        DispatchCreateScanDialog dialog = new DispatchCreateScanDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            dispatch, SessionManager.getUser().getCurrentWorkingCenter());
        dialog.open();
        setDirty(true); // FIXME need to do this better !
        reloadAliquots();
    }

    @Override
    protected void doAliquotTextAction(String text) {
        addAliquot(text);
    }

    protected void addAliquot(String inventoryId) {
        if (!inventoryId.isEmpty()) {
            SpecimenWrapper existingAliquot;
            try {
                existingAliquot = SpecimenWrapper.getSpecimen(
                    dispatch.getAppService(), inventoryId,
                    SessionManager.getUser());
                CheckStatus status = dispatch.checkCanAddSpecimen(
                    existingAliquot, true);
                if (status.ok)
                    addAliquot(existingAliquot);
                else
                    BiobankPlugin.openAsyncError("Error", status.message);
            } catch (Exception e) {
                BiobankPlugin.openAsyncError("Error",
                    "Unable to retrieve specimen info");
            }
        }
    }

    private void addAliquot(SpecimenWrapper aliquot) {
        List<SpecimenWrapper> aliquots = dispatch.getSpecimenCollection();
        if (aliquots != null && aliquots.contains(aliquot)) {
            BiobankPlugin.openAsyncError("Error",
                "Aliquot " + aliquot.getInventoryId()
                    + " has already been added to this dispatch");
            return;
        }
        try {
            dispatch.addSpecimens(Arrays.asList(aliquot));
        } catch (Exception e) {
            BiobankPlugin.openAsyncError("Error adding aliquots", e);
        }
        reloadAliquots();
    }

    @Override
    protected String getOkMessage() {
        return (dispatch.isNew()) ? MSG_NEW_DISPATCH_OK : MSG_DISPATCH_OK;
    }

    @Override
    public String getNextOpenedFormID() {
        return DispatchViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        dispatch.reset();
        dispatch.setSenderCenter(SessionManager.getUser()
            .getCurrentWorkingCenter());
        if (destSiteComboViewer != null) {
            CenterWrapper<?> destSite = dispatch.getReceiverCenter();
            if (destSite != null) {
                destSiteComboViewer.setSelection(new StructuredSelection(
                    destSite));
            } else if (destSiteComboViewer.getCombo().getItemCount() == 1)
                destSiteComboViewer.setSelection(new StructuredSelection(
                    destSiteComboViewer.getElementAt(0)));
            else
                destSiteComboViewer.getCombo().deselectAll();
        }
        reloadAliquots();
    }

    @Override
    protected void reloadAliquots() {
        if (aliquotsNonProcessedTable != null) {
            aliquotsNonProcessedTable.reloadCollection();
            page.layout(true, true);
            book.reflow(true);
        }
        if (aliquotsTreeTable != null) {
            aliquotsTreeTable.refresh();
        }
    }

    @Override
    protected String getTextForPartName() {
        if (dispatch.isNew()) {
            return "New Dispatch";
        } else {
            Assert.isNotNull(dispatch, "Dispatch is null");
            String label = new String();
            label += dispatch.getSenderCenter().getNameShort() + " -> "
                + dispatch.getReceiverCenter().getNameShort();

            if (dispatch.getShipmentInfo().getPackedAt() != null)
                label += "[" + dispatch.getFormattedPackedAt() + "]";
            return label;
        }
    }
}
