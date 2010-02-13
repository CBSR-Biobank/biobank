package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ClinicStudyInfoTable extends InfoTableWidget<StudyWrapper> {

    private class TableRowData {
        public StudyWrapper study;
        public String studyShortName;
        public Long patientCount;
        public Long visitCount;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { studyShortName,
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
                rc = compare(i1.studyShortName, i2.studyShortName);
                break;
            case 1:
                rc = compare(i1.patientCount, i2.patientCount);
                break;
            case 2:
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

    private static final String[] HEADINGS = new String[] { "Study",
        "No. Patients", "No. Patient Visits" };

    private static final int[] BOUNDS = new int[] { 160, 130, 100 };

    private ClinicWrapper clinic;

    public ClinicStudyInfoTable(Composite parent, ClinicWrapper clinic)
        throws Exception {
        super(parent, true, clinic.getStudyCollection(), HEADINGS, BOUNDS);
        this.clinic = clinic;
        setSorter(new TableSorter());
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null)
                    return "";

                switch (columnIndex) {
                case 0:
                    return item.studyShortName;
                case 1:
                    return (item.patientCount != null) ? item.patientCount
                        .toString() : "";
                case 2:
                    return (item.visitCount != null) ? item.visitCount
                        .toString() : "";
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(StudyWrapper study) throws Exception {
        TableRowData info = new TableRowData();
        info.study = study;
        info.studyShortName = study.getNameShort();
        if (info.studyShortName == null) {
            info.studyShortName = new String();
        }
        info.patientCount = study.getPatientCountForClinic(clinic);
        info.visitCount = study.getPatientVisitCountForClinic(clinic);
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
            TableRowData row = (TableRowData) item.o;
            Assert.isNotNull(row.study);
            result.add(row.study);
        }
        return null;
    }

    @Override
    public StudyWrapper getSelection() {
        return ((TableRowData) getSelectionInternal().o).study;
    }
}
