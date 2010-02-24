package edu.ualberta.med.biobank.dialogs;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.infotables.StudyContactEntryInfoTable;

public class SelectClinicContactDialog extends TitleAreaDialog {

    private static Logger LOGGER = Logger
        .getLogger(SelectClinicContactDialog.class.getName());

    public static final int ADD_BTN_ID = 100;

    private static final String TITLE = "Clinic Contacts";

    private StudyContactEntryInfoTable contactInfoTable;

    private ContactWrapper selectedContact;

    private StudyWrapper study;

    public SelectClinicContactDialog(Shell parent, StudyWrapper study) {
        super(parent);
        this.study = study;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(TITLE);
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitle("Add a clinic contact to study");
        setMessage("Select a contact to add to this study");
        return contents;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);
        Composite contents = new Composite(parentComposite, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        contactInfoTable = new StudyContactEntryInfoTable(contents, null);
        contactInfoTable.setEnabled(false);
        contactInfoTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (contactInfoTable.getSelection() != null)
                    SelectClinicContactDialog.this.getButton(
                        IDialogConstants.OK_ID).setEnabled(true);
            }
        });

        try {
            contactInfoTable.setCollection(study.getContactsNotAssoc());
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (Exception e) {
            LOGGER.error("BioBankFormBase.createPartControl Error", e);
        }

        contactInfoTable.setEnabled(true);
        return contents;
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
