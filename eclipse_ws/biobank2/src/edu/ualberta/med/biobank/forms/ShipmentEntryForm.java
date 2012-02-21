package edu.ualberta.med.biobank.forms;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentTempLoggerPeer;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentTempLoggerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.SpecimenOriginSelectDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget.ItemAction;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.Event;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoException;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoListener;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ShipmentEntryForm";

    public static final String MSG_NEW_SHIPMENT_OK = "Creating a new shipment record.";

    public static final String MSG_SHIPMENT_OK = "Editing an existing shipment record.";

    private ShipmentInfoWrapper shipmentInfo;

    private OriginInfoWrapper originInfo;

    private ComboViewer senderComboViewer;

    private ComboViewer receiverComboViewer;

    private ComboViewer shippingMethodComboViewer;

    private SpecimenEntryWidget specimenEntryWidget;

    private Label waybillLabel;

    private NonEmptyStringValidator waybillValidator;

    private static final String WAYBILL_BINDING = "shipment-waybill-binding";

    private static final String DATE_SHIPPED_BINDING = "shipment-date-shipped-binding";

    private DateTimeWidget dateSentWidget;

    private Label departedLabel;

    private NotNullValidator departedValidator;

    private BgcBaseText waybillWidget;

    private Set<SpecimenWrapper> specimensToPersist = new HashSet<SpecimenWrapper>();

    private Button radioTempPass;
    private Button radioTempFail;
    private Button uploadButton;
    private String path;

    private static final String HI_TEMP_BINDING = "hi-temp-binding";
    private static final String LOW_TEMP_BINDING = "low-temp-binding";
    private static final String ABOVE_MAX_TEMP_BINDING = "above-max-temp-binding";
    private static final String BELOW_MAX_TEMP_BINDING = "below-max-binding";

    private Label hiTempLabel;
    private Label lowTempLabel;
    private Label aboveMaxTempLabel;
    private Label belowMaxTempLabel;
    private Label tempResultLabel;
    private Label uploadtLabel;

    private BgcBaseText hiTempWidget;
    private BgcBaseText lowTempWidget;
    private BgcBaseText aboveMaxTempWidget;
    private BgcBaseText belowMaxTempWidget;
    private BgcBaseText deviceIDWidget;

    private Composite passFailComposite;
    private Composite uploadComposite;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ShipmentAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        originInfo = (OriginInfoWrapper) getModelObject();
        shipmentInfo = originInfo.getShipmentInfo();

        String tabName;
        if (originInfo.isNew()) {
            tabName = "New Shipment";
            CenterWrapper<?> userCenter = SessionManager.getUser()
                .getCurrentWorkingCenter();
            if (userCenter instanceof SiteWrapper) {
                originInfo.setReceiverSite((SiteWrapper) userCenter);
            }
            shipmentInfo.setReceivedAt(Calendar.getInstance().getTime());
        } else {
            tabName = "Shipment "
                + originInfo.getShipmentInfo().getFormattedDateReceived();
        }
        if (shipmentInfo.getShipmentTempLogger() == null) {
            ShipmentTempLoggerWrapper shipLogger = new ShipmentTempLoggerWrapper(
                SessionManager.getAppService());
            shipmentInfo.setShipmentTempLogger(shipLogger);
            shipmentInfo.getShipmentTempLogger().setShipmentInfo(shipmentInfo);
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Shipment Information");
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

        senderComboViewer = createComboViewer(client, "Sender",
            ClinicWrapper.getAllClinics(appService),
            (ClinicWrapper) originInfo.getCenter(),
            "A sender center should be selected", new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    originInfo.setCenter((CenterWrapper<?>) selectedObject);
                    activateWaybillWidget(((ClinicWrapper) selectedObject)
                        .getSendsShipments());
                }
            });
        setFirstControl(senderComboViewer.getControl());

        receiverComboViewer = createComboViewer(client, "Receiver",
            SiteWrapper.getSites(appService), originInfo.getReceiverSite(),
            "A receiving site should be selected", new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    originInfo.setReceiverSite((SiteWrapper) selectedObject);
                }
            });

        waybillLabel = widgetCreator.createLabel(client, "Waybill");
        waybillLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        waybillValidator = new NonEmptyStringValidator(
            "A waybill should be set");
        waybillWidget = (BgcBaseText) createBoundWidget(client,
            BgcBaseText.class, SWT.NONE, waybillLabel, new String[0],
            shipmentInfo, ShipmentInfoPeer.WAYBILL.getName(), waybillValidator,
            WAYBILL_BINDING);

        ClinicWrapper clinic = (ClinicWrapper) originInfo.getCenter();
        if (clinic != null) {
            activateWaybillWidget(clinic.getSendsShipments());
        }

        shippingMethodComboViewer = createComboViewer(client,
            "Shipping Method",
            ShippingMethodWrapper.getShippingMethods(appService), originInfo
                .getShipmentInfo().getShippingMethod(), null,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    ShippingMethodWrapper method = (ShippingMethodWrapper) selectedObject;
                    ShipmentInfoWrapper shipInfo = originInfo.getShipmentInfo();
                    shipInfo.setShippingMethod(method);
                    if (dateSentWidget != null && method != null) {
                        activateDepartedWidget(method.needDate());
                    }
                }
            });

        departedLabel = widgetCreator.createLabel(client, "Packed");
        departedLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        departedValidator = new NotNullValidator("Date Packed should be set");

        dateSentWidget = createDateTimeWidget(client, departedLabel,
            shipmentInfo.getPackedAt(), shipmentInfo,
            ShipmentInfoPeer.PACKED_AT.getName(), departedValidator, SWT.DATE
                | SWT.TIME, DATE_SHIPPED_BINDING);
        activateDepartedWidget(shipmentInfo.getShippingMethod() != null
            && shipmentInfo.getShippingMethod().needDate());

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            "Box Number", null, shipmentInfo,
            ShipmentInfoPeer.BOX_NUMBER.getName(), null);

        createDateTimeWidget(client, "Received", shipmentInfo.getReceivedAt(),
            shipmentInfo, ShipmentInfoPeer.RECEIVED_AT.getName(),
            new NotNullValidator("Date Received should be set"));

        if (originInfo.isNew()
            || originInfo.getShipmentInfo().getShipmentTempLogger()
                .getDeviceId() == null) {
            deviceIDWidget = (BgcBaseText) createBoundWidgetWithLabel(client,
                BgcBaseText.class, SWT.NONE, "Logger Device ID", null,
                shipmentInfo.getShipmentTempLogger(),
                ShipmentTempLoggerPeer.DEVICE_ID.getName(), null);
            deviceIDWidget.addListener(SWT.Modify, new Listener() {
                // org.eclipse.swt.widgets.Event conflicts with the other Event
                @Override
                public void handleEvent(org.eclipse.swt.widgets.Event event) {
                    if (!deviceIDWidget.getText().trim().isEmpty()) {
                        activateLoggerWidgets(true);
                    } else {
                        activateLoggerWidgets(false);
                    }
                }
            });

        } else {
            BgcBaseText deviceIDLabel = createReadOnlyLabelledField(client,
                SWT.NONE, "Logger Device ID");
            setTextValue(deviceIDLabel, originInfo.getShipmentInfo()
                .getShipmentTempLogger().getDeviceId());
        }

        hiTempLabel = widgetCreator.createLabel(client,
            "Highest temperature during transport (Celcius)");
        hiTempLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        hiTempWidget = (BgcBaseText) createBoundWidget(client,
            BgcBaseText.class, SWT.NONE, hiTempLabel, new String[0],
            shipmentInfo.getShipmentTempLogger(),
            ShipmentTempLoggerPeer.HIGH_TEMPERATURE.getName(),
            new NonEmptyStringValidator("Highest temperature should be set"),
            HI_TEMP_BINDING);

        lowTempLabel = widgetCreator.createLabel(client,
            "Lowest temperature during transport (Celcius)");
        lowTempLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        lowTempWidget = (BgcBaseText) createBoundWidget(client,
            BgcBaseText.class, SWT.NONE, lowTempLabel, new String[0],
            shipmentInfo.getShipmentTempLogger(),
            ShipmentTempLoggerPeer.LOW_TEMPERATURE.getName(),
            new NonEmptyStringValidator("Lowest temperature should be set"),
            LOW_TEMP_BINDING);

        tempResultLabel = widgetCreator.createLabel(client,
            "Shipment temperature result");
        tempResultLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        passFailComposite = createPassFail(client);

        aboveMaxTempLabel = widgetCreator.createLabel(client,
            "Number of minutes above maximum threshold");
        aboveMaxTempLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        aboveMaxTempWidget = (BgcBaseText) createBoundWidget(client,
            BgcBaseText.class, SWT.NONE, aboveMaxTempLabel, new String[0],
            shipmentInfo.getShipmentTempLogger(),
            ShipmentTempLoggerPeer.MINUTES_ABOVE_MAX.getName(), null,
            ABOVE_MAX_TEMP_BINDING);

        belowMaxTempLabel = widgetCreator.createLabel(client,
            "Number of minutes below maximum threshold");
        belowMaxTempLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        belowMaxTempWidget = (BgcBaseText) createBoundWidget(client,
            BgcBaseText.class, SWT.NONE, belowMaxTempLabel, new String[0],
            shipmentInfo.getShipmentTempLogger(),
            ShipmentTempLoggerPeer.MINUTES_BELOW_MAX.getName(), null,
            BELOW_MAX_TEMP_BINDING);

        uploadtLabel = widgetCreator.createLabel(client,
            "Upload tempurature logger report");
        uploadtLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));

        uploadComposite = createUpload(client);

        if (originInfo.getShipmentInfo().getShipmentTempLogger().getDeviceId() == null) {
            activateLoggerWidgets(false);
        } else {
            activateLoggerWidgets(true);
        }

        // createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
        // "Highest temperature during transport (Celcius)", null,
        // shipmentInfo.getShipmentTempLogger(),
        // ShipmentTempLoggerPeer.HIGH_TEMPERATURE.getName(), null);
        //
        // createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
        // "Lowest temperature during transport (Celcius)", null,
        // shipmentInfo.getShipmentTempLogger(),
        // ShipmentTempLoggerPeer.LOW_TEMPERATURE.getName(), null);
        //
        // createPassFail(client);

        // createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
        // "Number of minutes above maximum threshold", null,
        // shipmentInfo.getShipmentTempLogger(),
        // ShipmentTempLoggerPeer.MINUTES_ABOVE_MAX.getName(), null);
        //
        // createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
        // "Number of minutes below maximum threshold", null,
        // shipmentInfo.getShipmentTempLogger(),
        // ShipmentTempLoggerPeer.MINUTES_BELOW_MAX.getName(), null);
        //
        // createUpload(client);

    }

    protected Composite createPassFail(Composite client) {

        Composite composite = new Composite(client, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 30;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);
        composite
            .setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        radioTempPass = new Button(composite, SWT.RADIO);
        radioTempPass.setText("Pass");
        radioTempFail = new Button(composite, SWT.RADIO);
        radioTempFail.setText("Fail");
        if (shipmentInfo.getShipmentTempLogger() != null
            && shipmentInfo.getShipmentTempLogger().getTemperatureResult() != null) {
            if (shipmentInfo.getShipmentTempLogger().getTemperatureResult()) {
                radioTempPass.setSelection(true);
            } else {
                radioTempFail.setSelection(true);
            }
        }

        radioTempPass.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioTempPass.getSelection()) {
                    shipmentInfo.getShipmentTempLogger().setTemperatureResult(
                        true);
                    setDirty(true);
                }
            }
        });

        radioTempFail.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioTempFail.getSelection()) {
                    shipmentInfo.getShipmentTempLogger().setTemperatureResult(
                        false);
                    setDirty(true);
                }
            }
        });
        return composite;
    }

    private Composite createUpload(Composite parent) {

        Composite composite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        layout.marginLeft = -5;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(composite);

        final BgcBaseText fileToUpload = widgetCreator.createReadOnlyField(
            composite, SWT.NONE, "PDF File", true);

        uploadButton = toolkit.createButton(composite, "Upload", SWT.NONE);
        uploadButton.addListener(SWT.Selection, new Listener() {
            // org.eclipse.swt.widgets.Event conflicts with the other Event
            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                String[] filterExt = new String[] { "*.pdf" };
                path = runFileDialog("home", filterExt);
                if (path != null)
                    fileToUpload.setText(path);
                File fl = new File(path);
                try {
                    shipmentInfo.getShipmentTempLogger().setFile(
                        FileUtils.readFileToByteArray(fl));
                    setDirty(true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
        return composite;
    }

    private String runFileDialog(String name, String[] exts) {
        FileDialog fd = new FileDialog(form.getShell(), SWT.OPEN);
        fd.setOverwrite(true);
        fd.setText("Select PDF");
        fd.setFilterExtensions(exts);
        fd.setFileName(name);
        return fd.open();
    }

    protected void activateWaybillWidget(boolean waybillNeeded) {
        if (waybillLabel != null && !waybillLabel.isDisposed()) {
            waybillLabel.setVisible(waybillNeeded);
            ((GridData) waybillLabel.getLayoutData()).exclude = !waybillNeeded;
        }
        if (waybillWidget != null && !waybillWidget.isDisposed()) {
            waybillWidget.setVisible(waybillNeeded);
            ((GridData) waybillWidget.getLayoutData()).exclude = !waybillNeeded;

            if (waybillNeeded) {
                widgetCreator.addBinding(WAYBILL_BINDING);
            } else {
                widgetCreator.removeBinding(WAYBILL_BINDING);
                waybillWidget.setText("");
            }
        }
        form.layout(true, true);
    }

    protected void activateLoggerWidgets(boolean isNeeded) {
        if (hiTempLabel != null && !hiTempLabel.isDisposed()) {
            hiTempLabel.setVisible(isNeeded);
            ((GridData) hiTempLabel.getLayoutData()).exclude = !isNeeded;
            lowTempLabel.setVisible(isNeeded);
            ((GridData) lowTempLabel.getLayoutData()).exclude = !isNeeded;
            tempResultLabel.setVisible(isNeeded);
            ((GridData) tempResultLabel.getLayoutData()).exclude = !isNeeded;
            aboveMaxTempLabel.setVisible(isNeeded);
            ((GridData) aboveMaxTempLabel.getLayoutData()).exclude = !isNeeded;
            belowMaxTempLabel.setVisible(isNeeded);
            ((GridData) belowMaxTempLabel.getLayoutData()).exclude = !isNeeded;
            uploadtLabel.setVisible(isNeeded);
            ((GridData) uploadtLabel.getLayoutData()).exclude = !isNeeded;

        }
        if (hiTempWidget != null && !hiTempWidget.isDisposed()) {
            hiTempWidget.setVisible(isNeeded);
            ((GridData) hiTempWidget.getLayoutData()).exclude = !isNeeded;
            lowTempWidget.setVisible(isNeeded);
            ((GridData) lowTempWidget.getLayoutData()).exclude = !isNeeded;
            passFailComposite.setVisible(isNeeded);
            ((GridData) passFailComposite.getLayoutData()).exclude = !isNeeded;
            aboveMaxTempWidget.setVisible(isNeeded);
            ((GridData) aboveMaxTempWidget.getLayoutData()).exclude = !isNeeded;
            belowMaxTempWidget.setVisible(isNeeded);
            ((GridData) belowMaxTempWidget.getLayoutData()).exclude = !isNeeded;
            uploadComposite.setVisible(isNeeded);
            ((GridData) uploadComposite.getLayoutData()).exclude = !isNeeded;
            if (isNeeded) {
                widgetCreator.addBinding(HI_TEMP_BINDING);
                widgetCreator.addBinding(LOW_TEMP_BINDING);
                widgetCreator.addBinding(ABOVE_MAX_TEMP_BINDING);
                widgetCreator.addBinding(BELOW_MAX_TEMP_BINDING);
            } else {
                widgetCreator.removeBinding(HI_TEMP_BINDING);
                widgetCreator.removeBinding(LOW_TEMP_BINDING);
                widgetCreator.removeBinding(ABOVE_MAX_TEMP_BINDING);
                widgetCreator.removeBinding(BELOW_MAX_TEMP_BINDING);
                hiTempWidget.setText("");
                lowTempWidget.setText("");
                radioTempPass.setSelection(false);
                radioTempFail.setSelection(true);
                aboveMaxTempWidget.setText("");
                belowMaxTempWidget.setText("");
            }
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
        Composite client = createSectionWithClient("Specimens");
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL, GridData.FILL));
        toolkit.paintBordersFor(client);

        List<SpecimenWrapper> specimens = originInfo
            .getSpecimenCollection(true);

        specimenEntryWidget = new SpecimenEntryWidget(client, SWT.NONE,
            toolkit, appService, true);
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
            "Specimens should be added to a shipment");

        VetoListener<ItemAction, SpecimenWrapper> vetoListener = new VetoListener<ItemAction, SpecimenWrapper>() {
            @Override
            public void handleEvent(Event<ItemAction, SpecimenWrapper> event)
                throws VetoException {
                SpecimenWrapper specimen = event.getObject();
                switch (event.getType()) {
                case PRE_ADD:
                    if (specimen == null)
                        throw new VetoException(
                            "No specimen found for that inventory id.");
                    else if (specimen.isUsedInDispatch())
                        throw new VetoException(
                            "Specimen is currently listed in a dispatch.");
                    else if (specimen.getParentContainer() != null)
                        throw new VetoException(
                            "Specimen is currently listed as stored in a container.");
                    else if (specimen.getOriginInfo() != null
                        && specimen.getOriginInfo().getShipmentInfo() != null
                        && !specimen.getOriginInfo().getShipmentInfo()
                            .equals(originInfo.getShipmentInfo()))
                        throw new VetoException(
                            "Specimen is currently part of another shipment: "
                                + specimen.getOriginInfo().getShipmentInfo()
                                + ". You must remove this specimen from that shipment before it can be added to this one.");
                    break;
                case POST_ADD:
                    originInfo.addToSpecimenCollection(Arrays.asList(specimen));
                    specimen.setOriginInfo(originInfo);
                    break;
                case PRE_DELETE:
                    if (!originInfo.isNew()) {
                        try {
                            List<CenterWrapper<?>> centers = CenterWrapper
                                .getCenters(specimen.getAppService());
                            SpecimenOriginSelectDialog dlg = new SpecimenOriginSelectDialog(
                                form.getShell(), specimen, centers);

                            if (dlg.open() == Window.OK) {
                                specimensToPersist.add(specimen);
                            } else {
                                throw new VetoException(
                                    "Must select a new center for this specimen to originate from.");
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
        if (originInfo.getShipmentInfo().getWaybill() != null
            && originInfo.getShipmentInfo().getWaybill().isEmpty()) {
            originInfo.getShipmentInfo().setWaybill(null);
        }

        // If there is no device ID don't persist ShipmentTempLogger
        if (originInfo.getShipmentInfo().getShipmentTempLogger().getDeviceId() == null
            || (originInfo.getShipmentInfo().getShipmentTempLogger()
                .getDeviceId() != null && originInfo.getShipmentInfo()
                .getDeviceID().isEmpty())) {
            originInfo.getShipmentInfo().setShipmentTempLogger(null);
        }

        originInfo.persist();

        for (SpecimenWrapper s : specimensToPersist) {
            OriginInfoWrapper origin = s.getOriginInfo();
            origin.persist();

            s.setOriginInfo(origin);
            s.persist();
        }

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                SpecimenTransitView.reloadCurrent();
                if (!originInfo.getShipmentInfo().isReceivedToday())
                    SpecimenTransitView.showShipment(originInfo);
            }
        });
    }

    @Override
    protected void onReset() throws Exception {
        originInfo.reset();

        // do not change origin if form reset
        specimensToPersist.clear();

        shipmentInfo.reset();
        originInfo.setShipmentInfo(shipmentInfo);

        specimenEntryWidget.setSpecimens(originInfo
            .getSpecimenCollection(false));

        GuiUtil.reset(senderComboViewer, originInfo.getCenter());
        GuiUtil.reset(receiverComboViewer, originInfo.getReceiverSite());
        GuiUtil.reset(shippingMethodComboViewer,
            shipmentInfo.getShippingMethod());
    }
}
