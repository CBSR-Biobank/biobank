package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;

public class ContactInfoTable extends InfoTableWidget<ContactWrapper> {

    private static final String[] HEADINGS = new String[] { "Contact Name",
        "Title", "Email", "Phone #", "Fax #" };

    private static final int[] BOUNDS = new int[] { 150, 150, 100, 100, 100 };

    public ContactInfoTable(Composite parent,
        Collection<ContactWrapper> contacts) {
        super(parent, contacts, HEADINGS, BOUNDS);
    }

}
