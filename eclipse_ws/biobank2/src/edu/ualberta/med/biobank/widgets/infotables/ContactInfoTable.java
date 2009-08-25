package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.Contact;

public class ContactInfoTable extends InfoTableWidget<Contact> {

    private static final String[] headings = new String[] { "Contact Name",
        "Title", "Email", "Phone #", "Fax #" };

    private static final int[] bounds = new int[] { 150, 150, 100, 100, 100 };

    public ContactInfoTable(Composite parent, Collection<Contact> contacts) {
        super(parent, contacts, headings, bounds);
    }

}
