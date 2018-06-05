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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetAllAction;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentReadInfo;
import edu.ualberta.med.biobank.common.action.originInfo.OriginInfoSaveAction;
import edu.ualberta.med.biobank.common.action.shipment.ShipmentGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.SpecimenOriginSelectDialog;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget.ItemAction;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.Event;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoException;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoListener;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentEntryForm extends BiobankEntryForm {
    private static final I18n i18n = I18nFactory
        .getI18n(ShipmentEntryForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ShipmentEntryForm";

    @SuppressWarnings("nls")
    public static final String MSG_NEW_SHIPMENT_OK =
        "Creating a new shipment record.";

    @SuppressWarnings("nls")
    public static final String MSG_SHIPMENT_OK =
        "Editing an existing shipment record.";

    private ComboViewer senderComboViewer;

    private ComboViewer receiverComboViewer;

    private ComboViewer shippingMethodComboViewer;

    private SpecimenEntryWidget specimenEntryWidget;

    private Label waybillLabel;

    private NonEmptyStringValidator waybillValidator;

    @SuppressWarnings("nls")
    private static final String WAYBILL_BINDING = "shipment-waybill-binding";

    @SuppressWarnings("nls")
    private static final String DATE_SHIPPED_BINDING =
        "shipment-date-shipped-binding";

    @SuppressWarnings("nls")
    private static final String BOX_NUMBER_BINDING = "box-number-binding";

    private DateTimeWidget dateSentWidget;

    private Label departedLabel;

    private NotNullValidator departedValidator;

    private BgcBaseText waybillWidget;

    private final Set<SpecimenWrapper> removedSpecimensToPersist =
        new HashSet<SpecimenWrapper>();

    private BgcBaseText boxNumberWidget;

    private Label boxLabel;

    @SuppressWarnings("unused")
    private NonEmptyStringValidator boxValidator;

    protected boolean tryAgain;

    private CommentsInfoTable commentEntryTable;

    private ShipmentReadInfo oiInfo;

    private final OriginInfoWrapper originInfo = new OriginInfoWrapper(
        SessionManager.getAppService());
    private final ShipmentInfoWrapper shipmentInfo = new ShipmentInfoWrapper(
        SessionManager.getAppService());

    private final CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    private List<SpecimenInfo> specimens;

    @SuppressWarnings("nls")
    @Override
    protected void init() throws Exception {
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
        Assert.isTrue(adapter instanceof ShipmentAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        setOiInfo(adapter.getId());

        setDefaultValues();

        String tabName;
        if (oiInfo == null) {
            tabName = i18n.tr("New Shipment");
        } else {
            tabName =
                i18n.tr("Shipment {0}", originInfo
                    .getShipmentInfo().getFormattedDateReceived());
        }
        setPartName(tabName);
    }

    private void setOiInfo(Integer id) throws ApplicationException {
        if (id == null) {
            OriginInfo oi = new OriginInfo();
            oi.setShipmentInfo(new ShipmentInfo());
            originInfo.setWrappedObject(oi);
            shipmentInfo.setWrappedObject(oi.getShipmentInfo());
            specimens = new ArrayList<SpecimenInfo>();
        } else {
            ShipmentReadInfo read =
                SessionManager.getAppService().doAction(
                    new ShipmentGetInfoAction(id));
            originInfo.setWrappedObject(read.originInfo);
            shipmentInfo.setWrappedObject(read.originInfo.getShipmentInfo());
            specimens = read.specimens;
            SessionManager.logLookup(read.originInfo);
        }
        comment.setWrappedObject(new Comment());
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(ShipmentInfo.NAME.singular().toString());
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));
        createMainSection();
        createSpecimensSection();
    }

    @SuppressWarnings("nls")
    private void createMainSection() throws Exception, ApplicationException {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        List<Clinic> clinics = SessionManager.getAppService().doAction(
            new ClinicGetAllAction()).getList();

        senderComboViewer = createComboViewer(client,
            Dispatch.PropertyName.SENDER_CENTER.toString(),
            ModelWrapper.wrapModelCollection(
                SessionManager.getAppService(), clinics, ClinicWrapper.class),
            (ClinicWrapper) originInfo.getCenter(),
            // validation error message.
            i18n.tr("A sender center should be selected"),
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
                Dispatch.PropertyName.RECEIVER_CENTER.toString(),
                Arrays.asList(SessionManager.getUser()
                    .getCurrentWorkingCenter()),
                originInfo.getReceiverCenter(),
                // validation error message.
                i18n.tr("A receiving site should be selected"),
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        originInfo
                            .setReceiverCenter((CenterWrapper<?>) selectedObject);
                    }
                });

        waybillLabel =
            widgetCreator.createLabel(client,
                ShipmentInfo.PropertyName.WAYBILL.toString());
        waybillLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        waybillValidator =
            new NonEmptyStringValidator(
                // validation error message.
                i18n.tr("A waybill should be set"));
        waybillWidget =
            (BgcBaseText) createBoundWidget(client, BgcBaseText.class,
                SWT.NONE, waybillLabel, new String[0],
                shipmentInfo,
                ShipmentInfoPeer.WAYBILL.getName(), waybillValidator,
                WAYBILL_BINDING);

        shippingMethodComboViewer =
            createComboViewer(client,
                ShippingMethod.NAME.singular().toString(),
                ShippingMethodWrapper.getShippingMethods(SessionManager
                    .getAppService()), shipmentInfo
                    .getShippingMethod(), null,
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
                i18n.tr("Packed"));
        departedLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        departedValidator =
            new NotNullValidator(
                // validation error message.
                i18n.tr("Date Packed should be set"));

        dateSentWidget = createDateTimeWidget(client, departedLabel,
            shipmentInfo.getPackedAt(),
            shipmentInfo,
            ShipmentInfoPeer.PACKED_AT.getName(), departedValidator,
            SWT.DATE | SWT.TIME, DATE_SHIPPED_BINDING);
        activateDepartedWidget(shipmentInfo.getShippingMethod() != null
            && shipmentInfo.getShippingMethod().needDate());

        boxLabel =
            widgetCreator.createLabel(client,
                i18n.tr("Box Number"));
        boxLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        boxNumberWidget =
            (BgcBaseText) createBoundWidget(client, BgcBaseText.class,
                SWT.NONE, waybillLabel, new String[0],
                shipmentInfo,
                ShipmentInfoPeer.BOX_NUMBER.getName(), null, BOX_NUMBER_BINDING);

        ClinicWrapper clinic = (ClinicWrapper) originInfo.getCenter();
        if (clinic != null) {
            activateWidgets(clinic.getSendsShipments());
        }

        createDateTimeWidget(client, i18n.tr("Received"),
            shipmentInfo.getReceivedAt(),
            shipmentInfo,
            ShipmentInfoPeer.RECEIVED_AT.getName(), new NotNullValidator(
                i18n.tr("Date Received should be set")));

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
                waybillWidget.setText(StringUtil.EMPTY_STRING);
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
            boxNumberWidget.setText(StringUtil.EMPTY_STRING);
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
            shipmentInfo.setPackedAt(null);
        }
        form.layout(true, true);
    }

    @SuppressWarnings("nls")
    private void createSpecimensSection() {
        Composite client =
            createSectionWithClient(Specimen.NAME.plural().toString());
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL, GridData.FILL));
        toolkit.paintBordersFor(client);

        specimenEntryWidget =
            new SpecimenEntryWidget(client, SWT.NONE, toolkit, true);
        specimenEntryWidget
            .addSelectionChangedListener(new BgcEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    setDirty(true);
                }
            });
        specimenEntryWidget
            .addDoubleClickListener(new IInfoTableDoubleClickItemListener<SpecimenInfo>() {
                @Override
                public void doubleClick(InfoTableEvent<SpecimenInfo> event) {
                    Specimen s =
                        ((SpecimenInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).specimen;
                    AdapterBase.openForm(
                        new FormInput(
                            new SpecimenAdapter(null,
                                new SpecimenWrapper(SessionManager
                                    .getAppService(), s))),
                        SpecimenViewForm.ID);
                }
            });

        specimenEntryWidget.addBinding(widgetCreator,
            i18n.tr("Specimens should be added to a shipment"));

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
                                // exception message.
                                i18n.tr("No specimen found for that inventory id."));
                        if (!SessionManager.getUser().getCurrentWorkingCenter()
                            .equals(specimen.getCurrentCenter()))
                            throw new VetoException(
                                // exception message.
                                i18n.tr(
                                    "Specimen ''{0}'' is currently in center ''{1}''. You can create shipments only for specimens that are currently in you center.",
                                    specimen.getInventoryId(), specimen
                                        .getCurrentCenter().getNameShort()));
                        if (specimen.isUsedInDispatch())
                            throw new VetoException(
                                // exception message.
                                i18n.tr("Specimen is currently listed in a dispatch."));
                        if (specimen.getParentContainer() != null)
                            throw new VetoException(
                                // exception message.
                                i18n.tr("Specimen is currently listed as stored in a container."));
                        if (specimen.getOriginInfo() != null
                            && specimen.getOriginInfo().getShipmentInfo() != null
                            && !specimen.getOriginInfo().getShipmentInfo()
                                .equals(shipmentInfo))
                            throw new VetoException(
                                // exception message.
                                i18n.tr(
                                    "Specimen is currently part of another shipment: {0}. You must remove this specimen from that shipment before it can be added to this one.",
                                    specimen.getOriginInfo().getShipmentInfo()));
                        break;
                    case POST_ADD:
                        // action performs this now
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
                                        // exception message.
                                        i18n.tr(
                                            "Must select a new center for this specimen to originate from."));
                                }
                            } catch (ApplicationException e) {
                                throw new VetoException(e.getMessage());
                            }

                        }
                        break;
                    case POST_DELETE:
                        specimens.remove(Arrays
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

    @SuppressWarnings("nls")
    private void createCommentSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable =
            new CommentsInfoTable(client,
                originInfo.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createBoundWidgetWithLabel(client, BgcBaseText.class,
            SWT.MULTI, i18n.tr("Add a comment"), null, comment, "message", null);

    }

    @Override
    public String getNextOpenedFormId() {
        return ShipmentViewForm.ID;
    }

    @Override
    protected String getOkMessage() {
        return (originInfo.isNew()) ? MSG_NEW_SHIPMENT_OK : MSG_SHIPMENT_OK;
    }

    @Override
    protected void saveForm() throws Exception {

        Set<Integer> addedSpecimenIds = new HashSet<Integer>();
        for (SpecimenInfo info : specimenEntryWidget.getAddedSpecimens()) {
            addedSpecimenIds.add(info.specimen.getId());
        }
        Set<Integer> removedSpecimenIds = new HashSet<Integer>();
        for (SpecimenInfo info : specimenEntryWidget.getRemovedSpecimens()) {
            removedSpecimenIds.add(info.specimen.getId());
        }

        OriginInfoSaveInfo oiInfo = new OriginInfoSaveInfo(
            originInfo.getId(),
            originInfo.getReceiverCenter().getId(),
            originInfo.getCenter().getId(),
            comment.getMessage() == null ? StringUtil.EMPTY_STRING : comment.getMessage(),
            addedSpecimenIds,
            removedSpecimenIds);

        ShipmentInfoSaveInfo siInfo = new ShipmentInfoSaveInfo(
            shipmentInfo.getId(),
            shipmentInfo.getBoxNumber(),
            originInfo.getShipmentInfo().getPackedAt(),
            shipmentInfo.getReceivedAt(),
            originInfo.getShipmentInfo().getWaybill(),
            shipmentInfo.getShippingMethod().getId());

        OriginInfoSaveAction save = new OriginInfoSaveAction(oiInfo, siInfo);
        originInfo.setId(SessionManager.getAppService().doAction(save).getId());
        ((AdapterBase) adapter).setModelObject(originInfo);
    }

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
    public void setValues() throws Exception {
        // do not change origin if form reset
        removedSpecimensToPersist.clear();

        originInfo.setShipmentInfo(shipmentInfo);

        specimenEntryWidget.setSpecimens(specimens);

        setDefaultValues();
        GuiUtil.reset(senderComboViewer, originInfo.getCenter());
        GuiUtil.reset(receiverComboViewer, originInfo.getReceiverCenter());
        GuiUtil.reset(shippingMethodComboViewer,
            shipmentInfo.getShippingMethod());
    }

    private void setDefaultValues() {
        if (originInfo.isNew()) {
            CenterWrapper<?> userCenter =
                SessionManager.getUser().getCurrentWorkingCenter();
            if (userCenter instanceof SiteWrapper) {
                originInfo.setReceiverCenter(userCenter);
            }
            Date receivedAt = Calendar.getInstance().getTime();
            shipmentInfo.setReceivedAt(receivedAt);
        }
    }
}
