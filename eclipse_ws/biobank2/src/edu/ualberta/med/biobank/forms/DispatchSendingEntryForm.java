package edu.ualberta.med.biobank.forms;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.SessionSecurityHelper;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.DispatchCreateProcessAction;
import edu.ualberta.med.biobank.common.action.scanprocess.data.ShipmentProcessInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.CellProcessResult;
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
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.DispatchSpecimenListInfoTable;
import edu.ualberta.med.biobank.widgets.trees.DispatchSpecimensTreeTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchSendingEntryForm extends AbstractDispatchEntryForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.DispatchSendingEntryForm"; //$NON-NLS-1$

    public static final String MSG_NEW_DISPATCH_OK =
        Messages.DispatchSendingEntryForm_new_ok_msg;

    public static final String MSG_DISPATCH_OK =
        Messages.DispatchSendingEntryForm_edit_ok_msg;

    private ComboViewer destSiteComboViewer;

    private ComboViewer shippingMethodViewer;

    protected DispatchSpecimenListInfoTable specimensNonProcessedTable;

    private DispatchSpecimensTreeTable specimensTreeTable;

    private ShipmentInfoWrapper shipmentInfo = null;

    private BgcEntryFormWidgetListener listener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };

    private CommentCollectionInfoTable commentEntryTable;

    private BgcBaseText commentWidget;

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
        form.setText(Messages.DispatchSendingEntryForm_form_title);
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
                Messages.DispatchSendingEntryForm_shipMethod_label,
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
                Messages.DispatchSendingEntryForm_waybill_label, null,
                shipmentInfo, ShipmentInfoPeer.WAYBILL.getName(), null);
        }

        createCommentSection();

        createSpecimensSelectionSection();
    }

    private void createCommentSection() {
        Composite client = createSectionWithClient(Messages.Comments_title);
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable = new CommentCollectionInfoTable(client,
            dispatch.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        commentWidget =
            (BgcBaseText) createBoundWidgetWithLabel(client, BgcBaseText.class,
                SWT.MULTI,
                Messages.Comments_add, null, comment, "message", null);

    }

    private void createReceiverCombo(Composite client) {
        if (dispatch.isInTransitState()) {
            BgcBaseText receiverLabel = createReadOnlyLabelledField(client,
                SWT.NONE, Messages.DispatchSendingEntryForm_receiver_label);
            setTextValue(receiverLabel, dispatch.getReceiverCenter()
                .getNameShort());
        } else {
            try {
                destSiteComboViewer = createComboViewer(client,
                    Messages.DispatchSendingEntryForm_receiver_label,
                    CenterWrapper.getOtherCenters(SessionManager
                        .getAppService(), SessionManager.getUser()
                        .getCurrentWorkingCenter()),
                    dispatch.getReceiverCenter(),
                    Messages.DispatchSendingEntryForm_receiver_validation_msg,
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
                        Messages.DispatchSendingEntryForm_error_title,
                        Messages.DispatchSendingEntryForm_retrieve_centers_error_msg);
            }
        }
    }

    private void createSpecimensSelectionSection() {
        if (dispatch.isInCreationState()) {
            Section section =
                createSection(Messages.DispatchSendingEntryForm_specimens_title);
            Composite composite = toolkit.createComposite(section);
            composite.setLayout(new GridLayout(1, false));
            section.setClient(composite);
            if (dispatch.isInCreationState()) {
                addSectionToolbar(section,
                    Messages.DispatchSendingEntryForm_specimens_description,
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
            specimensTreeTable = new DispatchSpecimensTreeTable(page, dispatch,
                !dispatch.isInClosedState() && !dispatch.isInLostState(), true);
            specimensTreeTable.addSelectionChangedListener(biobankListener);
            specimensTreeTable.addClickListener();
        }
    }

    protected void createSpecimensNonProcessedSection(boolean edit) {
        String title = Messages.DispatchSendingEntryForm_specimens_title;
        if (dispatch.isInCreationState()) {
            title = Messages.DispatchSendingEntryForm_added_specimens_title;
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
        specimensNonProcessedTable.createDefaultEditItem();
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
                SpecimenWrapper specimen = new SpecimenWrapper(
                    SessionManager.getAppService());
                specimen.getWrappedObject()
                    .setId(res.getCell().getSpecimenId());
                specimen.reload();
                dispatch.addSpecimens(Arrays.asList(specimen),
                    DispatchSpecimenState.NONE);
                reloadSpecimens();
                setDirty(true);
                break;
            case ERROR:
                BgcPlugin.openAsyncError(
                    Messages.DispatchSendingEntryForm_invalid_spec_error_title,
                    res.getCell().getInformation());
                break;
            }
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.DispatchSendingEntryForm_error_title,
                Messages.DispatchSendingEntryForm_adding_error_msg, e);
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
            return Messages.DispatchSendingEntryForm_new_title;
        }
        Assert.isNotNull(dispatch, "Dispatch is null"); //$NON-NLS-1$
        String label = dispatch.getSenderCenter().getNameShort() + " -> " //$NON-NLS-1$
            + dispatch.getReceiverCenter().getNameShort();

        String packedAt = dispatch.getFormattedPackedAt();
        if (packedAt != null)
            label += " [" + packedAt + "]"; //$NON-NLS-1$ //$NON-NLS-2$
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

    @Override
    protected void checkEditAccess() {
        if (adapter != null
            && adapter.getId() != null
            && !SessionManager
                .isAllowed(SessionSecurityHelper.DISPATCH_SEND_KEY_DESC)) {
            BgcPlugin.openAccessDeniedErrorMessage();
            throw new RuntimeException(
                Messages.BiobankEntryForm_access_denied_error_msg);
        }
    }

}
