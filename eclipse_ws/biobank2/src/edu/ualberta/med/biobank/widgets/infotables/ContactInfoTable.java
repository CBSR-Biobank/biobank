package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContactInfoTable extends InfoTableWidget<ContactWrapper> {

    private static final int PAGE_SIZE_ROWS = 5;

    protected static class TableRowData {
        ContactWrapper contact;
        public String name;
        public String title;
        public String studies;
        public String emailAddress;
        public String mobileNumber;
        public String pagerNumber;
        public String officeNumber;
        public String faxNumber;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { name, title, studies,
                emailAddress, mobileNumber, pagerNumber, officeNumber,
                faxNumber }, "\t"); //$NON-NLS-1$
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.ContactInfoTable_name_label,
        Messages.ContactInfoTable_title_label,
        Messages.ContactInfoTable_studies_label,
        Messages.ContactInfoTable_email_label,
        Messages.ContactInfoTable_mobile_label,
        Messages.ContactInfoTable_pager_label,
        Messages.ContactInfoTable_office_label,
        Messages.ContactInfoTable_fax_label };

    public ContactInfoTable(Composite parent, List<ContactWrapper> contacts) {
        super(parent, contacts, HEADINGS, PAGE_SIZE_ROWS, ContactWrapper.class);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item =
                    (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return Messages.infotable_loading_msg;
                    }
                    return ""; //$NON-NLS-1$
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
                    return item.mobileNumber;
                case 5:
                    return item.pagerNumber;
                case 6:
                    return item.officeNumber;
                case 7:
                    return item.faxNumber;
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
        info.name = info.contact.getName();
        info.title = info.contact.getTitle();
        List<StudyWrapper> studies = info.contact.getStudyCollection(true);
        if (studies != null) {
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (StudyWrapper study : studies) {
                if (count > 0) {
                    sb.append(", "); //$NON-NLS-1$
                }
                sb.append(study.getNameShort());
                ++count;
            }
            info.studies = sb.toString();
        }
        info.emailAddress = info.contact.getEmailAddress();
        info.mobileNumber = info.contact.getMobileNumber();
        info.pagerNumber = info.contact.getPagerNumber();
        info.officeNumber = info.contact.getOfficeNumber();
        info.faxNumber = info.contact.getFaxNumber();
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
    protected BiobankTableSorter getComparator() {
        return null;
    }

    @Override
    protected Boolean canEdit(ContactWrapper target)
        throws ApplicationException {
        return true;
    }

    @Override
    protected Boolean canDelete(ContactWrapper target)
        throws ApplicationException {
        return true;
    }

    @Override
    protected Boolean canView(ContactWrapper target)
        throws ApplicationException {
        return true;
    }

}
