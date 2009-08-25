package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.model.Contact;

public class ContactAddDialog extends BiobankDialog {

    private static final String TITLE = "Contact Information";

    private Contact contact;

    public ContactAddDialog(Shell parent) {
        this(parent, new Contact());
    }

    public ContactAddDialog(Shell parent, Contact contact) {
        super(parent);
        Assert.isNotNull(contact);
        this.contact = contact;
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

        Control c = createBoundWidgetWithLabel(contents, Text.class,
            SWT.BORDER, "Name", new String[0], PojoObservables.observeValue(
                contact, "name"), null, null);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 250;
        c.setLayoutData(gd);

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