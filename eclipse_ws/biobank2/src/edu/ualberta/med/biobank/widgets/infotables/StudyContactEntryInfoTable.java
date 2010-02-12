package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class StudyContactEntryInfoTable extends InfoTableWidget<ContactWrapper> {

    private static final String[] HEADINGS = new String[] { "Clinic",
        "Contact Name", "Title", "Email", "Phone #", "Fax #" };

    private static final int[] BOUNDS = new int[] { 100, 150, 150, 200, 100,
        100 };

    public StudyContactEntryInfoTable(Composite parent,
        Collection<ContactWrapper> contactCollection) {
        super(parent, contactCollection, HEADINGS, BOUNDS);
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof ContactWrapper) {
                    ContactWrapper contact = (ContactWrapper) element;
                    switch (columnIndex) {
                    case 0:
                        return contact.getClinic().getName();
                    case 1:
                        return contact.getName();
                    case 2:
                        return contact.getTitle();
                    case 3:
                        return contact.getEmailAddress();
                    case 4:
                        return contact.getPhoneNumber();
                    case 5:
                        return contact.getFaxNumber();
                    }
                }
                return super.getColumnText(element, columnIndex);
            }
        };
    }

    @Override
    public List<ContactWrapper> getCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ContactWrapper getSelection() {
        // TODO Auto-generated method stub
        return null;
    }
}
