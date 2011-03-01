package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.ClinicStudyInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ContactInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.OriginInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicViewForm extends AddressViewFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ClinicViewForm";

    private ClinicAdapter clinicAdapter;

    private ClinicWrapper clinic;

    private ContactInfoTable contactsTable;

    private ClinicStudyInfoTable studiesTable;

    private BiobankText nameLabel;

    private BiobankText nameShortLabel;

    private Button hasShipmentsButton;

    private BiobankText activityStatusLabel;

    private BiobankText commentLabel;

    private BiobankText patientTotal;

    private BiobankText visitTotal;

    private BiobankText shipmentTotal;

    private OriginInfoTable shipmentsTable;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ClinicAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        clinicAdapter = (ClinicAdapter) adapter;
        clinic = clinicAdapter.getWrapper();
        clinic.reload();
        setPartName(Messages.getString("ClinicViewForm.title",
            clinic.getNameShort()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("ClinicViewForm.title",
            clinic.getName()));

        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createClinicSection();
        createAddressSection(clinic);
        createContactsSection();
        createStudiesSection();
        createShipmentsSection();
    }

    private void createClinicSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        nameLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.name"));
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.nameShort"));
        hasShipmentsButton = (Button) createLabelledWidget(client,
            Button.class, SWT.NONE,
            Messages.getString("clinic.field.label.sendsShipments"));
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.activity"));
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.getString("label.comments"));
        shipmentTotal = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("ClinicViewForm.field.label.totalShipments"));
        patientTotal = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("ClinicViewForm.field.label.totalPatients"));
        visitTotal = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("ClinicViewForm.field.label.totalPatientVisits"));

        setClinicValues();
    }

    private void setClinicValues() throws Exception {
        setTextValue(nameLabel, clinic.getName());
        setTextValue(nameShortLabel, clinic.getNameShort());
        setCheckBoxValue(hasShipmentsButton, clinic.getSendsShipments());
        setTextValue(activityStatusLabel, clinic.getActivityStatus());
        setTextValue(commentLabel, clinic.getComment());
        setTextValue(shipmentTotal, clinic.getOriginInfoCollection(true));
        setTextValue(patientTotal, clinic.getPatientCount());
        setTextValue(visitTotal, clinic.getProcessingEventCount());
    }

    private void createContactsSection() {
        Composite client = createSectionWithClient(Messages
            .getString("clinic.contact.title"));

        contactsTable = new ContactInfoTable(client,
            clinic.getContactCollection());
        contactsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(contactsTable);
    }

    protected void createStudiesSection() throws ApplicationException {
        Composite client = createSectionWithClient(Messages
            .getString("ClinicViewForm.studies.title"));

        studiesTable = new ClinicStudyInfoTable(client, clinic);
        studiesTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(studiesTable);

        studiesTable.addClickListener(collectionDoubleClickListener);
    }

    protected void createShipmentsSection() {
        Composite client = createSectionWithClient(Messages
            .getString("ClinicViewForm.shipments.title"));

        shipmentsTable = new OriginInfoTable(client, clinic);
        shipmentsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(shipmentsTable);

        shipmentsTable.addClickListener(collectionDoubleClickListener);
    }

    @Override
    public void reload() throws Exception {
        clinic.reload();
        setPartName(Messages
            .getString("ClinicViewForm.title", clinic.getName()));
        form.setText(Messages.getString("ClinicViewForm.title",
            clinic.getName()));
        setClinicValues();
        setAdressValues(clinic);
        contactsTable.setCollection(clinic.getContactCollection(true));
        shipmentsTable.setCollection(clinic.getOriginInfoCollection(true));
        studiesTable.setCollection(clinic.getStudyCollection());
    }

}
