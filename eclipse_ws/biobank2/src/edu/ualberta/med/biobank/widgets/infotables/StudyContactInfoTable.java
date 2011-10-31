package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
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
        ClinicWrapper clinic;
        String clinicNameShort;
        Long patientCount;
        Long ceventCount;
        String contactNames;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { clinicNameShort,
                (patientCount != null) ? patientCount.toString() : "", //$NON-NLS-1$
                (ceventCount != null) ? ceventCount.toString() : "", //$NON-NLS-1$
                contactNames }, "\t"); //$NON-NLS-1$

        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.StudyContactInfoTable_clinic_label,
        Messages.StudyContactInfoTable_patient_count_label,
        Messages.StudyContactInfoTable_cEvent_count_label,
        Messages.StudyContactInfoTable_contact_name_label };

    private StudyWrapper study;

    public StudyContactInfoTable(Composite parent, StudyWrapper study) {
        super(parent, null, HEADINGS, 10, ContactWrapper.class);
        this.study = study;
        setCollectionByStudy(study);
    }

    public static class ClinicContacts {
        public ClinicWrapper clinic;
        public String contacts;

        public ClinicContacts(ClinicWrapper clinic, String contacts) {
            this.clinic = clinic;
            this.contacts = contacts;
        }
    }

    public void setCollectionByStudy(StudyWrapper study) {
        super.setList(new ArrayList<ClinicContacts>(processClinics(study
            .getContactCollection(true))));
    }

    private Collection<ClinicContacts> processClinics(
        List<ContactWrapper> contactCollection) {
        HashMap<ClinicWrapper, ClinicContacts> tableData = new HashMap<ClinicWrapper, ClinicContacts>();
        for (ContactWrapper contact : contactCollection) {
            ClinicWrapper clinic = contact.getClinic();
            if (tableData.containsKey(clinic)) {
                ClinicContacts prevEntry = tableData.get(clinic);
                ClinicContacts newEntry = new ClinicContacts(
                    clinic,
                    prevEntry.contacts + ";" //$NON-NLS-1$
                        + (contact.getName() == null ? "" : contact.getName()) //$NON-NLS-1$
                        + (contact.getTitle() == null ? "" : "(" + contact.getTitle() + ")")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                tableData.put(clinic, newEntry);
            } else
                tableData.put(clinic,
                    new ClinicContacts(clinic, (contact.getName() == null ? "" //$NON-NLS-1$
                        : contact.getName())
                        + (contact.getTitle() == null ? "" : "(" //$NON-NLS-1$ //$NON-NLS-2$
                            + contact.getTitle() + ")"))); //$NON-NLS-1$
        }
        return tableData.values();
    }

    @Override
    public ClinicWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.clinic;
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return Messages.StudyContactInfoTable_loading;
                    }
                    return ""; //$NON-NLS-1$
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
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(Object o) throws Exception {
        TableRowData info = new TableRowData();
        ClinicContacts cc = (ClinicContacts) o;
        info.clinic = cc.clinic;
        info.clinicNameShort = info.clinic.getNameShort();
        info.patientCount = info.clinic.getPatientCountForStudy(study);
        info.ceventCount = info.clinic.getCollectionEventCountForStudy(study);
        info.contactNames = cc.contacts;
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
