package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
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

    private static final String[] HEADINGS = new String[] { "Date processed",
        "Num Samples" };

    public PatientVisitInfoTable(Composite parent,
        List<PatientVisitWrapper> collection) {
        super(parent, collection, HEADINGS, 10);
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
        List<AliquotWrapper> samples = visit.getAliquotCollection();
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
    public PatientVisitWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.visit;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

}
