package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
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
                rc = compare(i1.name, i2.name);
                break;
            case 1:
                rc = compare(i1.nameShort, i2.nameShort);
                break;
            case 2:
                rc = compare(i1.status, i2.status);
                break;
            case 3:
                rc = compare(i1.patientCount, i2.patientCount);
                break;
            case 4:
                rc = compare(i1.visitCount, i2.visitCount);
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

    private static final String[] HEADINGS = new String[] { "Name",
        "Short Name", "Status", "Patients", "Patient Visits" };

    private static final int[] BOUNDS = new int[] { 260, 130, 130, 130, 130 };

    public StudyInfoTable(Composite parent, List<StudyWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS, 10);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return info.name;
                case 1:
                    return info.nameShort;
                case 2:
                    return (info.status != null) ? info.status : "";
                case 3:
                    return (info.patientCount != null) ? info.patientCount
                        .toString() : "";
                case 4:
                    return (info.visitCount != null) ? info.visitCount
                        .toString() : "";
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
    public Object getCollectionModelObject(StudyWrapper study) throws Exception {
        TableRowData info = new TableRowData();
        info.study = study;
        info.name = study.getName();
        info.nameShort = study.getNameShort();
        info.status = study.getActivityStatus().getName();
        if (info.status == null) {
            info.status = new String();
        }
        List<PatientWrapper> patients = study.getPatientCollection();
        if (patients != null) {
            info.patientCount = patients.size();
        }
        info.visitCount = study.getPatientVisitCount();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public List<StudyWrapper> getCollection() {
        List<StudyWrapper> result = new ArrayList<StudyWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add(((TableRowData) item.o).study);
        }
        return result;
    }

    @Override
    public StudyWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.study;
    }

}
