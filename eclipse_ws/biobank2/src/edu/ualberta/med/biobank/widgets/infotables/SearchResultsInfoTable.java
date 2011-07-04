package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SearchResultsInfoTable extends InfoTableWidget<Object> {

    public SearchResultsInfoTable(Composite parent, List<Object> collection,
        String[] headings, int rows) {
        super(parent, collection, headings, rows, Object.class);
    }

    public SearchResultsInfoTable(Composite parent, List<Object> collection,
        String[] headings) {
        super(parent, collection, headings, Object.class);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof BiobankCollectionModel) {
                    BiobankCollectionModel m = (BiobankCollectionModel) element;
                    if (m.o != null) {
                        return getColumnText(m.o, columnIndex);
                    } else if (columnIndex == 0) {
                        return "loading ...";
                    }
                } else if (element instanceof Object[]) {
                    Object[] castedVals = (Object[]) element;
                    if (castedVals[columnIndex] == null)
                        return "";
                    else {
                        if (castedVals[columnIndex] instanceof Date)
                            return DateFormatter
                                .formatAsDate((Date) castedVals[columnIndex]);
                        else
                            return castedVals[columnIndex].toString();
                    }
                }
                return "no label provider";
            }
        };
    }

    @Override
    public Object getSelection() {
        return getSelectionInternal();
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        Object[] row = (Object[]) o;
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Object field : row) {
            if (count > 0) {
                sb.append("\t");
            }
            sb.append(field.toString());
            ++count;
        }
        return sb.toString();
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}
