package edu.ualberta.med.biobank.forms;

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
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;
import edu.ualberta.med.biobank.widgets.infotables.ClinicStudyInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ContactInfoTable;

public class ClinicViewForm extends AddressViewFormCommon {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ClinicViewForm";

    private ClinicWrapper clinic =
        new ClinicWrapper(SessionManager.getAppService());

    private ContactInfoTable contactsTable;

    private ClinicStudyInfoTable studiesTable;

    private BgcBaseText nameLabel;

    private BgcBaseText nameShortLabel;

    private Button hasShipmentsButton;

    private BgcBaseText activityStatusLabel;

    private CommentsInfoTable commentTable;

    private BgcBaseText patientTotal;

    private BgcBaseText ceventTotal;

    private ClinicInfo clinicInfo;

    private List<StudyCountInfo> studyCountInfo;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ClinicAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        Assert.isNotNull(adapter.getId());
        updateClinicInfo();
        setPartName(NLS.bind("Clinic {0}",
            clinic.getNameShort()));
    }

    private void updateClinicInfo() throws Exception {
        clinicInfo = SessionManager.getAppService().doAction(
            new ClinicGetInfoAction(adapter.getId()));
        Assert.isNotNull(clinicInfo);
        Assert.isNotNull(clinicInfo.clinic);
        clinic.setWrappedObject(clinicInfo.clinic);

        studyCountInfo =
            SessionManager.getAppService().doAction(
                new ClinicGetStudyInfoAction(adapter.getId())).getList();
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind("Clinic {0}", clinic.getName()));
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
            createReadOnlyLabelledField(client, SWT.NONE, "Name");
        nameShortLabel =
            createReadOnlyLabelledField(client, SWT.NONE, "Short Name");
        hasShipmentsButton =
            (Button) createLabelledWidget(client, Button.class, SWT.NONE,
                "Sends shipments");
        activityStatusLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                "Activity status");
        patientTotal =
            createReadOnlyLabelledField(client, SWT.NONE,
                "Total Patients");
        ceventTotal =
            createReadOnlyLabelledField(client, SWT.NONE,
                "Total Collection Events");

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
            createSectionWithClient("Contacts");
        List<ContactWrapper> contacts =
            ModelWrapper.wrapModelCollection(
                SessionManager.getAppService(), clinicInfo.contacts,
                ContactWrapper.class);

        contactsTable =
            new ContactInfoTable(client, contacts);
        contactsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(contactsTable);
    }

    private void createCommentsSection() {
        Composite client = createSectionWithClient("Comments");
        commentTable =
            new CommentsInfoTable(client,
                clinic.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    protected void createStudiesSection() {
        Composite client =
            createSectionWithClient("Studies");

        studiesTable = new ClinicStudyInfoTable(client, studyCountInfo);
        studiesTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(studiesTable);

        studiesTable
            .addClickListener(new IInfoTableDoubleClickItemListener<StudyCountInfo>() {

                @Override
                public void doubleClick(InfoTableEvent<StudyCountInfo> event) {
                    Study s =
                        ((StudyCountInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).getStudy();
                    AdapterBase.openForm(
                        new FormInput(
                            new StudyAdapter(null,
                                new StudyWrapper(SessionManager
                                    .getAppService(), s))),
                        StudyViewForm.ID);
                }
            });
        studiesTable
            .addEditItemListener(new IInfoTableEditItemListener<StudyCountInfo>() {
                @Override
                public void editItem(InfoTableEvent<StudyCountInfo> event) {
                    Study s =
                        ((StudyCountInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).getStudy();
                    AdapterBase.openForm(
                        new FormInput(
                            new StudyAdapter(null,
                                new StudyWrapper(SessionManager
                                    .getAppService(), s))),
                        StudyEntryForm.ID);
                }
            });
    }

    @Override
    public void setValues() throws Exception {
        form.setText(NLS.bind("Clinic {0}", clinic.getName()));
        setClinicValues();
        setAddressValues(clinic);
        contactsTable.setList(clinic.getContactCollection(true));
        studiesTable.setList(studyCountInfo);
        commentTable.setList(clinic.getCommentCollection(false));
    }

}
