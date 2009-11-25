package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.ShipmentAdapter;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
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

    private DateTimeWidget dateDrawnWidget;

    private DateTimeWidget dateReceivedWidget;

    private Text commentText;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ShipmentAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipmentAdapter = (ShipmentAdapter) adapter;
        shipmentWrapper = shipmentAdapter.getWrapper();
        try {
            shipmentWrapper.reload();
        } catch (Exception e) {
            LOGGER.error("Error while retrieving shipment", e);
        }
        String tabName;
        if (shipmentWrapper.isNew()) {
            tabName = "New Shipment";
        } else {
            tabName = "Shipment " + shipmentWrapper.getFormattedDateDrawn();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Shipment Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));
        // form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
        // BioBankPlugin.IMG_PATIENT_VISIT));
        createMainSection();
    }

    private void createMainSection() throws ApplicationException {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Label siteLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Site");
        PatientWrapper patient = ((PatientAdapter) shipmentAdapter.getParent())
            .getWrapper();
        StudyWrapper study = patient.getStudy();
        SiteWrapper site = study.getSite();

        FormUtils.setTextValue(siteLabel, site);

        if (shipmentWrapper.isNew()) {
            // choose clinic for new shipment
            List<ClinicWrapper> studyClinics = study.getClinicCollection();
            ClinicWrapper selectedClinic = shipmentWrapper.getClinic();
            if (studyClinics.size() == 1) {
                selectedClinic = studyClinics.get(0);
            }
            clinicsComboViewer = createComboViewerWithNoSelectionValidator(
                client, "Clinic", studyClinics, selectedClinic,
                "A clinic should be selected");
        } else {
            Label clinicLabel = (Label) createWidget(client, Label.class,
                SWT.NONE, "Clinic");
            if (shipmentWrapper.getClinic() != null) {
                clinicLabel.setText(shipmentWrapper.getClinic().getName());
            }
        }

        dateDrawnWidget = createDateTimeWidget(client, "Date Drawn",
            shipmentWrapper.getDateDrawn(), shipmentWrapper, "dateDrawn",
            "Date drawn should be set", false);
        firstControl = dateDrawnWidget;

        dateReceivedWidget = createDateTimeWidget(client, "Date Received",
            shipmentWrapper.getDateReceived(), shipmentWrapper, "dateReceived",
            "Date received should be set", false);

        commentText = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.MULTI, "Comments", null, BeansObservables.observeValue(
                shipmentWrapper, "comment"), null);
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
        // TODO Auto-generated method stub

    }

}
