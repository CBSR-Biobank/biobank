package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
        public Integer studyCount;
        public String activityStatus;
        public Long patientCount;
        public Integer patientVisitCount;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { clinicName,
                studyCount.toString(),
                (activityStatus != null) ? activityStatus : "",
                patientCount.toString(), patientVisitCount.toString() }, "\t");
        }
    }

    class TableSorter extends BiobankTableSorter {
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
                rc = c1.clinicName.compareTo(c2.clinicName);
                break;
            case 1:
                rc = c1.studyCount.compareTo(c2.studyCount);
                break;
            case 2:
                if (c1.activityStatus == null) {
                    rc = -1;
                } else if (c2.activityStatus == null) {
                    rc = 1;
                } else {
                    rc = c1.activityStatus.compareTo(c2.activityStatus);
                }
                break;
            case 3:
                rc = c1.patientCount.compareTo(c2.patientCount);
                break;
            case 4:
                rc = c1.patientVisitCount.compareTo(c2.patientVisitCount);
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
        "Study Count", "Status", "Patients", "Patient Visits" };

    private static final int[] BOUNDS = new int[] { 160, 130, 130, 130, 130 };

    public ClinicInfoTable(Composite parent,
        Collection<ClinicWrapper> collection) {
        super(parent, true, collection, HEADINGS, BOUNDS);
        setSorter(new TableSorter());
        addClipboadCopySupport();
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null)
                    return null;
                switch (columnIndex) {
                case 0:
                    return item.clinicName;
                case 1:
                    return item.studyCount.toString();
                case 2:
                    return item.activityStatus;
                case 3:
                    return item.patientCount.toString();
                case 4:
                    return item.patientVisitCount.toString();
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(ClinicWrapper clinic)
        throws Exception {
        TableRowData info = new TableRowData();
        info.clinicName = clinic.getName();
        List<StudyWrapper> studies = clinic.getStudyCollection();
        if (studies == null) {
            info.studyCount = 0;
        } else {
            info.studyCount = studies.size();
        }
        info.activityStatus = clinic.getActivityStatus();
        info.patientCount = clinic.getPatientCount();
        List<PatientVisitWrapper> pvs = clinic.getPatientVisitCollection();
        if (pvs == null) {
            info.patientVisitCount = 0;
        } else {
            info.patientVisitCount = pvs.size();
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
        return ((TableRowData) getSelectionInternal().o).clinic;
    }
}
