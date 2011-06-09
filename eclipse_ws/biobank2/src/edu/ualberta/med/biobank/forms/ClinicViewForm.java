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

public class ClinicViewForm extends AddressViewFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ClinicViewForm"; //$NON-NLS-1$

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

    private BiobankText ceventTotal;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ClinicAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        clinicAdapter = (ClinicAdapter) adapter;
        clinic = clinicAdapter.getWrapper();
        clinic.reload();
        setPartName(Messages.getString("ClinicViewForm.title", //$NON-NLS-1$
            clinic.getNameShort()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("ClinicViewForm.title", //$NON-NLS-1$
            clinic.getName()));

        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createClinicSection();
        createAddressSection(clinic);
        createContactsSection();
        createStudiesSection();
    }

    private void createClinicSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        nameLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.name")); //$NON-NLS-1$
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.nameShort")); //$NON-NLS-1$
        hasShipmentsButton = (Button) createLabelledWidget(client,
            Button.class, SWT.NONE,
            Messages.getString("clinic.field.label.sendsShipments")); //$NON-NLS-1$
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.activity")); //$NON-NLS-1$
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.getString("label.comments")); //$NON-NLS-1$
        patientTotal = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("ClinicViewForm.field.label.totalPatients")); //$NON-NLS-1$
        ceventTotal = createReadOnlyLabelledField(client, SWT.NONE,
            Messages
                .getString("ClinicViewForm.field.label.totalCollectionEvents")); //$NON-NLS-1$

        setClinicValues();
    }

    private void setClinicValues() throws Exception {
        setTextValue(nameLabel, clinic.getName());
        setTextValue(nameShortLabel, clinic.getNameShort());
        setCheckBoxValue(hasShipmentsButton, clinic.getSendsShipments());
        setTextValue(activityStatusLabel, clinic.getActivityStatus());
        setTextValue(commentLabel, clinic.getComment());
        setTextValue(patientTotal, clinic.getPatientCount());
        setTextValue(ceventTotal, clinic.getCollectionEventCount());
    }

    private void createContactsSection() {
        Composite client = createSectionWithClient(Messages
            .getString("clinic.contact.title")); //$NON-NLS-1$

        contactsTable = new ContactInfoTable(client,
            clinic.getContactCollection(false));
        contactsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(contactsTable);
    }

    protected void createStudiesSection() {
        Composite client = createSectionWithClient(Messages
            .getString("ClinicViewForm.studies.title")); //$NON-NLS-1$

        studiesTable = new ClinicStudyInfoTable(client, clinic);
        studiesTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(studiesTable);

        studiesTable.addClickListener(collectionDoubleClickListener);
    }

    @Override
    public void reload() throws Exception {
        clinic.reload();
        setPartName(Messages
            .getString("ClinicViewForm.title", clinic.getName())); //$NON-NLS-1$
        form.setText(Messages.getString("ClinicViewForm.title", //$NON-NLS-1$
            clinic.getName()));
        setClinicValues();
        setAdressValues(clinic);
        contactsTable.setCollection(clinic.getContactCollection(true));
        studiesTable.setCollection(clinic.getStudyCollection());
    }

}
