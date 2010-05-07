package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.ShipmentAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.views.ShipmentAdministrationView;
import edu.ualberta.med.biobank.views.ShipmentAdministrationView.ShipmentListener;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.ShipmentPatientsWidget;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ShipmentEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ShipmentEntryForm";

    public static final String MSG_NEW_SHIPMENT_OK = "Creating a new shipment record.";

    public static final String MSG_SHIPMENT_OK = "Editing an existing shipment record.";

    private ShipmentAdapter shipmentAdapter;

    private ShipmentWrapper shipmentWrapper;

    private ComboViewer clinicsComboViewer;

    private ComboViewer shippingMethodComboViewer;

    private SiteWrapper site;

    private ShipmentPatientsWidget shipmentPatientsWidget;

    private ShipmentListener shipListener;

    private Text waybillText;

    private Label waybillLabel;

    private NonEmptyStringValidator waybillValidator;

    private static final String WAYBILL_BINDING = "shipment-waybill-binding";

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ShipmentAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipmentAdapter = (ShipmentAdapter) adapter;
        shipmentWrapper = shipmentAdapter.getWrapper();
        site = SessionManager.getInstance().getCurrentSite();
        try {
            site.reload();
            shipmentWrapper.reload();
        } catch (Exception e) {
            logger.error("Error while retrieving shipment", e);
        }
        String tabName;
        if (shipmentWrapper.isNew()) {
            tabName = "New Shipment";
        } else {
            tabName = "Shipment " + shipmentWrapper.getFormattedDateReceived();
        }
        setPartName(tabName);
        shipListener = new ShipmentListener(shipmentAdapter);
        shipmentWrapper.addWrapperListener(shipListener);
    }

    @Override
    public void dispose() {
        shipmentWrapper.removeWrapperListener(shipListener);
        super.dispose();
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Shipment Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_SHIPMENT));
        createMainSection();
        createPatientsSection();
    }

    private void createMainSection() throws ApplicationException {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Text siteLabel = createReadOnlyLabelledField(client, SWT.NONE, "Site");
        setTextValue(siteLabel, site.getName());

        if (shipmentWrapper.isNew()) {
            // choose clinic for new shipment
            List<ClinicWrapper> siteClinics = site.getClinicCollection(true);
            ClinicWrapper selectedClinic = shipmentWrapper.getClinic();
            if (siteClinics.size() == 1) {
                selectedClinic = siteClinics.get(0);
            }
            clinicsComboViewer = createComboViewerWithNoSelectionValidator(
                client, "Clinic", siteClinics, selectedClinic,
                "A clinic should be selected");
            clinicsComboViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        setClinicFromSelection();
                        try {
                            shipmentWrapper.checkPatientsStudy();
                            activateWaybillField(Boolean.TRUE
                                .equals(shipmentWrapper.getClinic()
                                    .getSendsShipments()));
                        } catch (Exception e) {
                            BioBankPlugin.openAsyncError("Patients check", e);
                        }
                    }
                });
        } else {
            Text clinicLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Clinic");
            if (shipmentWrapper.getClinic() != null) {
                clinicLabel.setText(shipmentWrapper.getClinic().getName());
            }
        }

        waybillLabel = widgetCreator.createLabel(client, "Waybill");
        waybillLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        waybillValidator = new NonEmptyStringValidator(
            "A waybill should be set");
        waybillText = (Text) widgetCreator.createBoundWidget(client,
            Text.class, SWT.NONE, waybillLabel, new String[0], BeansObservables
                .observeValue(shipmentWrapper, "waybill"), waybillValidator,
            WAYBILL_BINDING);
        activateWaybillField(false);

        DateTimeWidget dateShippedWidget = createDateTimeWidget(client,
            "Date Shipped", shipmentWrapper.getDateShipped(), BeansObservables
                .observeValue(shipmentWrapper, "dateShipped"),
            "Date shipped should be set");
        setFirstControl(dateShippedWidget);

        ShippingMethodWrapper selectedShippingMethod = shipmentWrapper
            .getShippingMethod();
        shippingMethodComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Shipping Method", ShippingMethodWrapper
                .getShippingMethods(appService), selectedShippingMethod, null);

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Box Number",
            null, BeansObservables.observeValue(shipmentWrapper, "boxNumber"),
            null);

        createDateTimeWidget(client, "Date Received", shipmentWrapper
            .getDateReceived(), BeansObservables.observeValue(shipmentWrapper,
            "dateReceived"), "Date received should be set");

        createBoundWidgetWithLabel(client, Text.class, SWT.MULTI, "Comments",
            null, BeansObservables.observeValue(shipmentWrapper, "comment"),
            null);
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
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        shipmentPatientsWidget = new ShipmentPatientsWidget(client, SWT.NONE,
            shipmentWrapper, site, toolkit, true);
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
        return (shipmentWrapper.isNew()) ? MSG_NEW_SHIPMENT_OK
            : MSG_SHIPMENT_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        if (clinicsComboViewer != null) {
            setClinicFromSelection();
        }
        if (!Boolean.TRUE.equals(shipmentWrapper.getClinic()
            .getSendsShipments())) {
            shipmentWrapper.setWaybill(null);
        }

        IStructuredSelection shippingMethodSelection = (IStructuredSelection) shippingMethodComboViewer
            .getSelection();
        if ((shippingMethodSelection != null)
            && (shippingMethodSelection.size() > 0)) {
            shipmentWrapper
                .setShippingMethod((ShippingMethodWrapper) shippingMethodSelection
                    .getFirstElement());
        } else {
            shipmentWrapper.setShippingMethod((ShippingMethodWrapper) null);
        }
        boolean newShipment = shipmentWrapper.isNew();
        shipmentWrapper.persist();

        if (newShipment) {
            ShipmentAdministrationView.showShipment(shipmentWrapper);
        } else {
            shipmentAdapter.getParent().performExpand();
        }
    }

    private void setClinicFromSelection() {
        IStructuredSelection clinicSelection = (IStructuredSelection) clinicsComboViewer
            .getSelection();
        if ((clinicSelection != null) && (clinicSelection.size() > 0)) {
            shipmentWrapper.setClinic((ClinicWrapper) clinicSelection
                .getFirstElement());
        } else {
            shipmentWrapper.setClinic((ClinicWrapper) null);
        }
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        if (shipmentWrapper.isNew()
            && clinicsComboViewer.getCombo().getItemCount() > 1) {
            clinicsComboViewer.getCombo().deselectAll();
        }
        shipmentPatientsWidget.updateList();
        ShippingMethodWrapper shipMethod = shipmentWrapper.getShippingMethod();
        if (shipMethod != null) {
            shippingMethodComboViewer.setSelection(new StructuredSelection(
                shipMethod));
        } else if (shippingMethodComboViewer.getCombo().getItemCount() > 1) {
            shippingMethodComboViewer.getCombo().deselectAll();
        }
    }

}
