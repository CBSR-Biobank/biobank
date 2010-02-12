package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class StudyInfoTable extends InfoTableWidget<StudyWrapper> {

    protected class TableRowData {
        StudyWrapper study;
        String name;
        String nameShort;
        String status;
        Integer patientCount;
        Long visitCount;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { name, nameShort, status,
                (patientCount != null) ? patientCount.toString() : "",
                (visitCount != null) ? visitCount.toString() : "" }, "\t");
        }
    }

    private class TableSorter extends BiobankTableSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            TableRowData c1 = (TableRowData) ((BiobankCollectionModel) e1).o;
            TableRowData c2 = (TableRowData) ((BiobankCollectionModel) e2).o;
            if ((c1 == null) || (c2 == null)) {
                return -1;
            }
            int rc = 0;
            switch (propertyIndex) {
            case 0:
                rc = c1.name.compareTo(c2.name);
                break;
            case 1:
                rc = c1.nameShort.compareTo(c2.nameShort);
                break;
            case 2:
                rc = c1.status.compareTo(c2.status);
                break;
            case 3:
                if (c1.patientCount == null) {
                    rc = -1;
                } else if (c2.patientCount == null) {
                    rc = 1;
                } else {
                    rc = c1.patientCount.compareTo(c2.patientCount);
                }
                break;
            case 4:
                if (c1.visitCount == null) {
                    rc = -1;
                } else if (c2.visitCount == null) {
                    rc = 1;
                } else {
                    rc = c1.visitCount.compareTo(c2.visitCount);
                }
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

    private static final String[] HEADINGS = new String[] { "Name",
        "Short Name", "Status", "Patients", "Patient Visits" };

    private static final int[] BOUNDS = new int[] { 160, 130, 130, 130, 130 };

    public StudyInfoTable(Composite parent, Collection<StudyWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS);
        setSorter(new TableSorter());
        addClipboadCopySupport();
    }

    @Override
    public Object getCollectionModelObject(StudyWrapper study) throws Exception {
        TableRowData info = new TableRowData();
        info.study = study;
        info.name = study.getName();
        info.nameShort = study.getNameShort();
        info.status = study.getActivityStatus();
        if (info.status == null) {
            info.status = new String();
        }
        info.patientCount = study.getPatientCollection().size();
        info.visitCount = study.getPatientVisitCount();
        return info;
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
                    return item.nameShort;
                case 2:
                    return item.status;
                case 3:
                    return item.patientCount.toString();
                case 4:
                    return item.visitCount.toString();
                default:
                    return "";
                }
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public List<StudyWrapper> getCollection() {
        return null;
    }

    @Override
    public StudyWrapper getSelection() {
        return ((TableRowData) getSelectionInternal().o).study;
    }

}
