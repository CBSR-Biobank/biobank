package edu.ualberta.med.biobank.widgets.infotables;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

/**
 * Used to display clinic and contact information. Meant to be used by
 * StudyViewForm only.
 */
public class StudyContactInfoTable extends InfoTableWidget<ContactWrapper> {

    protected class TableRowData {
        ContactWrapper contact;
        String clinicNameShort;
        Long patientCount;
        Long visitCount;
        String contactName;
        String contactTitle;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { clinicNameShort,
                (patientCount != null) ? patientCount.toString() : "",
                (visitCount != null) ? visitCount.toString() : "", contactName,
                contactTitle }, "\t");

        }
    }

    private static final String[] HEADINGS = new String[] { "Clinic",
        "#Patients", "#Patient Visits", "Contact Name", "Title" };

    private StudyWrapper study;

    public StudyContactInfoTable(Composite parent, StudyWrapper study) {
        super(parent, null, HEADINGS, 10);
        this.study = study;
        this.setCollection(study.getContactCollection());
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
                    return (item.patientCount != null) ? item.patientCount
                        .toString() : "";
                case 2:
                    return (item.visitCount != null) ? item.visitCount
                        .toString() : "";
                case 3:
                    return item.contactName;
                case 4:
                    return item.contactTitle;
                default:
                    return "";
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
            info.patientCount = study.getPatientCountForClinic(clinic);
            info.visitCount = study.getPatientVisitCountForClinic(clinic);
        }
        info.contactName = contact.getName();
        info.contactTitle = contact.getTitle();
        return info;
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
