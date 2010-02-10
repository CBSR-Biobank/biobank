package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ContactInfoTable extends InfoTableWidget<ContactWrapper> {

    class TableRowData {
        String name;
        String title;
        String emailAddress;
        String phoneNumber;
        String faxNumber;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { name, title, emailAddress,
                phoneNumber, faxNumber }, "\t");
        }
    }

    class TableSorter extends BiobankTableSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            TableRowData c1 = (TableRowData) ((BiobankCollectionModel) e1).o;
            TableRowData c2 = (TableRowData) ((BiobankCollectionModel) e2).o;
            if ((c1 == null) || (c2 == null)) {
                return -1;
            }
            int rc = 0;
            switch (propertyIndex) {
            case 0:
                rc = c1.name.compareTo(c2.name);
                break;
            case 1:
                rc = c1.title.compareTo(c2.title);
                break;
            case 2:
                rc = c1.emailAddress.compareTo(c2.emailAddress);
                break;
            case 3:
                rc = c1.phoneNumber.compareTo(c2.phoneNumber);
                break;
            case 4:
                rc = c1.faxNumber.compareTo(c2.faxNumber);
                break;
            default:
                rc = 0;
            }
            // If descending order, flip the direction
            if (direction == 1) {
                rc = -rc;
            }
            return rc;
        }
    }

    private static final String[] HEADINGS = new String[] { "Contact Name",
        "Title", "Email", "Phone #", "Fax #" };

    private static final int[] BOUNDS = new int[] { 150, 150, 100, 100, 100 };

    public ContactInfoTable(Composite parent,
        Collection<ContactWrapper> contacts) {
        super(parent, true, contacts, HEADINGS, BOUNDS);
        setSorter(new TableSorter());
        addClipboadCopySupport();
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData contact = (TableRowData) ((BiobankCollectionModel) element).o;
                if (contact == null)
                    return null;
                switch (columnIndex) {
                case 0:
                    return contact.name;
                case 1:
                    return contact.title;
                case 2:
                    return contact.emailAddress;
                case 3:
                    return contact.phoneNumber;
                case 4:
                    return contact.faxNumber;
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(ContactWrapper contact)
        throws Exception {
        TableRowData info = new TableRowData();
        info.name = contact.getName();
        info.title = contact.getTitle();
        if (info.title == null) {
            info.title = new String();
        }
        info.emailAddress = contact.getEmailAddress();
        if (info.emailAddress == null) {
            info.emailAddress = new String();
        }
        info.phoneNumber = contact.getPhoneNumber();
        if (info.phoneNumber == null) {
            info.phoneNumber = new String();
        }
        info.faxNumber = contact.getFaxNumber();
        if (info.faxNumber == null) {
            info.faxNumber = new String();
        }
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }
}
