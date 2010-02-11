package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;

public class PatientVisitInfoTable extends InfoTableWidget<PatientVisitWrapper> {

    class TableRowData {
        Date dateProcessed;
        Integer sampleCount;

        @Override
        public String toString() {
            return StringUtils.join(new String[] {
                (dateProcessed != null) ? DateFormatter
                    .formatAsDateTime(dateProcessed) : "",
                (sampleCount != null) ? sampleCount.toString() : "" }, "\t");
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
                rc = c1.dateProcessed.compareTo(c2.dateProcessed);
                break;
            case 1:
                rc = c1.sampleCount.compareTo(c2.sampleCount);
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
        Collection<PatientVisitWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS);
    }

}
