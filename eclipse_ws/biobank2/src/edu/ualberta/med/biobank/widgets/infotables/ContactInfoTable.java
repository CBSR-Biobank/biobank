package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ContactInfoTable extends InfoTableWidget<ContactWrapper> {

    protected class TableRowData {
        ContactWrapper contact;
        String name;
        String title;
        String studies;
        String emailAddress;
        String phoneNumber;
        String faxNumber;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { name, title, studies,
                emailAddress, phoneNumber, faxNumber }, "\t");
        }
    }

    private class TableSorter extends BiobankTableSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            TableRowData i1 = (TableRowData) ((BiobankCollectionModel) e1).o;
            TableRowData i2 = (TableRowData) ((BiobankCollectionModel) e2).o;
            if (i1 == null) {
                return -1;
            } else if (i2 == null) {
                return 1;
            }

            int rc = 0;
            switch (propertyIndex) {
            case 0:
                rc = compare(i1.name, i2.name);
                break;
            case 1:
                rc = compare(i1.title, i2.title);
                break;
            case 2:
                rc = compare(i1.studies, i2.studies);
                break;
            case 3:
                rc = compare(i1.emailAddress, i2.emailAddress);
                break;
            case 4:
                rc = compare(i1.phoneNumber, i2.phoneNumber);
                break;
            case 5:
                rc = compare(i1.faxNumber, i2.faxNumber);
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
        "Title", "Studies", "Email", "Phone #", "Fax #" };

    private static final int[] BOUNDS = new int[] { 120, 120, 200, 120, 120,
        100 };

    public ContactInfoTable(Composite parent,
        Collection<ContactWrapper> contacts) {
        super(parent, true, contacts, HEADINGS, BOUNDS);
        setSorter(new TableSorter());
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return item.name;
                case 1:
                    return item.title;
                case 2:
                    return item.studies;
                case 3:
                    return item.emailAddress;
                case 4:
                    return item.phoneNumber;
                case 5:
                    return item.faxNumber;
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(ContactWrapper contact)
        throws Exception {
        if (contact == null)
            return null;
        TableRowData info = new TableRowData();
        info.contact = contact;
        info.name = contact.getName();
        info.title = contact.getTitle();
        List<StudyWrapper> studies = contact.getStudyCollection(true);
        if (studies != null) {
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (StudyWrapper study : studies) {
                if (count > 0) {
                    sb.append(", ");
                }
                sb.append(study.getNameShort());
                ++count;
            }
            info.studies = sb.toString();
        }
        info.emailAddress = contact.getEmailAddress();
        info.phoneNumber = contact.getPhoneNumber();
        info.faxNumber = contact.getFaxNumber();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public ContactWrapper getSelection() {
        TableRowData item = (TableRowData) getSelectionInternal().o;
        Assert.isNotNull(item);
        return item.contact;
    }

    @Override
    public List<ContactWrapper> getCollection() {
        List<ContactWrapper> result = new ArrayList<ContactWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add(((TableRowData) item.o).contact);
        }
        return result;
    }
}
