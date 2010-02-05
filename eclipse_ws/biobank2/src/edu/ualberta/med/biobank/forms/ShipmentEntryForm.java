package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingCompanyWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.ShipmentAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.views.ShipmentAdministrationView;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.ShipmentPatientsWidget;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentEntryForm extends BiobankEntryForm {

    private static Logger LOGGER = Logger.getLogger(ShipmentEntryForm.class
        .getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ShipmentEntryForm";

    public static final String MSG_NEW_SHIPMENT_OK = "Creating a new shipment record.";

    public static final String MSG_SHIPMENT_OK = "Editing an existing shipment record.";

    private ShipmentAdapter shipmentAdapter;

    private ShipmentWrapper shipmentWrapper;

    private ComboViewer clinicsComboViewer;

    private ComboViewer companyComboViewer;

    private SiteWrapper site;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ShipmentAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipmentAdapter = (ShipmentAdapter) adapter;
        shipmentWrapper = shipmentAdapter.getWrapper();
        site = SessionManager.getInstance().getCurrentSiteWrapper();
        try {
            shipmentWrapper.reload();
        } catch (Exception e) {
            LOGGER.error("Error while retrieving shipment", e);
        }
        String tabName;
        if (shipmentWrapper.isNew()) {
            tabName = "New Shipment";
        } else {
            tabName = "Shipment " + shipmentWrapper.getWaybill();
        }
        setPartName(tabName);
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

        Text siteLabel = createReadOnlyField(client, SWT.NONE, "Site");
        setTextValue(siteLabel, site.getName());

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Waybill",
            null, BeansObservables.observeValue(shipmentWrapper, "waybill"),
            new NonEmptyStringValidator("A waybill should be set"));

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
        } else {
            Text clinicLabel = createReadOnlyField(client, SWT.NONE, "Clinic");
            if (shipmentWrapper.getClinic() != null) {
                clinicLabel.setText(shipmentWrapper.getClinic().getName());
            }
        }

        DateTimeWidget dateShippedWidget = createDateTimeWidget(client,
            "Date Shipped", shipmentWrapper.getDateShipped(), shipmentWrapper,
            "dateShipped", "Date shipped should be set");
        firstControl = dateShippedWidget;

        ShippingCompanyWrapper selectedCompany = shipmentWrapper
            .getShippingCompany();
        companyComboViewer = createComboViewerWithNoSelectionValidator(client,
            "Shipping company", ShippingCompanyWrapper
                .getShippingCompanies(appService), selectedCompany, null);

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Box Number",
            null, BeansObservables.observeValue(shipmentWrapper, "boxNumber"),
            null);

        createDateTimeWidget(client, "Date Received", shipmentWrapper
            .getDateReceived(), shipmentWrapper, "dateReceived",
            "Date received should be set");

        createBoundWidgetWithLabel(client, Text.class, SWT.MULTI, "Comments",
            null, BeansObservables.observeValue(shipmentWrapper, "comment"),
            null);
    }

    private void createPatientsSection() {
        Composite client = createSectionWithClient("Patients");
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        ShipmentPatientsWidget shipmentPatientsWidget = new ShipmentPatientsWidget(
            client, SWT.NONE, shipmentWrapper, site, toolkit, true);
        shipmentPatientsWidget
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    setDirty(true);
                }
            });
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
            IStructuredSelection clinicSelection = (IStructuredSelection) clinicsComboViewer
                .getSelection();
            if ((clinicSelection != null) && (clinicSelection.size() > 0)) {
                shipmentWrapper.setClinic((ClinicWrapper) clinicSelection
                    .getFirstElement());
            } else {
                shipmentWrapper.setClinic((ClinicWrapper) null);
            }
        }
        IStructuredSelection companySelection = (IStructuredSelection) companyComboViewer
            .getSelection();
        if ((companySelection != null) && (companySelection.size() > 0)) {
            shipmentWrapper
                .setShippingCompany((ShippingCompanyWrapper) companySelection
                    .getFirstElement());
        } else {
            shipmentWrapper.setShippingCompany((ShippingCompanyWrapper) null);
        }
        boolean newShipment = shipmentWrapper.isNew();
        shipmentWrapper.persist();

        if (newShipment && ShipmentAdministrationView.currentInstance != null) {
            ShipmentAdministrationView.currentInstance
                .showInTree(shipmentWrapper);
        } else {
            shipmentAdapter.getParent().performExpand();
        }
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        if (shipmentWrapper.isNew()
            && clinicsComboViewer.getCombo().getItemCount() > 1) {
            clinicsComboViewer.getCombo().deselectAll();
        }
    }

}
