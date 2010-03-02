package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
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
        String clinicName;
        Long patientCount;
        Long visitCount;
        String contactName;
        String contactTitle;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { clinicName,
                (patientCount != null) ? patientCount.toString() : "",
                (visitCount != null) ? visitCount.toString() : "", contactName,
                contactTitle }, "\t");

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
                rc = compare(i1.patientCount, i2.patientCount);
                break;
            case 2:
                rc = compare(i1.visitCount, i2.visitCount);
                break;
            case 3:
                rc = compare(i1.contactName, i2.contactName);
                break;
            case 4:
                rc = compare(i1.contactTitle, i2.contactTitle);
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
        "#Patients", "#Patient Visits", "Contact Name", "Title" };

    private static final int[] BOUNDS = new int[] { 100, 80, 100, 150, 150 };

    private StudyWrapper study;

    public StudyContactInfoTable(Composite parent, StudyWrapper study) {
        super(parent, true, study.getContactCollection(), HEADINGS, BOUNDS, 10);
        this.study = study;
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
                    return item.clinicName;
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
    protected BiobankTableSorter getTableSorter() {
        return new TableSorter();
    }

    @Override
    public Object getCollectionModelObject(ContactWrapper contact)
        throws Exception {
        TableRowData info = new TableRowData();
        info.contact = contact;
        ClinicWrapper clinic = contact.getClinic();
        if (clinic != null) {
            info.clinicName = clinic.getName();
            info.patientCount = study.getPatientCountForClinic(clinic);
            info.visitCount = study.getPatientVisitCountForClinic(clinic);
        }
        info.contactName = contact.getName();
        info.contactTitle = contact.getTitle();
        return info;
    }

    @Override
    public List<ContactWrapper> getCollection() {
        List<ContactWrapper> result = new ArrayList<ContactWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add(((TableRowData) item.o).contact);
        }
        return result;
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
}
