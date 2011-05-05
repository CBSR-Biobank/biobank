package edu.ualberta.med.biobank.forms;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
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
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.data.ShipmentProcessData;
import edu.ualberta.med.biobank.common.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.DispatchCreateScanDialog;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DispatchSpecimensTreeTable;
import edu.ualberta.med.biobank.widgets.infotables.DispatchSpecimenListInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableSelection;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchSendingEntryForm extends AbstractDispatchEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchSendingEntryForm";

    public static final String MSG_NEW_DISPATCH_OK = "Creating a new dispatch record.";

    public static final String MSG_DISPATCH_OK = "Editing an existing dispatch record.";

    private ComboViewer destSiteComboViewer;

    private ComboViewer shippingMethodViewer;

    protected DispatchSpecimenListInfoTable specimensNonProcessedTable;

    private DispatchSpecimensTreeTable specimensTreeTable;

    private ShipmentInfoWrapper shipmentInfo = null;

    @Override
    protected void init() throws Exception {
        super.init();

        if (dispatch.isNew()) {
            dispatch.setSenderCenter(SessionManager.getUser()
                .getCurrentWorkingCenter());
        } else {
            shipmentInfo = dispatch.getShipmentInfo();
        }
    }

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

        setFirstControl(client);

        createReceiverCombo(client);

        if (!dispatch.isNew() && !dispatch.isInCreationState()) {
            ShippingMethodWrapper selectedShippingMethod = dispatch
                .getShipmentInfo().getShippingMethod();
            shippingMethodViewer = widgetCreator.createComboViewer(client,
                "Shipping Method", ShippingMethodWrapper
                    .getShippingMethods(SessionManager.getAppService()),
                selectedShippingMethod, null, new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        dispatch.getShipmentInfo().setShippingMethod(
                            (ShippingMethodWrapper) selectedObject);
                    }
                });

            createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
                "Waybill", null, shipmentInfo,
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

        createSpecimensSelectionSection();
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
                        .getUser().getCurrentWorkingCenter()),
                    dispatch.getReceiverCenter(),
                    "Dispatch must have a receiver",
                    new ComboSelectionUpdate() {
                        @Override
                        public void doSelection(Object selectedObject) {
                            dispatch
                                .setReceiverCenter((CenterWrapper<?>) selectedObject);
                            setDirty(true);
                        }
                    });
            } catch (ApplicationException e) {
                BiobankPlugin.openAsyncError("Error",
                    "Unable to retrieve Centers");
            }
        }
    }

    private void createSpecimensSelectionSection() {
        if (dispatch.isInCreationState()) {
            Section section = createSection("Specimens");
            Composite composite = toolkit.createComposite(section);
            composite.setLayout(new GridLayout(1, false));
            section.setClient(composite);
            if (dispatch.isInCreationState()) {
                addSectionToolbar(section, "Add specimens to this dispatch",
                    new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            openScanDialog();
                        }
                    }, null, BiobankPlugin.IMG_DISPATCH_SHIPMENT_ADD_SPECIMEN);

                createSpecimensSelectionActions(composite, false);
                createSpecimensNonProcessedSection(true);
            }
        } else {
            specimensTreeTable = new DispatchSpecimensTreeTable(page, dispatch,
                !dispatch.isInClosedState() && !dispatch.isInLostState(), true);
            specimensTreeTable.addSelectionChangedListener(biobankListener);
        }
    }

    protected void createSpecimensNonProcessedSection(boolean edit) {
        String title = "Specimens";
        if (dispatch.isInCreationState()) {
            title = "Added specimens";
        }
        Composite parent = createSectionWithClient(title);
        specimensNonProcessedTable = new DispatchSpecimenListInfoTable(parent,
            dispatch, edit) {
            @Override
            public List<DispatchSpecimenWrapper> getInternalDispatchSpecimens() {
                return dispatch.getNonProcessedDispatchSpecimenCollection();
            }

        };
        specimensNonProcessedTable.adaptToToolkit(toolkit, true);
        specimensNonProcessedTable.addClickListener(new IDoubleClickListener() {
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
        specimensNonProcessedTable.addSelectionChangedListener(biobankListener);
    }

    @Override
    protected void openScanDialog() {
        DispatchCreateScanDialog dialog = new DispatchCreateScanDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            dispatch, SessionManager.getUser().getCurrentWorkingCenter());
        dialog.open();
        setDirty(true); // FIXME add a boolean in the dialog to know if
                        // specimens were added
        reloadSpecimens();
    }

    @Override
    protected void doSpecimenTextAction(String inventoryId) {
        try {
            CellProcessResult res = appService.processCellStatus(new Cell(-1,
                -1, inventoryId, null), new ShipmentProcessData(null, dispatch,
                true, true), SessionManager.getUser());
            switch (res.getProcessStatus()) {
            case FILLED:
                // ok
                SpecimenWrapper specimen = new SpecimenWrapper(appService);
                specimen.getWrappedObject()
                    .setId(res.getCell().getSpecimenId());
                specimen.reload();
                dispatch.addSpecimens(Arrays.asList(specimen),
                    DispatchSpecimenState.NONE);
                reloadSpecimens();
                break;
            case ERROR:
                BiobankPlugin.openAsyncError("Invalid specimen", res.getCell()
                    .getInformation());
                break;
            }
        } catch (Exception e) {
            BiobankPlugin.openAsyncError("Error", "Error adding the specimen",
                e);
        }
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
    protected void onReset() throws Exception {
        CenterWrapper<?> sender = dispatch.getSenderCenter();
        super.onReset();
        dispatch.setSenderCenter(sender);

        if (shipmentInfo != null) {
            shipmentInfo.reset();
            dispatch.setShipmentInfo(shipmentInfo);

            GuiUtil.reset(shippingMethodViewer,
                shipmentInfo.getShippingMethod());
        }

        GuiUtil.reset(destSiteComboViewer, dispatch.getReceiverCenter());
    }

    @Override
    protected String getTextForPartName() {
        if (dispatch.isNew()) {
            return "New Dispatch";
        } else {
            Assert.isNotNull(dispatch, "Dispatch is null");
            String label = dispatch.getSenderCenter().getNameShort() + " -> "
                + dispatch.getReceiverCenter().getNameShort();

            String packedAt = dispatch.getFormattedPackedAt();
            if (packedAt != null)
                label += " [" + packedAt + "]";
            return label;
        }
    }

    @Override
    protected void reloadSpecimens() {
        if (specimensNonProcessedTable != null) {
            specimensNonProcessedTable.reloadCollection();
            page.layout(true, true);
            book.reflow(true);
        }
        if (specimensTreeTable != null) {
            specimensTreeTable.refresh();
        }
    }
}
