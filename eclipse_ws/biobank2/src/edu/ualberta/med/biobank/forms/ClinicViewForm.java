package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import edu.ualberta.med.biobank.widgets.infotables.ClinicStudyInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ContactInfoTable;

public class ClinicViewForm extends AddressViewFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ClinicViewForm"; //$NON-NLS-1$

    private ClinicAdapter clinicAdapter;

    private ClinicWrapper clinic;

    private ContactInfoTable contactsTable;

    private ClinicStudyInfoTable studiesTable;

    private BgcBaseText nameLabel;

    private BgcBaseText nameShortLabel;

    private Button hasShipmentsButton;

    private BgcBaseText activityStatusLabel;

    private BgcBaseText commentLabel;

    private BgcBaseText patientTotal;

    private BgcBaseText ceventTotal;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ClinicAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        clinicAdapter = (ClinicAdapter) adapter;
        clinic = clinicAdapter.getWrapper();
        clinic.reload();
        setPartName(NLS.bind(Messages.ClinicViewForm_title,
            clinic.getNameShort()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.ClinicViewForm_title, clinic.getName()));

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
            Messages.label_name);
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.label_nameShort);
        hasShipmentsButton = (Button) createLabelledWidget(client,
            Button.class, SWT.NONE, Messages.clinic_field_label_sendsShipments);
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.label_activity);
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.label_comments);
        patientTotal = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.ClinicViewForm_field_label_totalPatients);
        ceventTotal = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.ClinicViewForm_field_label_totalCollectionEvents);

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
        Composite client = createSectionWithClient(Messages.clinic_contact_title);

        contactsTable = new ContactInfoTable(client,
            clinic.getContactCollection(false));
        contactsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(contactsTable);
    }

    protected void createStudiesSection() {
        Composite client = createSectionWithClient(Messages.ClinicViewForm_studies_title);

        studiesTable = new ClinicStudyInfoTable(client, clinic);
        studiesTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(studiesTable);

        studiesTable.addClickListener(collectionDoubleClickListener);
    }

    @Override
    public void reload() throws Exception {
        clinic.reload();
        setPartName(NLS.bind(Messages.ClinicViewForm_title, clinic.getName()));
        form.setText(NLS.bind(Messages.ClinicViewForm_title, clinic.getName()));
        setClinicValues();
        setAdressValues(clinic);
        contactsTable.setCollection(clinic.getContactCollection(true));
        studiesTable.setCollection(clinic.getStudyCollection());
    }

}
