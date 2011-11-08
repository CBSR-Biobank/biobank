package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableWidget;
import edu.ualberta.med.biobank.widgets.infotables.Messages;

public class StudyContactEntryInfoTable extends InfoTableWidget<ContactWrapper> {

    private static final int PAGE_SIZE_ROWS = 10;

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
                "\t"); //$NON-NLS-1$
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.StudyContactEntryInfoTable_clinic_label,
        Messages.StudyContactEntryInfoTable_name_label,
        Messages.StudyContactEntryInfoTable_title_label,
        Messages.StudyContactEntryInfoTable_email_label,
        Messages.StudyContactEntryInfoTable_mobile_label,
        Messages.StudyContactEntryInfoTable_pager_label,
        Messages.StudyContactEntryInfoTable_office_label };

    public StudyContactEntryInfoTable(Composite parent,
        List<ContactWrapper> contactCollection) {
        super(parent, contactCollection, HEADINGS, PAGE_SIZE_ROWS,
            ContactWrapper.class);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return Messages.infotable_loading_msg;
                    }
                    return ""; //$NON-NLS-1$
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
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(Object o) throws Exception {
        if (o == null)
            return null;
        TableRowData info = new TableRowData();
        info.contact = (ContactWrapper) o;
        ClinicWrapper clinic = info.contact.getClinic();
        Assert.isNotNull(clinic, "contact's clinic is null"); //$NON-NLS-1$
        info.clinicNameShort = clinic.getNameShort();
        info.name = info.contact.getName();
        info.title = info.contact.getTitle();
        if (info.title == null) {
            info.title = ""; //$NON-NLS-1$
        }
        info.emailAddress = info.contact.getEmailAddress();
        info.mobileNumber = info.contact.getMobileNumber();
        info.pagerNumber = info.contact.getPagerNumber();
        info.officeNumber = info.contact.getOfficeNumber();
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
                    TableRowData i1 = getCollectionModelObject(e1);
                    TableRowData i2 = getCollectionModelObject(e2);
                    return super
                        .compare(i1.clinicNameShort, i2.clinicNameShort);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }
}