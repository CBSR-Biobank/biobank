package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class StudyContactEntryInfoTable extends InfoTableWidget<ContactWrapper> {

    protected class TableRowData {
        ContactWrapper contact;
        String clinicName;
        String name;
        String title;
        String emailAddress;
        String phoneNumber;
        String faxNumber;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { clinicName, name, title,
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
                rc = compare(i1.clinicName, i2.clinicName);
                break;
            case 1:
                rc = compare(i1.name, i2.name);
                break;
            case 2:
                rc = compare(i1.title, i2.title);
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

    private static final String[] HEADINGS = new String[] { "Clinic",
        "Contact Name", "Title", "Email", "Phone #", "Fax #" };

    private static final int[] BOUNDS = new int[] { 100, 150, 150, 200, 100,
        100 };

    public StudyContactEntryInfoTable(Composite parent,
        Collection<ContactWrapper> contactCollection) {
        super(parent, contactCollection, HEADINGS, BOUNDS);
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
                    return item.clinicName;
                case 1:
                    return item.name;
                case 2:
                    return item.title;
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
        ClinicWrapper clinic = contact.getClinic();
        Assert.isNotNull(clinic, "contact's clinic is null");
        info.clinicName = clinic.getName();
        info.name = contact.getName();
        info.title = contact.getTitle();
        if (info.title == null) {
            info.title = new String();
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
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.contact;
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
