package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.ClinicPeer;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.entry.ContactEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicEntryForm extends AddressEntryFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ClinicEntryForm"; //$NON-NLS-1$

    private static final String MSG_NEW_CLINIC_OK = Messages
        .getString("ClinicEntryForm.creation.msg"); //$NON-NLS-1$

    private static final String MSG_CLINIC_OK = Messages
        .getString("ClinicEntryForm.msg.ok"); //$NON-NLS-1$

    private static final String MSG_NO_CLINIC_NAME = Messages
        .getString("ClinicEntryForm.msg.noClinicName"); //$NON-NLS-1$

    private ClinicAdapter clinicAdapter;

    private ClinicWrapper clinic;

    private ContactEntryInfoTable contactEntryWidget;

    protected Combo session;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    private ComboViewer activityStatusComboViewer;

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ClinicAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());
        clinicAdapter = (ClinicAdapter) adapter;
        if (clinicAdapter.getWrapper().isNew())
            clinic = clinicAdapter.getWrapper();
        else
            clinic = (ClinicWrapper) clinicAdapter.getWrapper()
                .getDatabaseClone();
        clinic.reload();

        String tabName;
        if (clinic.isNew()) {
            tabName = Messages.getString("ClinicEntryForm.title.new"); //$NON-NLS-1$
            clinic.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
        } else
            tabName = Messages.getString("ClinicEntryForm.title.edit", //$NON-NLS-1$
                clinic.getNameShort());
        setPartName(tabName);
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
        form.setText(Messages.getString("ClinicEntryForm.main.title")); //$NON-NLS-1$
        page.setLayout(new GridLayout(1, false));
        toolkit.createLabel(page,
            Messages.getString("ClinicEntryForm.main.description"), SWT.LEFT); //$NON-NLS-1$
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

        setFirstControl(createBoundWidgetWithLabel(client, BiobankText.class,
            SWT.NONE, Messages.getString("label.name"), null, clinic, //$NON-NLS-1$
            ClinicPeer.NAME.getName(), new NonEmptyStringValidator(
                MSG_NO_CLINIC_NAME)));

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
            Messages.getString("label.nameShort"), null, clinic, //$NON-NLS-1$
            ClinicPeer.NAME_SHORT.getName(), new NonEmptyStringValidator(
                MSG_NO_CLINIC_NAME));

        createBoundWidgetWithLabel(client, Button.class, SWT.CHECK,
            Messages.getString("clinic.field.label.sendsShipments"), null, //$NON-NLS-1$
            clinic, ClinicPeer.SENDS_SHIPMENTS.getName(), null);
        toolkit.paintBordersFor(client);

        activityStatusComboViewer = createComboViewer(
            client,
            Messages.getString("label.activity"), //$NON-NLS-1$
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            clinic.getActivityStatus(),
            Messages.getString("ClinicEntryForm.activity.validator.msg"), //$NON-NLS-1$
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    clinic
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            Messages.getString("label.comments"), null, clinic, //$NON-NLS-1$
            ClinicPeer.COMMENT.getName(), null);
    }

    private void createContactSection() {
        Section section = createSection(Messages
            .getString("clinic.contact.title")); //$NON-NLS-1$

        contactEntryWidget = new ContactEntryInfoTable(section, clinic);
        contactEntryWidget.adaptToToolkit(toolkit, true);
        contactEntryWidget.addSelectionChangedListener(listener);

        addSectionToolbar(section,
            Messages.getString("ClinicEntryForm.contact.button.add"), //$NON-NLS-1$
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
        clinic.addToContactCollection(contactEntryWidget
            .getAddedOrModifedContacts());
        clinic.removeFromContactCollection(contactEntryWidget
            .getDeletedContacts());
        clinic.persist();
        SessionManager.updateAllSimilarNodes(clinicAdapter, true);
    }

    @Override
    public String getNextOpenedFormID() {
        return ClinicViewForm.ID;
    }

    @Override
    public void reset() {
        super.reset();
        ActivityStatusWrapper currentActivityStatus = clinic
            .getActivityStatus();
        if (currentActivityStatus != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                currentActivityStatus));
        } else if (activityStatusComboViewer.getCombo().getItemCount() > 1) {
            activityStatusComboViewer.getCombo().deselectAll();
        }
        contactEntryWidget.reload();
    }
}
