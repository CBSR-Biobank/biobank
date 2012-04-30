package edu.ualberta.med.biobank.forms;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.DispatchCreateProcessAction;
import edu.ualberta.med.biobank.common.action.scanprocess.data.ShipmentProcessInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.DispatchCreateScanDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.DispatchSpecimenListInfoTable;
import edu.ualberta.med.biobank.widgets.trees.DispatchSpecimensTreeTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchSendingEntryForm extends AbstractDispatchEntryForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.DispatchSendingEntryForm"; 

    public static final String MSG_NEW_DISPATCH_OK =
        "Creating a new dispatch record.";

    public static final String MSG_DISPATCH_OK =
        "Editing an existing dispatch record.";

    private ComboViewer destSiteComboViewer;

    private ComboViewer shippingMethodViewer;

    protected DispatchSpecimenListInfoTable specimensNonProcessedTable;

    private DispatchSpecimensTreeTable specimensTreeTable;

    private ShipmentInfoWrapper shipmentInfo = null;

    private CommentsInfoTable commentEntryTable;

    @Override
    protected void init() throws Exception {
        super.init();

        if (dispatch.isNew()) {
            Assert
                .isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
            dispatch.getWrappedObject().setSenderCenter(
                SessionManager.getUser()
                    .getCurrentWorkingCenter().getWrappedObject());
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
                "Shipping Method",
                ShippingMethodWrapper.getShippingMethods(SessionManager
                    .getAppService()), selectedShippingMethod, null,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        dispatch.getShipmentInfo().setShippingMethod(
                            (ShippingMethodWrapper) selectedObject);
                    }
                }, new BiobankLabelProvider());

            createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
                "Waybill", null,
                shipmentInfo, ShipmentInfoPeer.WAYBILL.getName(), null);

            createDateTimeWidget(client, "Departed", null, shipmentInfo,
                ShipmentInfoPeer.PACKED_AT.getName(), null);
        }

        createCommentSection();

        createSpecimensSelectionSection();
    }

    private void createCommentSection() {
        Composite client = createSectionWithClient("Comments");
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable = new CommentsInfoTable(client,
            dispatch.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            "Add a comment", null, comment, "message", null);

    }

    private void createReceiverCombo(Composite client) {
        if (dispatch.isInTransitState()) {
            BgcBaseText receiverLabel = createReadOnlyLabelledField(client,
                SWT.NONE, "Receiver");
            setTextValue(receiverLabel, dispatch.getReceiverCenter()
                .getNameShort());
        } else {
            Assert
                .isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
            try {
                destSiteComboViewer = createComboViewer(client,
                    "Receiver",
                    CenterWrapper.getOtherCenters(SessionManager
                        .getAppService(), SessionManager.getUser()
                        .getCurrentWorkingCenter()),
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
                BgcPlugin
                    .openAsyncError(
                        "Error",
                        "Unable to retrieve Centers");
            }
        }
    }

    private void createSpecimensSelectionSection() {
        if (dispatch.isInCreationState()) {
            Section section =
                createSection("Specimens");
            Composite composite = toolkit.createComposite(section);
            composite.setLayout(new GridLayout(1, false));
            section.setClient(composite);
            if (dispatch.isInCreationState()) {
                addSectionToolbar(section,
                    "Add specimens to this dispatch",
                    new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            openScanDialog();
                        }
                    }, null, BgcPlugin.IMG_DISPATCH_SHIPMENT_ADD_SPECIMEN);

                createSpecimensSelectionActions(composite, false);
                createSpecimensNonProcessedSection(true);
            }
        } else {
            specimensTreeTable =
                new DispatchSpecimensTreeTable(page, dispatch,
                    !dispatch.isInClosedState() && !dispatch.isInLostState());
            specimensTreeTable.addSelectionChangedListener(biobankListener);
            specimensTreeTable.addClickListener();
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
                return dispatch.getDispatchSpecimenCollection(false);
            }
        };
        specimensNonProcessedTable.adaptToToolkit(toolkit, true);
        specimensNonProcessedTable
            .addClickListener(new IInfoTableDoubleClickItemListener<DispatchSpecimenWrapper>() {
                @Override
                public void doubleClick(
                    InfoTableEvent<DispatchSpecimenWrapper> event) {
                    Object selection = event.getSelection();
                    if (selection instanceof InfoTableSelection) {
                        InfoTableSelection tableSelection =
                            (InfoTableSelection) selection;
                        DispatchSpecimenWrapper dsa =
                            (DispatchSpecimenWrapper) tableSelection
                                .getObject();
                        if (dsa != null) {
                            SessionManager.openViewForm(dsa.getSpecimen());
                        }
                    }
                }
            });
        specimensNonProcessedTable
            .addEditItemListener(new IInfoTableEditItemListener<DispatchSpecimenWrapper>() {

                @Override
                public void editItem(
                    InfoTableEvent<DispatchSpecimenWrapper> event) {
                    Object selection = event.getSelection();
                    if (selection instanceof InfoTableSelection) {
                        InfoTableSelection tableSelection =
                            (InfoTableSelection) selection;
                        DispatchSpecimenWrapper dsa =
                            (DispatchSpecimenWrapper) tableSelection
                                .getObject();
                        if (dsa != null) {
                            new SpecimenAdapter(null, dsa.getSpecimen())
                                .openEntryForm();
                        }
                    }
                }
            });
        specimensNonProcessedTable.addSelectionChangedListener(biobankListener);
    }

    @Override
    protected void openScanDialog() {
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
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
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
        try {
            CellProcessResult res =
                (CellProcessResult) SessionManager
                    .getAppService().doAction(
                        new DispatchCreateProcessAction(
                            new ShipmentProcessInfo(null,
                                dispatch, true), SessionManager.getUser()
                                .getCurrentWorkingCenter().getId(),
                            new CellInfo(-1, -1, inventoryId, null),
                            Locale.getDefault()));
            switch (res.getProcessStatus()) {
            case FILLED:
                // ok
                Specimen spec = SessionManager.getAppService()
                    .doAction(new SpecimenGetInfoAction(res
                        .getCell().getSpecimenId())).getSpecimen();
                dispatch.addSpecimens(Arrays.asList(new SpecimenWrapper(
                    SessionManager.getAppService(), spec)),
                    DispatchSpecimenState.NONE);
                reloadSpecimens();
                setDirty(true);
                break;
            case ERROR:
                BgcPlugin.openAsyncError(
                    "Invalid specimen",
                    res.getCell().getInformation());
                break;
            }
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                "Error",
                "Error adding the specimen", e);
        }
    }

    @Override
    protected String getOkMessage() {
        return (dispatch.isNew()) ? MSG_NEW_DISPATCH_OK : MSG_DISPATCH_OK;
    }

    @Override
    public String getNextOpenedFormId() {
        return DispatchViewForm.ID;
    }

    @Override
    public void setValues() throws Exception {
        CenterWrapper<?> sender = dispatch.getSenderCenter();
        super.setValues();
        dispatch.setSenderCenter(sender);

        if (shipmentInfo != null) {
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
        }
        Assert.isNotNull(dispatch, "Dispatch is null"); 
        String label = dispatch.getSenderCenter().getNameShort() + " -> " 
            + dispatch.getReceiverCenter().getNameShort();

        String packedAt = dispatch.getFormattedPackedAt();
        if (packedAt != null)
            label += " [" + packedAt + "]";  
        return label;
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
