package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetStudyInfoAction;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import edu.ualberta.med.biobank.widgets.infotables.ClinicStudyInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ContactInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicViewForm extends AddressViewFormCommon {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ClinicViewForm"; //$NON-NLS-1$

    private ClinicWrapper clinic;

    private ContactInfoTable contactsTable;

    private ClinicStudyInfoTable studiesTable;

    private BgcBaseText nameLabel;

    private BgcBaseText nameShortLabel;

    private Button hasShipmentsButton;

    private BgcBaseText activityStatusLabel;

    private CommentCollectionInfoTable commentTable;

    private BgcBaseText patientTotal;

    private BgcBaseText ceventTotal;

    private ClinicInfo clinicInfo;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ClinicAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        updateClinicInfo();
        setPartName(NLS.bind(Messages.ClinicViewForm_title,
            clinic.getNameShort()));
    }

    private void updateClinicInfo() throws Exception {
        clinicInfo = SessionManager.getAppService().doAction(
            new ClinicGetInfoAction(adapter.getId()));
        clinic =
            new ClinicWrapper(SessionManager.getAppService(), clinicInfo.clinic);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.ClinicViewForm_title, clinic.getName()));

        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createClinicSection();
        createCommentsSection();
        createAddressSection(clinic);
        createContactsSection();
        createStudiesSection();
    }

    private void createClinicSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        nameLabel =
            createReadOnlyLabelledField(client, SWT.NONE, Messages.label_name);
        nameShortLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.label_nameShort);
        hasShipmentsButton =
            (Button) createLabelledWidget(client, Button.class, SWT.NONE,
                Messages.clinic_field_label_sendsShipments);
        activityStatusLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.label_activity);
        patientTotal =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.ClinicViewForm_field_label_totalPatients);
        ceventTotal =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.ClinicViewForm_field_label_totalCollectionEvents);

        setClinicValues();
    }

    private void setClinicValues() throws Exception {
        setTextValue(nameLabel, clinic.getName());
        setTextValue(nameShortLabel, clinic.getNameShort());
        setCheckBoxValue(hasShipmentsButton, clinic.getSendsShipments());
        setTextValue(activityStatusLabel, clinic.getActivityStatus());
        setTextValue(patientTotal, clinicInfo.patientCount);
        setTextValue(ceventTotal, clinicInfo.collectionEventCount);
    }

    private void createContactsSection() {
        Composite client =
            createSectionWithClient(Messages.clinic_contact_title);
        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        for (Contact c : clinicInfo.contacts) {
            contacts.add(new ContactWrapper(SessionManager.getAppService(), c));
        }
        contactsTable =
            new ContactInfoTable(client, contacts);
        contactsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(contactsTable);
    }

    private void createCommentsSection() {
        Composite client = createSectionWithClient(Messages.label_comments);
        commentTable =
            new CommentCollectionInfoTable(client,
                clinic.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    protected void createStudiesSection() throws ApplicationException {
        Composite client =
            createSectionWithClient(Messages.ClinicViewForm_studies_title);
        List<StudyWrapper> studies = new ArrayList<StudyWrapper>();
        for (StudyCountInfo s : clinicInfo.studyInfos) {
            studies.add(new StudyWrapper(SessionManager.getAppService(), s
                .getStudy()));
        }

        List<StudyCountInfo> studyCountInfo =
            SessionManager.getAppService().doAction(
                new ClinicGetStudyInfoAction(adapter.getId())).getList();

        studiesTable = new ClinicStudyInfoTable(client, studyCountInfo);
        studiesTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(studiesTable);

        studiesTable.addClickListener(collectionDoubleClickListener);
        studiesTable.createDefaultEditItem();
    }

    @Override
    public void reload() throws Exception {
        updateClinicInfo();
        setPartName(NLS.bind(Messages.ClinicViewForm_title, clinic.getName()));
        form.setText(NLS.bind(Messages.ClinicViewForm_title, clinic.getName()));
        setClinicValues();
        setAddressValues(clinic);
        contactsTable.setList(clinic.getContactCollection(true));
        studiesTable.setList(clinic.getStudyCollection());
        commentTable.setList(clinic.getCommentCollection(false));
    }

}
