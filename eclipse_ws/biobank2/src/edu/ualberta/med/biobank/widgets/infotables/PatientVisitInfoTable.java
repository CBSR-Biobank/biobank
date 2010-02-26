package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class PatientVisitInfoTable extends InfoTableWidget<PatientVisitWrapper> {

    class TableRowData {
        PatientVisitWrapper visit;
        String dateProcessed;
        Integer sampleCount;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { dateProcessed,
                (sampleCount != null) ? sampleCount.toString() : "0" }, "\t");
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
                rc = compare(c1.dateProcessed, c2.dateProcessed);
                break;
            case 1:
                rc = compare(c1.sampleCount, c2.sampleCount);
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

    private static final String[] HEADINGS = new String[] { "Date processed",
        "Num Samples" };

    private static final int[] BOUNDS = new int[] { 200, 130, -1, -1, -1, -1,
        -1 };

    public PatientVisitInfoTable(Composite parent,
        List<PatientVisitWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS);
        setSorter(new TableSorter());
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
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
                    return info.dateProcessed;
                case 1:
                    return (info.sampleCount != null) ? info.sampleCount
                        .toString() : "0";

                default:
                    return "";
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(PatientVisitWrapper visit)
        throws Exception {
        TableRowData info = new TableRowData();
        info.visit = visit;
        info.dateProcessed = visit.getFormattedDateProcessed();
        List<SampleWrapper> samples = visit.getSampleCollection();
        if (samples != null) {
            info.sampleCount = samples.size();
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
    public List<PatientVisitWrapper> getCollection() {
        List<PatientVisitWrapper> result = new ArrayList<PatientVisitWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add(((TableRowData) item.o).visit);
        }
        return result;
    }

    @Override
    public PatientVisitWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.visit;
    }

}
