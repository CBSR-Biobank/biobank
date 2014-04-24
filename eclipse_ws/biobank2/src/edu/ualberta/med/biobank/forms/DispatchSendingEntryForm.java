package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.DispatchCreateProcessAction;
import edu.ualberta.med.biobank.common.action.scanprocess.data.ShipmentProcessInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.action.search.SpecimenByMicroplateSearchAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.util.InventoryIdUtil;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.DispatchCreateScanDialog;
import edu.ualberta.med.biobank.dialogs.dispatch.SendDispatchDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.DispatchSpecimenListInfoTable;
import edu.ualberta.med.biobank.widgets.trees.DispatchSpecimensTreeTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchSendingEntryForm extends AbstractDispatchEntryForm {
    private static final I18n i18n = I18nFactory
        .getI18n(DispatchSendingEntryForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.DispatchSendingEntryForm";

    @SuppressWarnings("nls")
    public static final String MSG_NEW_DISPATCH_OK =
        "Creating a new dispatch record.";

    @SuppressWarnings("nls")
    public static final String MSG_DISPATCH_OK =
        "Editing an existing dispatch record.";

    private ComboViewer destSiteComboViewer;

    private ComboViewer shippingMethodViewer;

    protected DispatchSpecimenListInfoTable specimensNonProcessedTable;

    private DispatchSpecimensTreeTable specimensTreeTable;

    private ShipmentInfoWrapper shipmentInfo = null;

    private CommentsInfoTable commentEntryTable;

    private DispatchAdapter dispatchAdapter;

    @Override
    protected void init() throws Exception {
        super.init();

        dispatchAdapter = (DispatchAdapter) adapter;

        if (dispatch.isNew()) {
            Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
            dispatch.getWrappedObject().setSenderCenter(
                SessionManager.getUser().getCurrentWorkingCenter().getWrappedObject());
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

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(i18n.tr("Dispatch Information"));
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
            ShippingMethodWrapper selectedShippingMethod =
                dispatch.getShipmentInfo().getShippingMethod();
            shippingMethodViewer = widgetCreator.createComboViewer(
                client,
                ShippingMethod.NAME.singular().toString(),
                ShippingMethodWrapper.getShippingMethods(SessionManager.getAppService()),
                selectedShippingMethod,
                null,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        ShippingMethodWrapper shippingMethod = (ShippingMethodWrapper) selectedObject;
                        dispatch.getShipmentInfo().setShippingMethod(shippingMethod);
                    }
                },
                new BiobankLabelProvider());

            createBoundWidgetWithLabel(
                client,
                BgcBaseText.class,
                SWT.NONE,
                ShipmentInfo.PropertyName.WAYBILL.toString(),
                null,
                shipmentInfo,
                ShipmentInfoPeer.WAYBILL.getName(),
                null);

            createDateTimeWidget(
                client,
                i18n.tr("Departed"),
                null,
                shipmentInfo,
                ShipmentInfoPeer.PACKED_AT.getName(),
                null);
        }

        createCommentSection();

        createSpecimensSelectionSection();
    }

    @SuppressWarnings("nls")
    private void createCommentSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable = new CommentsInfoTable(client, dispatch.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createBoundWidgetWithLabel(
            client,
            BgcBaseText.class,
            SWT.MULTI,
            i18n.tr("Add a comment"),
            null,
            comment,
            "message",
            null);

    }

    @SuppressWarnings("nls")
    private void createReceiverCombo(Composite client) {
        if (dispatch.isInTransitState()) {
            BgcBaseText receiverLabel = createReadOnlyLabelledField(
                client, SWT.NONE, Dispatch.PropertyName.RECEIVER_CENTER.toString());
            setTextValue(receiverLabel, dispatch.getReceiverCenter().getNameShort());
        } else {
            Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
            try {
                destSiteComboViewer = createComboViewer(
                    client,
                    Dispatch.PropertyName.RECEIVER_CENTER.toString(),
                    CenterWrapper.getOtherCenters(
                        SessionManager.getAppService(),
                        SessionManager.getUser().getCurrentWorkingCenter()),
                    dispatch.getReceiverCenter(),
                    // TR: validation error message.
                    i18n.tr("Dispatch must have a receiver"),
                    new ComboSelectionUpdate() {
                        @Override
                        public void doSelection(Object selectedObject) {
                            dispatch.setReceiverCenter((CenterWrapper<?>) selectedObject);
                            setDirty(true);
                        }
                    });
            } catch (ApplicationException e) {
                BgcPlugin.openAsyncError(
                    // dialog message.
                    i18n.tr("Unable to retrieve Centers"));
            }
        }
    }

    @SuppressWarnings("nls")
    private void createSpecimensSelectionSection() {
        if (dispatch.isInCreationState()) {
            Section section = createSection(Specimen.NAME.plural().toString());
            Composite composite = toolkit.createComposite(section);
            composite.setLayout(new GridLayout(1, false));
            section.setClient(composite);
            if (dispatch.isInCreationState()) {
                addSectionToolbar(
                    section,
                    i18n.tr("Add specimens to this dispatch"),
                    new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            openScanDialog();
                        }
                    },
                    null,
                    BgcPlugin.Image.DISPATCH_SHIPMENT_ADD_SPECIMEN);

                createSpecimensSelectionActions(composite, false);
                createSpecimensNonProcessedSection(true);
            }
        } else {
            specimensTreeTable = new DispatchSpecimensTreeTable(
                page, dispatch, !dispatch.isInClosedState() && !dispatch.isInLostState());
            specimensTreeTable.addSelectionChangedListener(biobankListener);
            specimensTreeTable.addClickListener();
        }
    }

    @SuppressWarnings("nls")
    protected void createSpecimensNonProcessedSection(boolean edit) {
        String title = Specimen.NAME.plural().toString();
        if (dispatch.isInCreationState()) {
            title = i18n.tr("Added specimens");
        }
        Composite parent = createSectionWithClient(title);
        specimensNonProcessedTable = new DispatchSpecimenListInfoTable(parent, dispatch, edit) {
            @Override
            public List<DispatchSpecimenWrapper> getInternalDispatchSpecimens() {
                return dispatch.getDispatchSpecimenCollection(false);
            }
        };
        specimensNonProcessedTable.adaptToToolkit(toolkit, true);
        specimensNonProcessedTable.addClickListener(
            new IInfoTableDoubleClickItemListener<DispatchSpecimenWrapper>() {
                @Override
                public void doubleClick(
                    InfoTableEvent<DispatchSpecimenWrapper> event) {
                    Object selection = event.getSelection();
                    if (selection instanceof InfoTableSelection) {
                        InfoTableSelection tableSelection = (InfoTableSelection) selection;
                        DispatchSpecimenWrapper dsa = (DispatchSpecimenWrapper)
                            tableSelection.getObject();
                        if (dsa != null) {
                            SessionManager.openViewForm(dsa.getSpecimen());
                        }
                    }
                }
            });
        specimensNonProcessedTable.addEditItemListener(
            new IInfoTableEditItemListener<DispatchSpecimenWrapper>() {

                @Override
                public void editItem(
                    InfoTableEvent<DispatchSpecimenWrapper> event) {
                    Object selection = event.getSelection();
                    if (selection instanceof InfoTableSelection) {
                        InfoTableSelection tableSelection = (InfoTableSelection) selection;
                        DispatchSpecimenWrapper dsa = (DispatchSpecimenWrapper)
                            tableSelection.getObject();
                        if (dsa != null) {
                            new SpecimenAdapter(null, dsa.getSpecimen()).openEntryForm();
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
            dispatch,
            SessionManager.getUser().getCurrentWorkingCenter());
        dialog.open();
        setDirty(true); // FIXME add a boolean in the dialog to know if
                        // specimens were added
        reloadSpecimens();
    }

    @SuppressWarnings("nls")
    @Override
    protected void doSpecimenTextAction(String inventoryId) {
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
        try {
            CellProcessResult res = (CellProcessResult) SessionManager.getAppService().doAction(
                new DispatchCreateProcessAction(
                    new ShipmentProcessInfo(null, dispatch, true),
                    SessionManager.getUser().getCurrentWorkingCenter().getId(),
                    new CellInfo(-1, -1, inventoryId, null),
                    Locale.getDefault()));
            switch (res.getProcessStatus()) {
            case FILLED:
                // ok
                Specimen spec = SessionManager.getAppService().doAction(
                    new SpecimenGetInfoAction(res.getCell().getSpecimenId())).getSpecimen();
                dispatch.addSpecimens(Arrays.asList(
                    new SpecimenWrapper(SessionManager.getAppService(), spec)),
                    DispatchSpecimenState.NONE);
                reloadSpecimens();
                setDirty(true);
                break;
            case ERROR:
                BgcPlugin.openAsyncError(
                    i18n.tr("Invalid specimen"),
                    res.getCell().getInformation().toString());
                break;
            }
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                i18n.tr("Error adding the specimen"), e);
        }
    }

    @SuppressWarnings("nls")
    @Override
    protected void doMicroplateTextAction(String microplateId) {
        if (InventoryIdUtil.isFormatMicroplate(microplateId)) {
            try {
                ArrayList<String> ids = SessionManager.getAppService().doAction(
                    new SpecimenByMicroplateSearchAction(microplateId)).getList();
                if (ids.isEmpty()) {
                    BgcPlugin.openAsyncError(
                        i18n.tr("Microplate does not exist or has no specimens"));
                }
                else {
                    for (String id : ids) {
                        doSpecimenTextAction(id);
                    }
                }
            } catch (Exception e) {
                BgcPlugin.openAsyncError(
                    i18n.tr("Problem adding microplate specimens"), e);
            }
        }
        else {
            BgcPlugin.openAsyncError(
                i18n.tr("Microplate ID format not valid"));
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
            GuiUtil.reset(shippingMethodViewer, shipmentInfo.getShippingMethod());
        }

        GuiUtil.reset(destSiteComboViewer, dispatch.getReceiverCenter());
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTextForPartName() {
        if (dispatch.isNew()) {
            return i18n.tr("New Dispatch");
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

    @SuppressWarnings("nls")
    @Override
    protected void doAfterSave() throws Exception {
        super.doAfterSave();

        boolean userSelection = MessageDialog.openQuestion(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            // confirmation dialog title
            i18n.tr("Send the dispatch"),
            // confirmation dialog message
            i18n.tr("Do you wish to send the dispatch?"));

        if (userSelection) {
            sendDispatch(this, dispatch, dispatchAdapter);
        } else {
            BgcPlugin.openMessage(
                // dialog title.
                i18n.tr("Send later"),
                i18n.tr("To send the dispatch later you can open the dispatch view form and "
                    + "press the \"Send\" button in the top right of the form.\n\n"
                    + "You can also expand the \"Creation\" node on the tree at the left, and "
                    + "right click on your dispatch and select \"Send Dispatch\"."));
        }
    }

    @SuppressWarnings("nls")
    static void sendDispatch(
        final BiobankFormBase form,
        final DispatchWrapper dispatch,
        final DispatchAdapter dispatchAdapter) {

        if (form == null) {
            throw new IllegalArgumentException("form is null");
        }
        if (dispatch == null) {
            throw new IllegalArgumentException("dispatch is null");
        }
        if (dispatchAdapter == null) {
            throw new IllegalArgumentException("dispatchAdapter is null");
        }

        int result = new SendDispatchDialog(Display.getDefault().getActiveShell(), dispatch).open();
        if (result == Dialog.OK) {
            IRunnableContext context =
                new ProgressMonitorDialog(Display.getDefault().getActiveShell());
            try {
                context.run(true, true, new IRunnableWithProgress() {
                    @Override
                    public void run(final IProgressMonitor monitor) {
                        monitor.beginTask(
                            // progress message.
                            i18n.tr("Saving..."),
                            IProgressMonitor.UNKNOWN);
                        try {
                            dispatchAdapter.setModelObject(dispatch);
                            dispatchAdapter.doSend();
                        } catch (Exception ex) {
                            form.saveErrorCatch(ex, monitor, false);
                            return;
                        }
                        monitor.done();
                    }
                });
            } catch (Exception e1) {
                BgcPlugin.openAsyncError(
                    // dialog title.
                    i18n.tr("Save error"), e1);
            }
            SpecimenTransitView.getCurrent().reload();
            dispatchAdapter.openViewForm();
        }
    }
}
