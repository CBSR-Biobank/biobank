package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class PatientVisitInfoTable extends InfoTableWidget<ProcessingEventWrapper> {

    class TableRowData {
        ProcessingEventWrapper visit;
        String dateProcessed;
        String dateDrawn;
        Integer sampleCount;
        String comment;

        @Override
        public String toString() {
            return StringUtils.join(
                new String[] { dateProcessed, dateDrawn,
                    (sampleCount != null) ? sampleCount.toString() : "0",
                    comment }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] { "Date processed",
        "Date Drawn", "Num Samples", "Comment" };

    public PatientVisitInfoTable(Composite parent,
        List<ProcessingEventWrapper> collection) {
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
                    return info.dateDrawn;
                case 2:
                    return (info.sampleCount != null) ? info.sampleCount
                        .toString() : "0";
                case 3:
                    return info.comment;

                default:
                    return "";
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(ProcessingEventWrapper visit)
        throws Exception {
        TableRowData info = new TableRowData();
        info.visit = visit;
        info.dateProcessed = visit.getFormattedDateProcessed();
        info.dateDrawn = visit.getFormattedDateDrawn();
        List<AliquotWrapper> samples = visit.getAliquotCollection();
        if (samples != null) {
            info.sampleCount = samples.size();
        }
        info.comment = visit.getComment();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public ProcessingEventWrapper getSelection() {
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
