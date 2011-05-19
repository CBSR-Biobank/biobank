package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class StudyContactEntryInfoTable extends InfoTableWidget<ContactWrapper> {

    private static final int PAGE_SIZE_ROWS = 15;

    protected static class TableRowData {
        ContactWrapper contact;
        String clinicNameShort;
        String name;
        String title;
        String emailAddress;
        String mobileNumber;
        String pagerNumber;
        String officeNumber;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { clinicNameShort, name,
                title, emailAddress, mobileNumber, pagerNumber, officeNumber },
                "\t");
        }
    }

    private static final String[] HEADINGS = new String[] { "Clinic",
        "Contact Name", "Title", "Email", "Mobile #", "Pager #", "Office #" };

    public StudyContactEntryInfoTable(Composite parent,
        List<ContactWrapper> contactCollection) {
        super(parent, contactCollection, HEADINGS, PAGE_SIZE_ROWS);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
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
                    return item.clinicNameShort;
                case 1:
                    return item.name;
                case 2:
                    return item.title;
                case 3:
                    return item.emailAddress;
                case 4:
                    return item.mobileNumber;
                case 5:
                    return item.pagerNumber;
                case 6:
                    return item.officeNumber;
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(ContactWrapper contact)
        throws Exception {
        if (contact == null)
            return null;
        TableRowData info = new TableRowData();
        info.contact = contact;
        ClinicWrapper clinic = contact.getClinic();
        Assert.isNotNull(clinic, "contact's clinic is null");
        info.clinicNameShort = clinic.getNameShort();
        info.name = contact.getName();
        info.title = contact.getTitle();
        if (info.title == null) {
            info.title = "";
        }
        info.emailAddress = contact.getEmailAddress();
        info.mobileNumber = contact.getMobileNumber();
        info.pagerNumber = contact.getPagerNumber();
        info.officeNumber = contact.getOfficeNumber();
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

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                try {
                    TableRowData i1 = getCollectionModelObject((ContactWrapper) e1);
                    TableRowData i2 = getCollectionModelObject((ContactWrapper) e2);
                    return super
                        .compare(i1.clinicNameShort, i2.clinicNameShort);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }
}