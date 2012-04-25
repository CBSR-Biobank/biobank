package edu.ualberta.med.biobank.forms;

import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasNameShort;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.ContactEntryInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicEntryForm extends AddressEntryFormCommon {
    private static final I18n i18n = I18nFactory
        .getI18n(ClinicEntryForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ClinicEntryForm";

    @SuppressWarnings("nls")
    // title area message
    private static final String MSG_NEW_CLINIC_OK =
        i18n.tr("New clinic information.");

    @SuppressWarnings("nls")
    // title area message
    private static final String MSG_CLINIC_OK = i18n.tr("Clinic information.");

    @SuppressWarnings("nls")
    // validation error message
    private static final String MSG_NO_CLINIC_NAME =
        i18n.tr("Clinic must have a name and short name");

    private final ClinicWrapper clinic = new ClinicWrapper(
        SessionManager.getAppService());

    private ContactEntryInfoTable contactEntryWidget;

    protected Combo session;

    private final BgcEntryFormWidgetListener listener =
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

    @SuppressWarnings("nls")
    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ClinicAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        updateClinicInfo(adapter.getId());
        String tabName;
        if (clinic.isNew()) {
            // tab name
            tabName = i18n.tr("New Clinic");
            clinic.setActivityStatus(ActivityStatus.ACTIVE);
        } else
            // tab name
            tabName = i18n.tr("Clinic {0}", clinic.getNameShort());
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

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws ApplicationException {
        // form title
        form.setText(i18n.tr("Clinic Information"));
        page.setLayout(new GridLayout(1, false));
        toolkit
            .createLabel(
                page,
                // label
                i18n.tr("Clinics can be associated with studies after submitting this initial information."),
                SWT.LEFT);
        createClinicInfoSection();
        createAddressArea(clinic);
        createContactSection();
        createButtonsSection();

    }

    @SuppressWarnings("nls")
    private void createClinicInfoSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        setFirstControl(createBoundWidgetWithLabel(client, BgcBaseText.class,
            SWT.NONE,
            HasName.PropertyName.NAME.toString(),
            null, clinic,
            ClinicPeer.NAME.getName(), new NonEmptyStringValidator(
                MSG_NO_CLINIC_NAME)));

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            HasNameShort.PropertyName.NAME_SHORT.toString(),
            null, clinic,
            ClinicPeer.NAME_SHORT.getName(), new NonEmptyStringValidator(
                MSG_NO_CLINIC_NAME));

        createBoundWidgetWithLabel(client, Button.class, SWT.CHECK,
            Clinic.Property.SENDS_SHIPMENTS.toString(),
            null, clinic,
            ClinicPeer.SENDS_SHIPMENTS.getName(), null);
        toolkit.paintBordersFor(client);

        activityStatusComboViewer =
            createComboViewer(client,
                ActivityStatus.NAME.format(1).toString(),
                ActivityStatus.valuesList(), clinic.getActivityStatus(),
                // validation error message
                i18n.tr("Clinic must have an activity status"),
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        clinic
                            .setActivityStatus((ActivityStatus) selectedObject);
                    }
                });

        createCommentSection();

    }

    @SuppressWarnings("nls")
    private void createCommentSection() {
        Composite client = createSectionWithClient(
            Comment.NAME.format(2).toString());
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
            // label
            i18n.tr("Add a Comment"), null, comment, "message", null);
    }

    @SuppressWarnings("nls")
    private void createContactSection() {
        Section section = createSection(Contact.NAME.format(2).toString());

        List<ContactWrapper> contacts =
            ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
                clinicInfo.contacts, ContactWrapper.class);

        contactEntryWidget = new ContactEntryInfoTable(section, contacts);
        contactEntryWidget.adaptToToolkit(toolkit, true);
        contactEntryWidget.addSelectionChangedListener(listener);

        addSectionToolbar(section,
            // label
            i18n.tr("Add contact"),
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
