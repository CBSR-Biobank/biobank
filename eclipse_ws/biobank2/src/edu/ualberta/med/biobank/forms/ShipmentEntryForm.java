package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentReadInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShippingMethodInfo;
import edu.ualberta.med.biobank.common.action.shipment.OriginInfoSaveAction;
import edu.ualberta.med.biobank.common.action.shipment.ShipmentGetInfoAction;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.helpers.SiteQuery;
import edu.ualberta.med.biobank.common.wrappers.util.WrapperUtil;
import edu.ualberta.med.biobank.dialogs.SpecimenOriginSelectDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget.ItemAction;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.Event;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoException;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoListener;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentEntryForm extends BiobankEntryForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.ShipmentEntryForm"; //$NON-NLS-1$

    public static final String MSG_NEW_SHIPMENT_OK =
        Messages.ShipmentEntryForm_new_ship_ok_msg;

    public static final String MSG_SHIPMENT_OK =
        Messages.ShipmentEntryForm_edit_ship_ok_msg;

    private ComboViewer senderComboViewer;

    private ComboViewer receiverComboViewer;

    private ComboViewer shippingMethodComboViewer;

    private SpecimenEntryWidget specimenEntryWidget;

    private Label waybillLabel;

    private NonEmptyStringValidator waybillValidator;

    private static final String WAYBILL_BINDING = "shipment-waybill-binding"; //$NON-NLS-1$

    private static final String DATE_SHIPPED_BINDING =
        "shipment-date-shipped-binding"; //$NON-NLS-1$

    private static final String BOX_NUMBER_BINDING = "box-number-binding"; //$NON-NLS-1$

    private DateTimeWidget dateSentWidget;

    private Label departedLabel;

    private NotNullValidator departedValidator;

    private BgcBaseText waybillWidget;

    private Set<SpecimenWrapper> removedSpecimensToPersist =
        new HashSet<SpecimenWrapper>();

    private BgcBaseText boxNumberWidget;

    private Label boxLabel;

    @SuppressWarnings("unused")
    private NonEmptyStringValidator boxValidator;

    protected boolean tryAgain;

    private CommentCollectionInfoTable commentEntryTable;

    private BgcEntryFormWidgetListener listener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };

    private OriginInfo oiCopy;

    private ShipmentReadInfo oiInfo;

    private OriginInfoWrapper originInfo;
    private ShipmentInfoWrapper shipmentInfo;

    private BgcBaseText commentWidget;
    private CommentWrapper comment;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ShipmentAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        comment = new CommentWrapper(SessionManager.getAppService());

        oiCopy = new OriginInfo();
        if (adapter.getId() != null) {
            oiInfo =
                SessionManager.getAppService().doAction(
                    new ShipmentGetInfoAction(adapter.getId()));
            copyShipment();
        }

        originInfo =
            new OriginInfoWrapper(SessionManager.getAppService(), oiCopy);
        if (oiCopy.getShipmentInfo() == null)
            shipmentInfo =
                new ShipmentInfoWrapper(SessionManager.getAppService());
        else
            shipmentInfo =
                new ShipmentInfoWrapper(SessionManager.getAppService(),
                    oiCopy.getShipmentInfo());

        setDefaultValues();

        String tabName;
        if (oiInfo == null) {
            tabName = Messages.ShipmentEntryForm_title_new;
        } else {
            tabName =
                NLS.bind(Messages.ShipmentEntryForm_title_edit, originInfo
                    .getShipmentInfo().getFormattedDateReceived());
        }
        setPartName(tabName);
    }

    public void copyShipment() {
        if (oiInfo == null) {
            oiCopy.setCenter(null);
            oiCopy.setReceiverSite(null);
            oiCopy.setShipmentInfo(null);
            oiCopy.setSpecimenCollection(new HashSet<Specimen>());
        } else {
            oiCopy.setId(oiInfo.oi.getId());
            oiCopy.setCenter(oiInfo.oi.getCenter());
            oiCopy.setReceiverSite(oiInfo.oi.getReceiverSite());
            oiCopy.setShipmentInfo(oiInfo.oi.getShipmentInfo());
            oiCopy.setSpecimenCollection(new HashSet<Specimen>(oiInfo.specimens));
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.ShipmentEntryForm_form_title);
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));
        createMainSection();
        createSpecimensSection();
    }

    private void createMainSection() throws Exception, ApplicationException {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        senderComboViewer =
            createComboViewer(client, Messages.ShipmentEntryForm_sender_label,
                ClinicWrapper.getAllClinics(SessionManager.getAppService()),
                (ClinicWrapper) originInfo.getCenter(),
                Messages.ShipmentEntryForm_sender_validation_msg,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        originInfo.setCenter((CenterWrapper<?>) selectedObject);
                        activateWidgets(((ClinicWrapper) selectedObject)
                            .getSendsShipments());
                    }
                });
        setFirstControl(senderComboViewer.getControl());

        receiverComboViewer =
            createComboViewer(client,
                Messages.ShipmentEntryForm_receiver_label,
                SiteQuery.getSites(SessionManager.getAppService()),
                originInfo.getReceiverSite(),
                Messages.ShipmentEntryForm_receiver_validation_msg,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        originInfo
                            .setReceiverSite((SiteWrapper) selectedObject);
                    }
                });

        waybillLabel =
            widgetCreator.createLabel(client,
                Messages.ShipmentEntryForm_waybill_label);
        waybillLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        waybillValidator =
            new NonEmptyStringValidator(
                Messages.ShipmentEntryForm_waybill_validation_msg);
        waybillWidget =
            (BgcBaseText) createBoundWidget(client, BgcBaseText.class,
                SWT.NONE, waybillLabel, new String[0], shipmentInfo,
                ShipmentInfoPeer.WAYBILL.getName(), waybillValidator,
                WAYBILL_BINDING);

        shippingMethodComboViewer =
            createComboViewer(client,
                Messages.ShipmentEntryForm_shipMethod_label,
                ShippingMethodWrapper.getShippingMethods(SessionManager
                    .getAppService()), shipmentInfo.getShippingMethod(), null,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        ShippingMethodWrapper method =
                            (ShippingMethodWrapper) selectedObject;
                        shipmentInfo.setShippingMethod(method);
                        if (dateSentWidget != null && method != null) {
                            activateDepartedWidget(method.needDate());
                        }
                    }
                });

        departedLabel =
            widgetCreator.createLabel(client,
                Messages.ShipmentEntryForm_packed_label);
        departedLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        departedValidator =
            new NotNullValidator(
                Messages.ShipmentEntryForm_packed_validation_msg);

        dateSentWidget =
            createDateTimeWidget(client, departedLabel,
                shipmentInfo.getPackedAt(), shipmentInfo,
                ShipmentInfoPeer.PACKED_AT.getName(), departedValidator,
                SWT.DATE | SWT.TIME, DATE_SHIPPED_BINDING);
        activateDepartedWidget(shipmentInfo.getShippingMethod() != null
            && shipmentInfo.getShippingMethod().needDate());

        boxLabel =
            widgetCreator.createLabel(client,
                Messages.ShipmentEntryForm_boxNber_label);
        boxLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        boxNumberWidget =
            (BgcBaseText) createBoundWidget(client, BgcBaseText.class,
                SWT.NONE, waybillLabel, new String[0], shipmentInfo,
                ShipmentInfoPeer.BOX_NUMBER.getName(), null, BOX_NUMBER_BINDING);

        ClinicWrapper clinic = (ClinicWrapper) originInfo.getCenter();
        if (clinic != null) {
            activateWidgets(clinic.getSendsShipments());
        }

        createDateTimeWidget(client, Messages.ShipmentEntryForm_received_label,
            shipmentInfo.getReceivedAt(), shipmentInfo,
            ShipmentInfoPeer.RECEIVED_AT.getName(), new NotNullValidator(
                Messages.ShipmentEntryForm_received_validation_msg));

        createCommentSection();

    }

    protected void activateWidgets(boolean sendsShipments) {
        if (waybillLabel != null && !waybillLabel.isDisposed()) {
            waybillLabel.setVisible(sendsShipments);
            ((GridData) waybillLabel.getLayoutData()).exclude = !sendsShipments;
        }
        if (waybillWidget != null && !waybillWidget.isDisposed()) {
            waybillWidget.setVisible(sendsShipments);
            ((GridData) waybillWidget.getLayoutData()).exclude =
                !sendsShipments;

            if (sendsShipments) {
                widgetCreator.addBinding(WAYBILL_BINDING);
            } else {
                widgetCreator.removeBinding(WAYBILL_BINDING);
                waybillWidget.setText(""); //$NON-NLS-1$
            }
        }

        boxNumberWidget.setVisible(sendsShipments);
        ((GridData) boxNumberWidget.getLayoutData()).exclude = !sendsShipments;
        boxLabel.setVisible(sendsShipments);
        ((GridData) boxLabel.getLayoutData()).exclude = !sendsShipments;
        if (sendsShipments) {
            widgetCreator.addBinding(BOX_NUMBER_BINDING);
        } else {
            widgetCreator.removeBinding(BOX_NUMBER_BINDING);
            boxNumberWidget.setText(""); //$NON-NLS-1$
        }
        form.layout(true, true);
    }

    protected void activateDepartedWidget(boolean departedNeeded) {
        dateSentWidget.setVisible(departedNeeded);
        ((GridData) dateSentWidget.getLayoutData()).exclude = !departedNeeded;
        departedLabel.setVisible(departedNeeded);
        ((GridData) departedLabel.getLayoutData()).exclude = !departedNeeded;
        if (departedNeeded) {
            widgetCreator.addBinding(DATE_SHIPPED_BINDING);
        } else {
            widgetCreator.removeBinding(DATE_SHIPPED_BINDING);
        }
        form.layout(true, true);

    }

    private void createSpecimensSection() {
        Composite client =
            createSectionWithClient(Messages.ShipmentEntryForm_specimens_title);
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL, GridData.FILL));
        toolkit.paintBordersFor(client);

        List<SpecimenWrapper> specimens =
            originInfo.getSpecimenCollection(true);

        specimenEntryWidget =
            new SpecimenEntryWidget(client, SWT.NONE, toolkit,
                SessionManager.getAppService(), true);
        specimenEntryWidget
            .addSelectionChangedListener(new BgcEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    setDirty(true);
                }
            });
        specimenEntryWidget
            .addDoubleClickListener(collectionDoubleClickListener);
        specimenEntryWidget.addBinding(widgetCreator,
            Messages.ShipmentEntryForm_specimens_validation_msg);

        VetoListener<ItemAction, SpecimenWrapper> vetoListener =
            new VetoListener<ItemAction, SpecimenWrapper>() {
                @Override
                public void handleEvent(Event<ItemAction, SpecimenWrapper> event)
                    throws VetoException {
                    SpecimenWrapper specimen = event.getObject();
                    switch (event.getType()) {
                    case PRE_ADD:
                        if (specimen == null)
                            throw new VetoException(
                                Messages.ShipmentEntryForm_notfound_error_msg);
                        if (!SessionManager.getUser().getCurrentWorkingCenter()
                            .equals(specimen.getCurrentCenter()))
                            throw new VetoException(
                                NLS.bind(
                                    Messages.ShipmentEntryForm_other_center_error_msg,
                                    specimen.getInventoryId(), specimen
                                        .getCurrentCenter().getNameShort()));
                        if (specimen.isUsedInDispatch())
                            throw new VetoException(
                                Messages.ShipmentEntryForm_dispatched_specimen_error_msg);
                        if (specimen.getParentContainer() != null)
                            throw new VetoException(
                                Messages.ShipmentEntryForm_stored_error_msg);
                        if (specimen.getOriginInfo() != null
                            && specimen.getOriginInfo().getShipmentInfo() != null
                            && !specimen.getOriginInfo().getShipmentInfo()
                                .equals(shipmentInfo))
                            throw new VetoException(
                                NLS.bind(
                                    Messages.ShipmentEntryForm_inAnother_ship_error_msg,
                                    specimen.getOriginInfo().getShipmentInfo()));
                        break;
                    case POST_ADD:
                        originInfo.addToSpecimenCollection(Arrays
                            .asList(specimen));
                        specimen.setOriginInfo(originInfo);
                        break;
                    case PRE_DELETE:
                        if (!originInfo.isNew()) {
                            try {
                                List<CenterWrapper<?>> centers =
                                    CenterWrapper.getCenters(specimen
                                        .getAppService());
                                SpecimenOriginSelectDialog dlg =
                                    new SpecimenOriginSelectDialog(
                                        form.getShell(), specimen, centers);

                                if (dlg.open() == Window.OK) {
                                    removedSpecimensToPersist.add(specimen);
                                } else {
                                    throw new VetoException(
                                        Messages.ShipmentEntryForm_center_select_msg);
                                }
                            } catch (ApplicationException e) {
                                throw new VetoException(e.getMessage());
                            }

                        }
                        break;
                    case POST_DELETE:
                        originInfo.removeFromSpecimenCollection(Arrays
                            .asList(specimen));
                        break;
                    }
                }
            };

        specimenEntryWidget.addVetoListener(ItemAction.PRE_ADD, vetoListener);
        specimenEntryWidget.addVetoListener(ItemAction.POST_ADD, vetoListener);
        specimenEntryWidget
            .addVetoListener(ItemAction.PRE_DELETE, vetoListener);
        specimenEntryWidget.addVetoListener(ItemAction.POST_DELETE,
            vetoListener);

        specimenEntryWidget.setSpecimens(specimens);
    }

    private void createCommentSection() {
        Composite client = createSectionWithClient(Messages.Comments_title);
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable =
            new CommentCollectionInfoTable(client,
                originInfo.getCommentCollection(false));
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

    @Override
    public String getNextOpenedFormID() {
        return ShipmentViewForm.ID;
    }

    @Override
    protected String getOkMessage() {
        return (originInfo.isNew()) ? MSG_NEW_SHIPMENT_OK : MSG_SHIPMENT_OK;
    }

    @Override
    protected void saveForm() throws Exception {

        Set<Integer> addedSpecimenIds =
            WrapperUtil.getCollectionIds(specimenEntryWidget
                .getAddedSpecimens());
        Set<Integer> removedSpecimenIds =
            WrapperUtil.getCollectionIds(specimenEntryWidget
                .getRemovedSpecimens());

        ShippingMethodInfo methodInfo =
            new ShippingMethodInfo(shipmentInfo.getShippingMethod().getId());
        OriginInfoSaveInfo oiInfo =
            new OriginInfoSaveInfo(originInfo.getId(), originInfo
                .getReceiverSite().getId(), originInfo.getCenter().getId(),
                comment.getMessage() == null ? ""
                    : comment.getMessage(), addedSpecimenIds,
                removedSpecimenIds);
        ShipmentInfoSaveInfo siInfo =
            new ShipmentInfoSaveInfo(shipmentInfo.getId(),
                shipmentInfo.getBoxNumber(), shipmentInfo.getPackedAt(),
                shipmentInfo.getReceivedAt(), shipmentInfo.getWaybill(),
                methodInfo);
        OriginInfoSaveAction save = new OriginInfoSaveAction(oiInfo, siInfo);
        SessionManager.getAppService().doAction(save);

        /*
         * if (shipmentInfo.getWaybill() != null &&
         * shipmentInfo.getWaybill().isEmpty()) { shipmentInfo.setWaybill(null);
         * } try { originInfo.setShipmentInfo(shipmentInfo);
         * originInfo.persist(); } catch (ModificationConcurrencyException mc) {
         * if (isTryingAgain) { // already tried once throw mc; }
         * 
         * Display.getDefault().syncExec(new Runnable() {
         * 
         * @Override public void run() { tryAgain = BgcPlugin.openConfirm(
         * Messages.ShipmentEntryForm_concurrency_title,
         * Messages.ShipmentEntryForm_concurrency_msg); setDirty(true); try {
         * doTrySettingAgain(); tryAgain = true; } catch (Exception e) {
         * saveErrorCatch(e, null, true); } } }); }
         * 
         * if (!tryAgain) // persist those specimens only once we are sure the
         * shipment // persist has succeeded. for (SpecimenWrapper s :
         * removedSpecimensToPersist) { // when remove a specimen, ask for the
         * origin center. Then // create a new origin info with this center and
         * the deleted // specimen: OriginInfoWrapper origin =
         * s.getOriginInfo(); origin.persist(); // then we set back the originfo
         * to the specimen to be sure it // has the right modelObject:
         * s.setOriginInfo(origin); // then we save the specimen s.persist(); }
         */
    }

    /*
     * protected void doTrySettingAgain() throws Exception { // remove added
     * specimens and add removed specimens and try to // add/remove them again
     * (after reloading them) through the // SpecimenEntryWidget to check again
     * if can perform the action
     * 
     * List<SpecimenWrapper> addedSpecimens =
     * specimenEntryWidget.getAddedSpecimens();
     * 
     * List<SpecimenWrapper> removedSpecimens =
     * specimenEntryWidget.getRemovedSpecimens(); List<SpecimenWrapper>
     * pEventSpecs = originInfo.getSpecimenCollection(false);
     * pEventSpecs.removeAll(addedSpecimens);
     * pEventSpecs.addAll(removedSpecimens); for (SpecimenWrapper sp :
     * pEventSpecs) { sp.reload(); }
     * originInfo.setSpecimenWrapperCollection(pEventSpecs);
     * specimenEntryWidget.setSpecimens(pEventSpecs);
     * 
     * Map<String, String> problems = new HashMap<String, String>(); for
     * (SpecimenWrapper spec : addedSpecimens) { String inventoryId =
     * spec.getInventoryId(); try { spec.reload();
     * specimenEntryWidget.addSpecimen(spec); } catch (Exception ex) {
     * problems.put(Messages.ShipmentEntryForm_adding_label + " " + inventoryId,
     * ex.getMessage()); //$NON-NLS-1$ } } for (SpecimenWrapper spec :
     * removedSpecimens) { String inventoryId = spec.getInventoryId(); try {
     * spec.reload(); specimenEntryWidget.removeSpecimen(spec); } catch
     * (Exception ex) { problems.put(Messages.ShipmentEntryForm_removing_label +
     * " " + inventoryId, ex.getMessage()); //$NON-NLS-1$ } } if
     * (problems.size() != 0) { StringBuffer msg = new StringBuffer(); for
     * (Entry<String, String> entry : problems.entrySet()) { if (msg.length() >
     * 0) msg.append("\n"); //$NON-NLS-1$
     * msg.append(entry.getKey()).append(": ") //$NON-NLS-1$
     * .append(entry.getValue()); } throw new BiobankException(
     * Messages.ShipmentEntryForm_tryAgain_error_msg + msg.toString()); } }
     */

    @Override
    protected void doAfterSave() throws Exception {
        if (tryAgain) {
            tryAgain = false;
            confirm();
        } else {
            SpecimenTransitView.reloadCurrent();
            if (!shipmentInfo.isReceivedToday())
                SpecimenTransitView.showShipment(originInfo);
        }
    }

    @Override
    protected void onReset() throws Exception {
        init();

        // do not change origin if form reset
        removedSpecimensToPersist.clear();

        originInfo.setShipmentInfo(shipmentInfo);

        specimenEntryWidget.setSpecimens(originInfo
            .getSpecimenCollection(false));

        setDefaultValues();
        GuiUtil.reset(senderComboViewer, originInfo.getCenter());
        GuiUtil.reset(receiverComboViewer, originInfo.getReceiverSite());
        GuiUtil.reset(shippingMethodComboViewer,
            shipmentInfo.getShippingMethod());
    }

    private void setDefaultValues() {
        if (originInfo.isNew()) {
            CenterWrapper<?> userCenter =
                SessionManager.getUser().getCurrentWorkingCenter();
            if (userCenter instanceof SiteWrapper) {
                originInfo.setReceiverSite((SiteWrapper) userCenter);
            }
            Date receivedAt = Calendar.getInstance().getTime();
            shipmentInfo.setReceivedAt(receivedAt);
        }
    }
}
