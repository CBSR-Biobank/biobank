package edu.ualberta.med.biobank.forms;

import java.util.Date;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.ClinicShipmentAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.views.PatientAdministrationView;
import edu.ualberta.med.biobank.views.ShipmentAdministrationView;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.ShipmentPatientsWidget;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicShipmentEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ClinicShipmentEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ShipmentEntryForm";

    public static final String MSG_NEW_SHIPMENT_OK = "Creating a new shipment record.";

    public static final String MSG_SHIPMENT_OK = "Editing an existing shipment record.";

    private ClinicShipmentAdapter shipmentAdapter;

    private ClinicShipmentWrapper shipment;

    private ComboViewer clinicsComboViewer;

    private ComboViewer shippingMethodComboViewer;

    private SiteWrapper site;

    private ShipmentPatientsWidget shipmentPatientsWidget;

    private BiobankText waybillText;

    private Label waybillLabel;

    private NonEmptyStringValidator waybillValidator;

    private static final String WAYBILL_BINDING = "shipment-waybill-binding";

    private boolean dateReceivedModified;

    private DateTimeWidget dateShippedWidget;

    private DateTimeWidget dateReceivedWidget;

    private ComboViewer activityStatusComboViewer;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ClinicShipmentAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipmentAdapter = (ClinicShipmentAdapter) adapter;
        shipment = shipmentAdapter.getWrapper();
        site = SessionManager.getInstance().getCurrentSite();
        try {
            site.reload();
            shipment.reload();
        } catch (Exception e) {
            logger.error("Error while retrieving shipment", e);
        }
        String tabName;
        if (shipment.isNew()) {
            tabName = "New Shipment";
            shipment.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
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
        form.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_SHIPMENT));
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

        BiobankText siteLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Site");
        setTextValue(siteLabel, site.getName());

        if (shipment.isNew()) {
            // choose clinic for new shipment
            List<ClinicWrapper> siteClinics = SessionManager.getInstance()
                .getSession().getClinicCollection(true);
            ClinicWrapper selectedClinic = shipment.getClinic();
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
                        clinicSelection();
                    }
                });
        } else {
            BiobankText clinicLabel = createReadOnlyLabelledField(client,
                SWT.NONE, "Clinic");
            if (shipment.getClinic() != null) {
                clinicLabel.setText(shipment.getClinic().getName());
            }
        }

        waybillLabel = widgetCreator.createLabel(client, "Waybill");
        waybillLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        waybillValidator = new NonEmptyStringValidator(
            "A waybill should be set");
        waybillText = (BiobankText) widgetCreator.createBoundWidget(client,
            BiobankText.class, SWT.NONE, waybillLabel, new String[0],
            BeansObservables.observeValue(shipment, "waybill"),
            waybillValidator, WAYBILL_BINDING);
        activateWaybillField(false);

        if (shipment.getDateShipped() == null)
            shipment.setDateShipped(new Date());

        dateShippedWidget = createDateTimeWidget(client, "Date Shipped",
            shipment.getDateShipped(),
            BeansObservables.observeValue(shipment, "dateShipped"),
            "Date shipped should be set");
        setFirstControl(dateShippedWidget);

        ShippingMethodWrapper selectedShippingMethod = shipment
            .getShippingMethod();
        shippingMethodComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Shipping Method",
            ShippingMethodWrapper.getShippingMethods(appService),
            selectedShippingMethod, null);

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
            "Box Number", null,
            BeansObservables.observeValue(shipment, "boxNumber"), null);

        if (shipment.getDateReceived() == null)
            shipment.setDateReceived(new Date());

        dateReceivedWidget = createDateTimeWidget(client, "Date Received",
            shipment.getDateReceived(),
            BeansObservables.observeValue(shipment, "dateReceived"),
            "Date received should be set");
        dateReceivedWidget.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                dateReceivedModified = true;
            }
        });

        activityStatusComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Activity Status",
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            shipment.getActivityStatus(),
            "Container must have an activity status");

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            "Comments", null,
            BeansObservables.observeValue(shipment, "comment"), null);

        if (clinicsComboViewer != null) {
            clinicSelection();
        }
    }

    private void clinicSelection() {
        setClinicFromSelection();
        if (shipment.getClinic() != null) {
            try {
                shipment.checkPatientsStudy();
            } catch (Exception e) {
                BioBankPlugin.openAsyncError("Patients check", e);
            }
            activateWaybillField(Boolean.TRUE.equals(shipment.getClinic()
                .getSendsShipments()));
        }
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
        return ClinicShipmentViewForm.ID;
    }

    @Override
    protected String getOkMessage() {
        return (shipment.isNew()) ? MSG_NEW_SHIPMENT_OK : MSG_SHIPMENT_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        shipment.setSite(site);
        setClinicFromSelection();
        if (!Boolean.TRUE.equals(shipment.getClinic().getSendsShipments())) {
            shipment.setWaybill(null);
        }

        IStructuredSelection shippingMethodSelection = (IStructuredSelection) shippingMethodComboViewer
            .getSelection();
        if ((shippingMethodSelection != null)
            && (shippingMethodSelection.size() > 0)) {
            shipment
                .setShippingMethod((ShippingMethodWrapper) shippingMethodSelection
                    .getFirstElement());
        } else {
            shipment.setShippingMethod((ShippingMethodWrapper) null);
        }
        ActivityStatusWrapper activityStatus = (ActivityStatusWrapper) ((StructuredSelection) activityStatusComboViewer
            .getSelection()).getFirstElement();
        shipment.setActivityStatus(activityStatus);
        boolean newShipment = shipment.isNew();
        shipment.persist();

        if (newShipment) {
            if (shipmentAdapter.getWrapper().isReceivedToday()) {
                shipmentAdapter.getParent().removeChild(shipmentAdapter, false);
                ShipmentAdministrationView.getCurrent().reload();
                if (PatientAdministrationView.getCurrent() != null) {
                    PatientAdministrationView.getCurrent().reload();
                }
            } else {
                ShipmentAdministrationView.showShipment(shipment);
            }
        } else {
            if (dateReceivedModified) {
                shipmentAdapter.getParent().removeChild(shipmentAdapter, false);
                ShipmentAdministrationView.getCurrent().reload();
                if (PatientAdministrationView.getCurrent() != null) {
                    PatientAdministrationView.getCurrent().reload();
                }
                if (!shipment.isReceivedToday()) {
                    ShipmentAdministrationView.showShipment(shipment);
                }
            }
            shipmentAdapter.getParent().performExpand();
        }
    }

    private void setClinicFromSelection() {
        if (clinicsComboViewer != null) {
            IStructuredSelection clinicSelection = (IStructuredSelection) clinicsComboViewer
                .getSelection();
            if ((clinicSelection != null) && (clinicSelection.size() > 0)) {
                shipment.setClinic((ClinicWrapper) clinicSelection
                    .getFirstElement());
            } else {
                shipment.setClinic((ClinicWrapper) null);
            }
        }
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        if (shipment.isNew()
            && clinicsComboViewer.getCombo().getItemCount() > 1) {
            clinicsComboViewer.getCombo().deselectAll();
        }
        dateShippedWidget.setDate(new Date());
        dateReceivedWidget.setDate(new Date());

        shipmentPatientsWidget.updateList();
        ShippingMethodWrapper shipMethod = shipment.getShippingMethod();
        if (shipMethod != null) {
            shippingMethodComboViewer.setSelection(new StructuredSelection(
                shipMethod));
        } else if (shippingMethodComboViewer.getCombo().getItemCount() > 1) {
            shippingMethodComboViewer.getCombo().deselectAll();
        }
        ActivityStatusWrapper activity = shipment.getActivityStatus();
        if (activity != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                activity));
        } else if (activityStatusComboViewer.getCombo().getItemCount() > 1) {
            activityStatusComboViewer.getCombo().deselectAll();
        }
    }

}
