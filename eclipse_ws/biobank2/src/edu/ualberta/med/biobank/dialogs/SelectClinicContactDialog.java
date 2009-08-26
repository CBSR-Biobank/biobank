package edu.ualberta.med.biobank.dialogs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.widgets.infotables.ContactInfoTable;

public class SelectClinicContactDialog extends BiobankDialog {

    public static final int ADD_BTN_ID = 100;

    private static final String TITLE = "Clinic Contacts";

    private HashMap<String, Clinic> clinicMap;

    private CCombo clinicCombo;

    private ContactInfoTable contactInfoTable;

    private Contact selectedContact;

    public SelectClinicContactDialog(Shell parent,
        Collection<Clinic> clinicCollection) {
        super(parent);
        clinicMap = new HashMap<String, Clinic>();
        if (clinicCollection != null) {
            for (Clinic clinic : clinicCollection) {
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
        return contents;
    }

    private void updateTable() {
        String name = clinicCombo.getText();
        Assert.isNotNull(name, "clinic combo selection error");
        Clinic clinic = clinicMap.get(name);
        Assert.isNotNull(clinic, "no clinic with name \"" + name + "\"");
        contactInfoTable.setCollection(clinic.getContactCollection());
        contactInfoTable.setEnabled(true);
    }

    @Override
    protected void okPressed() {
        selectedContact = contactInfoTable.getSelection();
        super.okPressed();
    }

    public Contact getSelection() {
        return selectedContact;
    }

}
