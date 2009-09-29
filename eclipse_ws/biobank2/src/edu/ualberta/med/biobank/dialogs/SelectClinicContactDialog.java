package edu.ualberta.med.biobank.dialogs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.widgets.infotables.ContactInfoTable;

public class SelectClinicContactDialog extends BiobankDialog {

    public static final int ADD_BTN_ID = 100;

    private static final String TITLE = "Clinic Contacts";

    private HashMap<String, ClinicWrapper> clinicMap;

    private CCombo clinicCombo;

    private ContactInfoTable contactInfoTable;

    private ContactWrapper selectedContact;

    public SelectClinicContactDialog(Shell parent,
        Collection<ClinicWrapper> clinicCollection) {
        super(parent);
        clinicMap = new HashMap<String, ClinicWrapper>();
        if (clinicCollection != null) {
            for (ClinicWrapper clinic : clinicCollection) {
                clinicMap.put(clinic.getName(), clinic);
            }
        }
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(TITLE);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);
        setStatusMessage("Select a clinic and then a contact");
        Composite contents = new Composite(parentComposite, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label label = new Label(contents, SWT.NONE);
        label.setText("Clinic:");
        clinicCombo = new CCombo(contents, SWT.BORDER | SWT.READ_ONLY);
        GridData gd = new GridData(SWT.LEFT, SWT.FILL, true, true);
        gd.widthHint = 250;
        clinicCombo.setLayoutData(gd);
        Set<String> sortedKeys = new TreeSet<String>(clinicMap.keySet());
        for (String stName : sortedKeys) {
            clinicCombo.add(stName);
        }
        clinicCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateTable();
            }
        });

        contactInfoTable = new ContactInfoTable(contents, null);
        contactInfoTable.setEnabled(false);
        contactInfoTable.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (contactInfoTable.getSelection() != null)
                    SelectClinicContactDialog.this.getButton(
                        IDialogConstants.OK_ID).setEnabled(true);

            }
        });
        return contents;
    }

    private void updateTable() {
        String name = clinicCombo.getText();
        Assert.isNotNull(name, "clinic combo selection error");
        ClinicWrapper clinic = clinicMap.get(name);
        Assert.isNotNull(clinic, "no clinic with name \"" + name + "\"");
        contactInfoTable.setCollection(clinic.getContactCollection());
        contactInfoTable.setEnabled(true);
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
