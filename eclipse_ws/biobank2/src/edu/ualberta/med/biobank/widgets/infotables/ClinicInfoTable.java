package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ClinicInfoTable extends InfoTableWidget<ClinicWrapper> {

    class TableRowData {
        public ClinicWrapper clinic;
        public String clinicName;
        public String clinicNameShort;
        public Integer studyCount;
        public String status;
        public Long patientCount;
        public Integer visitCount;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { clinicName, clinicNameShort,
                studyCount.toString(), (status != null) ? status : "",
                (patientCount != null) ? patientCount.toString() : "",
                (visitCount != null) ? visitCount.toString() : "" }, "\t");
        }
    }

    class TableSorter extends BiobankTableSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            TableRowData i1 = (TableRowData) ((BiobankCollectionModel) e1).o;
            TableRowData i2 = (TableRowData) ((BiobankCollectionModel) e2).o;
            if ((i1 == null) || (i2 == null)) {
                return -1;
            }
            int rc = 0;
            switch (propertyIndex) {
            case 0:
                rc = compare(i1.clinicName, i2.clinicName);
                break;
            case 1:
                rc = compare(i1.clinicNameShort, i2.clinicNameShort);
                break;
            case 2:
                rc = compare(i1.studyCount, i2.studyCount);
                break;
            case 3:
                rc = compare(i1.status, i2.status);
                break;
            case 4:
                rc = compare(i1.patientCount, i2.patientCount);
                break;
            case 5:
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
        "Short name", "Study Count", "Status", "Patients", "Patient Visits" };

    private static final int[] BOUNDS = new int[] { 180, 130, 130, 130, 130,
        130 };

    public ClinicInfoTable(Composite parent, List<ClinicWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS, 10);
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
                    return item.clinicNameShort;
                case 2:
                    return item.studyCount.toString();
                case 3:
                    return item.status;
                case 4:
                    return item.patientCount.toString();
                case 5:
                    return item.visitCount.toString();
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
    public Object getCollectionModelObject(ClinicWrapper clinic)
        throws Exception {
        TableRowData info = new TableRowData();
        info.clinic = clinic;
        info.clinicName = clinic.getName();
        info.clinicNameShort = clinic.getNameShort();
        List<StudyWrapper> studies = clinic.getStudyCollection();
        if (studies == null) {
            info.studyCount = 0;
        } else {
            info.studyCount = studies.size();
        }
        info.status = clinic.getActivityStatus().getName();
        info.patientCount = clinic.getPatientCount();
        List<PatientVisitWrapper> pvs = clinic.getPatientVisitCollection();
        if (pvs == null) {
            info.visitCount = 0;
        } else {
            info.visitCount = pvs.size();
        }
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public List<ClinicWrapper> getCollection() {
        List<ClinicWrapper> result = new ArrayList<ClinicWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add(((TableRowData) item.o).clinic);
        }
        return result;
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

    /*
     * @Override public void setSelection(ClinicWrapper item) {
     * BiobankCollectionModel modelItem = null; for (BiobankCollectionModel m :
     * model) { if (item.equals(m.o)) { modelItem = m; break; } } if (modelItem
     * == null) return;
     * 
     * tableViewer.setSelection(new StructuredSelection(modelItem)); }
     */
}
