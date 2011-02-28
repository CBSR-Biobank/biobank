package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.widgets.BasicSiteCombo;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.ShipmentPatientsWidget;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ShipmentEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ShipmentEntryForm";

    public static final String MSG_NEW_SHIPMENT_OK = "Creating a new shipment record.";

    public static final String MSG_SHIPMENT_OK = "Editing an existing shipment record.";

    private ShipmentAdapter shipmentAdapter;

    private CollectionEventWrapper shipment;

    private ComboViewer clinicsComboViewer;

    private ComboViewer shippingMethodComboViewer;

    private ShipmentPatientsWidget shipmentPatientsWidget;

    private BiobankText waybillText;

    private Label waybillLabel;

    private NonEmptyStringValidator waybillValidator;

    private static final String WAYBILL_BINDING = "shipment-waybill-binding";

    private static final String DATE_SHIPPED_BINDING = "shipment-date-shipped-binding";

    private DateTimeWidget departedWidget;

    private DateTimeWidget dateReceivedWidget;

    private Label departedLabel;

    private NotNullValidator departedValidator;

    private BasicSiteCombo siteCombo;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ShipmentAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipmentAdapter = (ShipmentAdapter) adapter;
        shipment = shipmentAdapter.getWrapper();
        try {
            shipment.reload();
        } catch (Exception e) {
            logger.error("Error while retrieving shipment", e);
        }
        String tabName;
        if (shipment.isNew()) {
            tabName = "New Shipment";
        } else {
            tabName = "Shipment " + shipment.getFormattedDateReceived();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Shipment Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));
        createMainSection();
        createPatientsSection();
    }

    private void createMainSection() throws ApplicationException {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        siteCombo = createBasicSiteCombo(client, true,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    shipment.setSite(siteCombo.getSelectedSite());
                    if (clinicsComboViewer != null)
                        clinicsComboViewer.setInput(siteCombo.getSelectedSite()
                            .getWorkingClinicCollection());
                }
            });
        setFirstControl(siteCombo);

        ClinicWrapper selectedClinic = null;
        if (shipment.isNew()) {
            // choose clinic for new shipment
            selectedClinic = shipment.getClinic();
            clinicsComboViewer = createComboViewer(client, "Clinic",
                new ArrayList<ClinicWrapper>(), selectedClinic,
                "A clinic should be selected", new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        shipment.setClinic((ClinicWrapper) selectedObject);
                        if (shipment.getClinic() != null) {
                            try {
                                shipment.checkPatientsStudy();
                            } catch (Exception e) {
                                BioBankPlugin.openAsyncError("Patients check",
                                    e);
                            }
                            if (waybillText != null) {
                                activateWaybillField(Boolean.TRUE
                                    .equals(shipment.getClinic()
                                        .getSendsShipments()));
                            }
                        }
                    }
                });
        } else {
            BiobankText clinicLabel = createReadOnlyLabelledField(client,
                SWT.NONE, "Clinic");
            if (shipment.getClinic() != null) {
                clinicLabel.setText(shipment.getClinic().getName());
            }
        }
        siteCombo.setSelectedSite(shipment.getSite(), true);

        waybillLabel = widgetCreator.createLabel(client, "Waybill");
        waybillLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        waybillValidator = new NonEmptyStringValidator(
            "A waybill should be set");
        waybillText = (BiobankText) createBoundWidget(client,
            BiobankText.class, SWT.NONE, waybillLabel, new String[0], shipment,
            "waybill", waybillValidator, WAYBILL_BINDING);
        activateWaybillField(Boolean.TRUE.equals(shipment.getClinic() != null
            && shipment.getClinic().getSendsShipments()));
        if (clinicsComboViewer != null && selectedClinic != null) {
            clinicsComboViewer.setSelection(new StructuredSelection(
                selectedClinic));
        }

        // ShippingMethodWrapper selectedShippingMethod = shipment
        // .getShippingMethod();
        // shippingMethodComboViewer = createComboViewer(client,
        // "Shipping Method",
        // ShippingMethodWrapper.getShippingMethods(appService),
        // selectedShippingMethod, null, new ComboSelectionUpdate() {
        // @Override
        // public void doSelection(Object selectedObject) {
        // shipment
        // .setShippingMethod((ShippingMethodWrapper) selectedObject);
        // if (shipment.getShippingMethod() != null) {
        // if (departedWidget != null) {
        // activateDepartedWidget(shipment.needDeparted());
        // }
        // }
        // }
        // });
        // if (getFirstControl() == null)
        // setFirstControl(shippingMethodComboViewer.getCombo());
        //
        // if (shipment.getDeparted() == null)
        // shipment.setDeparted(new Date());
        //
        // departedLabel = widgetCreator.createLabel(client, "Departed");
        // departedLabel.setLayoutData(new GridData(
        // GridData.VERTICAL_ALIGN_BEGINNING));
        // departedValidator = new NotNullValidator("Departed should be set");
        // departedWidget = createDateTimeWidget(client, departedLabel,
        // shipment.getDeparted(), shipment, "departed", departedValidator,
        // SWT.DATE | SWT.TIME, DATE_SHIPPED_BINDING);
        // activateDepartedWidget(shipment.needDeparted());
        //
        // createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
        // "Box Number", null, shipment, "boxNumber", null);
        //
        // if (shipment.getDateReceived() == null)
        // shipment.setDateReceived(new Date());
        //
        // dateReceivedWidget = createDateTimeWidget(client, "Date Received",
        // shipment.getDateReceived(), shipment, "dateReceived",
        // new NotNullValidator("Date received should be set"));
        //
        // activityStatusComboViewer = createComboViewer(client,
        // "Activity Status",
        // ActivityStatusWrapper.getAllActivityStatuses(appService),
        // shipment.getActivityStatus(),
        // "Container must have an activity status",
        // new ComboSelectionUpdate() {
        // @Override
        // public void doSelection(Object selectedObject) {
        // shipment
        // .setActivityStatus((ActivityStatusWrapper) selectedObject);
        // }
        // });
        // if (shipment.getActivityStatus() != null) {
        // activityStatusComboViewer.setSelection(new StructuredSelection(
        // shipment.getActivityStatus()));
        // }
        //
        // createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
        // "Comments", null, shipment, "comment", null);

    }

    protected void activateDepartedWidget(boolean departedNeeded) {
        departedWidget.setVisible(departedNeeded);
        ((GridData) departedWidget.getLayoutData()).exclude = !departedNeeded;
        departedLabel.setVisible(departedNeeded);
        ((GridData) departedLabel.getLayoutData()).exclude = !departedNeeded;
        if (departedNeeded) {
            widgetCreator.addBinding(DATE_SHIPPED_BINDING);
        } else {
            widgetCreator.removeBinding(DATE_SHIPPED_BINDING);
        }
        form.layout(true, true);

    }

    private void activateWaybillField(boolean activate) {
        waybillText.setVisible(activate);
        ((GridData) waybillText.getLayoutData()).exclude = !activate;
        waybillLabel.setVisible(activate);
        ((GridData) waybillLabel.getLayoutData()).exclude = !activate;
        if (activate) {
            widgetCreator.addBinding(WAYBILL_BINDING);
            waybillValidator.validate(waybillText.getText());
        } else {
            widgetCreator.removeBinding(WAYBILL_BINDING);
            waybillValidator.validate("test");
        }
        form.layout(true, true);
    }

    private void createPatientsSection() {
        Composite client = createSectionWithClient("Patients");
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL, GridData.FILL));
        toolkit.paintBordersFor(client);

        shipmentPatientsWidget = new ShipmentPatientsWidget(client, SWT.NONE,
            shipment, toolkit, true);
        shipmentPatientsWidget
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    setDirty(true);
                }
            });
        shipmentPatientsWidget
            .addDoubleClickListener(collectionDoubleClickListener);
        shipmentPatientsWidget.addBinding(widgetCreator,
            "Patients should be added to a shipment");
    }

    @Override
    public String getNextOpenedFormID() {
        return ShipmentViewForm.ID;
    }

    @Override
    protected String getOkMessage() {
        return (shipment.isNew()) ? MSG_NEW_SHIPMENT_OK : MSG_SHIPMENT_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        // if (!Boolean.TRUE.equals(shipment.getClinic().getSendsShipments())) {
        // shipment.setWaybill(null);
        // }
        // if (!shipment.needDeparted()) {
        // shipment.setDeparted(null);
        // }
        //
        // shipment.persist();
        //
        // Display.getDefault().syncExec(new Runnable() {
        // @Override
        // public void run() {
        // ShipmentAdministrationView.reloadCurrent();
        // PatientAdministrationView.reloadCurrent();
        // if (!shipment.isReceivedToday())
        // ShipmentAdministrationView.showShipment(shipment);
        // }
        // });
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        if (shipment.isNew()
            && clinicsComboViewer.getCombo().getItemCount() > 1) {
            clinicsComboViewer.getCombo().deselectAll();
        }
        departedWidget.setDate(new Date());
        dateReceivedWidget.setDate(new Date());

        shipmentPatientsWidget.updateList();
        // ShippingMethodWrapper shipMethod = shipment.getShippingMethod();
        // if (shipMethod != null) {
        // shippingMethodComboViewer.setSelection(new StructuredSelection(
        // shipMethod));
        // } else if (shippingMethodComboViewer.getCombo().getItemCount() > 1) {
        // shippingMethodComboViewer.getCombo().deselectAll();
        // }
    }

}
