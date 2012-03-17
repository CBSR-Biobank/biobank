package edu.ualberta.med.biobank.forms;

import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction.ContactSaveInfo;
import edu.ualberta.med.biobank.common.peer.ClinicPeer;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.ContactEntryInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicEntryForm extends AddressEntryFormCommon {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ClinicEntryForm"; //$NON-NLS-1$

    private static final String MSG_NEW_CLINIC_OK =
        Messages.ClinicEntryForm_creation_msg;

    private static final String MSG_CLINIC_OK = Messages.ClinicEntryForm_msg_ok;

    private static final String MSG_NO_CLINIC_NAME =
        Messages.ClinicEntryForm_msg_noClinicName;

    private ClinicWrapper clinic = new ClinicWrapper(
        SessionManager.getAppService());

    private ContactEntryInfoTable contactEntryWidget;

    protected Combo session;

    private BgcEntryFormWidgetListener listener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };

    private ComboViewer activityStatusComboViewer;

    private CommentsInfoTable commentEntryTable;

    private CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    private ClinicInfo clinicInfo;

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ClinicAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());
        updateClinicInfo(adapter.getId());
        String tabName;
        if (clinic.isNew()) {
            tabName = Messages.ClinicEntryForm_title_new;
            clinic.setActivityStatus(ActivityStatus.ACTIVE);
        } else
            tabName =
                NLS.bind(Messages.ClinicEntryForm_title_edit,
                    clinic.getNameShort());
        setPartName(tabName);
    }

    private void updateClinicInfo(Integer id) throws Exception {
        if (id != null) {
            clinicInfo =
                SessionManager.getAppService().doAction(
                    new ClinicGetInfoAction(id));
            clinic.setWrappedObject(clinicInfo.clinic);
        } else {
            clinicInfo = new ClinicInfo();
            clinic.setWrappedObject(new Clinic());
        }
        comment.setWrappedObject(new Comment());
        ((AdapterBase) adapter).setModelObject(clinic);
    }

    @Override
    protected String getOkMessage() {
        if (clinic.getId() == null) {
            return MSG_NEW_CLINIC_OK;
        }
        return MSG_CLINIC_OK;
    }

    @Override
    protected void createFormContent() throws ApplicationException {
        form.setText(Messages.ClinicEntryForm_main_title);
        page.setLayout(new GridLayout(1, false));
        toolkit.createLabel(page, Messages.ClinicEntryForm_main_description,
            SWT.LEFT);
        createClinicInfoSection();
        createAddressArea(clinic);
        createContactSection();
        createButtonsSection();

    }

    private void createClinicInfoSection() throws ApplicationException {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        setFirstControl(createBoundWidgetWithLabel(client, BgcBaseText.class,
            SWT.NONE, Messages.label_name, null, clinic,
            ClinicPeer.NAME.getName(), new NonEmptyStringValidator(
                MSG_NO_CLINIC_NAME)));

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            Messages.label_nameShort, null, clinic,
            ClinicPeer.NAME_SHORT.getName(), new NonEmptyStringValidator(
                MSG_NO_CLINIC_NAME));

        createBoundWidgetWithLabel(client, Button.class, SWT.CHECK,
            Messages.clinic_field_label_sendsShipments, null, clinic,
            ClinicPeer.SENDS_SHIPMENTS.getName(), null);
        toolkit.paintBordersFor(client);

        activityStatusComboViewer =
            createComboViewer(client, Messages.label_activity,
                ActivityStatus.valuesList(), clinic.getActivityStatus(),
                Messages.ClinicEntryForm_activity_validator_msg,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        clinic
                            .setActivityStatus((ActivityStatus) selectedObject);
                    }
                });

        createCommentSection();

    }

    private void createCommentSection() {
        Composite client = createSectionWithClient(Messages.Comments_title);
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable =
            new CommentsInfoTable(client, clinic.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        comment = new CommentWrapper(SessionManager.getAppService());

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            Messages.Comments_add, null, comment, "message", null);
    }

    private void createContactSection() {
        Section section = createSection(Messages.clinic_contact_title);

        List<ContactWrapper> contacts =
            ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
                clinicInfo.contacts, ContactWrapper.class);

        contactEntryWidget = new ContactEntryInfoTable(section, contacts);
        contactEntryWidget.adaptToToolkit(toolkit, true);
        contactEntryWidget.addSelectionChangedListener(listener);

        addSectionToolbar(section, Messages.ClinicEntryForm_contact_button_add,
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    contactEntryWidget.addContact();
                }
            }, ContactWrapper.class);
        section.setClient(contactEntryWidget);
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
    }

    @Override
    public void saveForm() throws Exception {
        ClinicSaveAction saveClinic = new ClinicSaveAction();
        saveClinic.setId(clinic.getId());
        saveClinic.setName(clinic.getName());
        saveClinic.setNameShort(clinic.getNameShort());
        saveClinic.setActivityStatus(clinic.getActivityStatus());
        saveClinic.setSendsShipments(clinic.getSendsShipments());
        saveClinic.setContactSaveInfos(getNewContactInfo());
        saveClinic.setAddress(clinic.getAddress().getWrappedObject());
        saveClinic.setCommentText(comment.getMessage());
        Integer id =
            SessionManager.getAppService().doAction(saveClinic).getId();
        updateClinicInfo(id);
        SessionManager.getUser().updateCurrentCenter(clinic);
    }

    private HashSet<ContactSaveInfo> getNewContactInfo() {
        clinic.addToContactCollection(contactEntryWidget
            .getAddedOrModifedContacts());
        clinic.removeFromContactCollection(contactEntryWidget
            .getDeletedContacts());

        HashSet<ContactSaveInfo> contactSaveInfos =
            new HashSet<ContactSaveInfo>();

        for (ContactWrapper wrapper : clinic.getContactCollection(false)) {
            contactSaveInfos
                .add(new ContactSaveInfo(wrapper.getWrappedObject()));
        }
        return contactSaveInfos;
    }

    @Override
    public String getNextOpenedFormId() {
        return ClinicViewForm.ID;
    }

    @Override
    public void setValues() throws Exception {
        if (clinic.isNew()) {
            clinic.setActivityStatus(ActivityStatus.ACTIVE);
        }

        GuiUtil.reset(activityStatusComboViewer, clinic.getActivityStatus());
        contactEntryWidget.reload();
        commentEntryTable.setList(clinic.getCommentCollection(false));
    }
}
