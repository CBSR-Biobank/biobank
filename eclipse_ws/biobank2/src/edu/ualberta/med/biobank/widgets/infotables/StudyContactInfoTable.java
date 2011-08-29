package edu.ualberta.med.biobank.widgets.infotables;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;

/**
 * Used to display clinic and contact information. Meant to be used by
 * StudyViewForm only.
 */
public class StudyContactInfoTable extends InfoTableWidget<ContactWrapper> {

    protected static class TableRowData {
        ContactWrapper contact;
        String clinicNameShort;
        Long patientCount;
        Long ceventCount;
        String contactName;
        String contactTitle;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { clinicNameShort,
                (patientCount != null) ? patientCount.toString() : "", //$NON-NLS-1$
                (ceventCount != null) ? ceventCount.toString() : "", //$NON-NLS-1$
                contactName, contactTitle }, "\t"); //$NON-NLS-1$

        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.StudyContactInfoTable_clinic_label,
        Messages.StudyContactInfoTable_patient_count_label,
        Messages.StudyContactInfoTable_cEvent_count_label,
        Messages.StudyContactInfoTable_contact_name_label,
        Messages.StudyContactInfoTable_contact_title_label };

    private StudyWrapper study;

    public StudyContactInfoTable(Composite parent, StudyWrapper study) {
        super(parent, null, HEADINGS, 10, ContactWrapper.class);
        this.study = study;
        this.setCollection(study.getContactCollection(true));
    }

    @Override
    public ClinicWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.contact.getClinic();
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
                    return item.contactName;
                case 4:
                    return item.contactTitle;
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(ContactWrapper contact)
        throws Exception {
        TableRowData info = new TableRowData();
        info.contact = contact;
        ClinicWrapper clinic = contact.getClinic();
        if (clinic != null) {
            info.clinicNameShort = clinic.getNameShort();
            info.patientCount = clinic.getPatientCountForStudy(study);
            info.ceventCount = clinic.getCollectionEventCountForStudy(study);
        }
        info.contactName = contact.getName();
        info.contactTitle = contact.getTitle();
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
