package edu.ualberta.med.biobank.dialogs.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.clinic.ContactsGetAllAction;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.widgets.infotables.entry.StudyContactEntryInfoTable;

public class SelectClinicContactDialog extends BgcBaseDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(SelectClinicContactDialog.class);

    public static final int ADD_BTN_ID = 100;

    @SuppressWarnings("nls")
    // select clinic contact dialog title
    private static final String TITLE = i18n.tr("Clinic Contacts");

    private StudyContactEntryInfoTable contactInfoTable;

    private Contact selectedContact;

    private final List<Contact> excludedContacts;

    private ComboViewer clinicCombo;

    public SelectClinicContactDialog(Shell parent, List<Contact> contacts) {
        super(parent);
        this.excludedContacts = contacts;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        // select clinic contact dialog title area message
        return i18n.tr("Select a contact to add to this study");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaTitle() {
        // select clinic contact dialog title area title
        return i18n.tr("Add a clinic contact to study");
    }

    @Override
    protected void createDialogAreaInternal(final Composite parent)
        throws Exception {
        final Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        GridData cgd = new GridData(SWT.FILL, SWT.FILL, true, true);
        contents.setLayoutData(cgd);

        LabelProvider labelProvider = new LabelProvider() {
            @Override
            public String getText(Object o) {
                return ((Clinic) o).getNameShort();
            }
        };

        List<Contact> allContacts = SessionManager.getAppService().
            doAction(new ContactsGetAllAction()).getList();
        allContacts.removeAll(excludedContacts);

        HashSet<Clinic> clinics = new HashSet<Clinic>();
        for (Contact contact : allContacts) {
            clinics.add(contact.getClinic());
        }

        clinicCombo = widgetCreator.createComboViewer(contents,
            Clinic.NAME.singular().toString(),
            new ArrayList<Clinic>(clinics), null, labelProvider);
        clinicCombo
            .addSelectionChangedListener(new ISelectionChangedListener() {

                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    filterContacts((Clinic) ((StructuredSelection) event
                        .getSelection()).getFirstElement());
                    getShell().setSize(
                        contents.getParent().getParent()
                            .computeSize(SWT.DEFAULT, getShell().getSize().y));
                }
            });

        contactInfoTable = new StudyContactEntryInfoTable(contents,
            new ArrayList<Contact>());
        contactInfoTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (contactInfoTable.getSelection() != null)
                    SelectClinicContactDialog.this.getButton(
                        IDialogConstants.OK_ID).setEnabled(true);
            }
        });
        GridData gd = new GridData(SWT.FILL, SWT.NONE, true, true);
        gd.horizontalSpan = 2;
        contactInfoTable.setLayoutData(gd);
        contactInfoTable.setEnabled(true);

    }

    protected void filterContacts(Clinic clinic) {
        Collection<Contact> clinicContacts = clinic.getContacts();
        for (Contact contact : excludedContacts)
            clinicContacts.remove(contact);
        contactInfoTable.setList(new ArrayList<Contact>(clinicContacts));
    }

    @Override
    protected void okPressed() {
        selectedContact = contactInfoTable.getSelection();
        super.okPressed();
    }

    public Contact getSelection() {
        return selectedContact;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

}
