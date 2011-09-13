package edu.ualberta.med.biobank.dialogs.select;

import java.util.ArrayList;
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

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.widgets.infotables.entry.StudyContactEntryInfoTable;

public class SelectClinicContactDialog extends BgcBaseDialog {

    public static final int ADD_BTN_ID = 100;

    private static final String TITLE = Messages.SelectClinicContactDialog_dialog_title;

    private StudyContactEntryInfoTable contactInfoTable;

    private ContactWrapper selectedContact;

    private List<ContactWrapper> contacts;

    private ComboViewer clinicCombo;

    public SelectClinicContactDialog(Shell parent, List<ContactWrapper> contacts) {
        super(parent);
        this.contacts = contacts;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.SelectClinicContactDialog_description;
    }

    @Override
    protected String getTitleAreaTitle() {
        return Messages.SelectClinicContactDialog_main_title;
    }

    @Override
    protected void createDialogAreaInternal(final Composite parent)
        throws Exception {
        final Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        GridData cgd = new GridData(SWT.FILL, SWT.FILL, true, true);
        contents.setLayoutData(cgd);

        HashSet<ClinicWrapper> clinics = new HashSet<ClinicWrapper>();
        for (ContactWrapper contact : contacts)
            clinics.add(contact.getClinic());

        LabelProvider labelProvider = new LabelProvider() {
            @Override
            public String getText(Object o) {
                return ((ClinicWrapper) o).getNameShort();
            }
        };

        clinicCombo = widgetCreator.createComboViewer(contents,
            Messages.SelectClinicContactDialog_clinic_label,
            new ArrayList<ClinicWrapper>(clinics), null, labelProvider);
        clinicCombo
            .addSelectionChangedListener(new ISelectionChangedListener() {

                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    filterContacts((ClinicWrapper) ((StructuredSelection) event
                        .getSelection()).getFirstElement());
                    getShell().setSize(
                        contents.getParent().getParent()
                            .computeSize(SWT.DEFAULT, getShell().getSize().y));
                }
            });

        contactInfoTable = new StudyContactEntryInfoTable(contents,
            new ArrayList<ContactWrapper>());
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

    protected void filterContacts(ClinicWrapper clinic) {
        List<ContactWrapper> clinicContacts = clinic.getContactCollection(true);
        for (ContactWrapper contact : contacts)
            clinicContacts.remove(contact);
        contactInfoTable.setCollection(clinicContacts);
    }

    @Override
    protected void okPressed() {
        selectedContact = contactInfoTable.getSelection();
        super.okPressed();
    }

    public ContactWrapper getSelection() {
        return selectedContact;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

}
