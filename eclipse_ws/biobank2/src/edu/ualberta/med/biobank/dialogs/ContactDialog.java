package edu.ualberta.med.biobank.dialogs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;

public class ContactDialog extends BiobankDialog {

    private static final String TITLE = "Contact Information";

    private Contact contact;

    private HashMap<String, Clinic> clinicMap;

    private CCombo clinicCombo;

    public ContactDialog(Shell parent, Contact contact,
        Collection<Clinic> clinicCollection) {
        super(parent);
        Assert.isNotNull(contact);
        Assert.isNotNull(clinicCollection);
        this.contact = contact;

        clinicMap = new HashMap<String, Clinic>();
        for (Clinic clinic : clinicCollection) {
            clinicMap.put(clinic.getName(), clinic);
        }
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        String title = new String();
        Integer id = contact.getId();

        if (id == null) {
            title = "Add";
        } else {
            title = "Edit ";
        }
        title += TITLE;
        shell.setText(title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);
        Composite contents = new Composite(parentComposite, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label label = new Label(contents, SWT.NONE);
        label.setText("Sample Type:");
        clinicCombo = new CCombo(contents, SWT.BORDER | SWT.READ_ONLY);
        Set<String> sortedKeys = new TreeSet<String>(clinicMap.keySet());
        for (String stName : sortedKeys) {
            clinicCombo.add(stName);
        }

        Clinic c = contact.getClinic();
        if (c != null) {
            clinicCombo.setText(c.getName());
        }
        clinicCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String clinicName = clinicCombo.getText();
                if (clinicName != null) {
                    Clinic clinic = clinicMap.get(clinicName);
                    Assert.isNotNull(clinic, "clinic is null");
                    contact.setClinic(clinic);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }

        });

        createBoundWidgetWithLabel(contents, Text.class, SWT.BORDER, "Name",
            new String[0], PojoObservables.observeValue(contact, "name"), null,
            null);

        createBoundWidgetWithLabel(contents, Text.class, SWT.BORDER, "Title",
            new String[0], PojoObservables.observeValue(contact, "title"),
            null, null);

        createBoundWidgetWithLabel(contents, Text.class, SWT.BORDER, "email",
            new String[0], PojoObservables
                .observeValue(contact, "emailAddress"), null, null);

        createBoundWidgetWithLabel(contents, Text.class, SWT.BORDER, "Phone #",
            new String[0],
            PojoObservables.observeValue(contact, "phoneNumber"), null, null);

        createBoundWidgetWithLabel(contents, Text.class, SWT.BORDER, "Fax #",
            new String[0], PojoObservables.observeValue(contact, "faxNumber"),
            null, null);

        return contents;
    }

    public Contact getContact() {
        return contact;
    }
}