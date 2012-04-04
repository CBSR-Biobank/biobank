package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.StudyContactInfoTable.ClinicContacts;

/**
 * Used to display clinic and contact information. Meant to be used by
 * StudyViewForm only.
 */
public class StudyContactInfoTable extends InfoTableWidget<ClinicContacts> {

    protected static class TableRowData {
        ClinicContacts clinic;
        String clinicNameShort;
        Long patientCount;
        Long ceventCount;
        String contactNames;

        @SuppressWarnings("nls")
        @Override
        public String toString() {
            return StringUtils.join(new String[] { clinicNameShort,
                (patientCount != null) ? patientCount.toString() : "",
                (ceventCount != null) ? ceventCount.toString() : "",
                contactNames }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] {
        "Clinic",
        "#Patients",
        "#Collection Events",
        "Contact(s)" };

    private StudyWrapper study;

    public StudyContactInfoTable(Composite parent, StudyWrapper study) {
        super(parent, null, HEADINGS, 10, ContactWrapper.class);
        this.study = study;
        setCollectionByStudy(study);
    }

    public static class ClinicContacts {
        private ClinicWrapper clinic;
        private StringBuffer contactsBuf;

        public ClinicContacts(ClinicWrapper clinic, ContactWrapper contact) {
            this.setClinic(clinic);
            contactsBuf = new StringBuffer();
            addContact(contact);
        }

        public void addContact(ContactWrapper contact) {
            if (contactsBuf.length() > 0) {
                contactsBuf.append("\n"); 
            }
            String name = contact.getName();
            if ((name != null) && !name.isEmpty()) {
                contactsBuf.append(contact.getName());
            }
            String title = contact.getTitle();
            if ((title != null) && !title.isEmpty()) {
                contactsBuf.append(" ("); 
                contactsBuf.append(title);
                contactsBuf.append(")"); 
            }
        }

        public String getFormattedContacts() {
            return contactsBuf.toString();
        }

        public ClinicWrapper getClinic() {
            return clinic;
        }

        public void setClinic(ClinicWrapper clinic) {
            this.clinic = clinic;
        }
    }

    public void setCollectionByStudy(StudyWrapper study) {
        super.setList(new ArrayList<ClinicContacts>(processClinics(study
            .getContactCollection(true))));
    }

    private Collection<ClinicContacts> processClinics(
        List<ContactWrapper> contactCollection) {
        HashMap<ClinicWrapper, ClinicContacts> tableData =
            new HashMap<ClinicWrapper, ClinicContacts>();
        for (ContactWrapper contact : contactCollection) {
            ClinicWrapper clinic = contact.getClinic();
            if (tableData.containsKey(clinic)) {
                ClinicContacts prevEntry = tableData.get(clinic);
                prevEntry.addContact(contact);
                tableData.put(clinic, prevEntry);
            } else {
                tableData.put(clinic, new ClinicContacts(clinic, contact));
            }
        }
        return tableData.values();
    }

    @Override
    public ClinicContacts getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        return ((TableRowData) item.o).clinic;
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
                        return "loading...";
                    }
                    return ""; 
                }
                switch (columnIndex) {
                case 0:
                    return item.clinicNameShort;
                case 1:
                    return NumberFormatter.format(item.patientCount);
                case 2:
                    return NumberFormatter.format(item.ceventCount);
                case 3:
                    return item.contactNames;
                default:
                    throw new IllegalArgumentException(
                        "column index is invalid " + columnIndex);
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(Object o) throws Exception {
        TableRowData info = new TableRowData();
        ClinicContacts cc = (ClinicContacts) o;
        info.clinic = cc;
        info.clinicNameShort = info.clinic.getClinic().getNameShort();
        info.patientCount =
            info.clinic.getClinic().getPatientCountForStudy(study);
        info.ceventCount =
            info.clinic.getClinic().getCollectionEventCountForStudy(study);
        info.contactNames = cc.getFormattedContacts();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}
