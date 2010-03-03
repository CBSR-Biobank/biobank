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

public class PatientInfoTable extends InfoTableWidget<PatientWrapper> {

    class TableRowData {
        PatientWrapper patient;
        String pnumber;
        String studyNameShort;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { pnumber, studyNameShort },
                "\t");
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
                rc = compare(c1.pnumber, c2.pnumber);
                break;
            case 1:
                rc = compare(c1.studyNameShort, c2.studyNameShort);
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

    private static final String[] HEADINGS = new String[] { "Patient Number",
        "Study" };

    private static final int[] BOUNDS = new int[] { 150, 150, -1, -1, -1, -1 };

    public PatientInfoTable(Composite parent, boolean multiSelectRows,
        List<PatientWrapper> patients) {
        super(parent, multiSelectRows, patients, HEADINGS, BOUNDS, 10);
    }

    public PatientInfoTable(Composite parent, List<PatientWrapper> patients) {
        super(parent, true, patients, HEADINGS, BOUNDS, 10);
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
                    return item.pnumber;
                case 1:
                    return item.studyNameShort;
                case 3:
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
    public Object getCollectionModelObject(PatientWrapper patient)
        throws Exception {
        TableRowData info = new TableRowData();
        info.patient = patient;
        info.pnumber = patient.getPnumber();
        StudyWrapper study = patient.getStudy();
        if (study != null) {
            info.studyNameShort = study.getNameShort();
        } else {
            info.studyNameShort = new String();
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
    public List<PatientWrapper> getCollection() {
        List<PatientWrapper> result = new ArrayList<PatientWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add(((TableRowData) item.o).patient);
        }
        return result;
    }

    @Override
    public PatientWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.patient;
    }
}
