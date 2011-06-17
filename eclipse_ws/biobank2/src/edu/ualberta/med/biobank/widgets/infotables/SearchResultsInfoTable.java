package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SearchResultsInfoTable extends InfoTableWidget<Object> {

    public SearchResultsInfoTable(Composite parent, List<Object> collection,
        String[] headings, int rows) {
        super(parent, collection, headings, rows);
    }

    public SearchResultsInfoTable(Composite parent, List<Object> collection,
        String[] headings) {
        super(parent, collection, headings);
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
                        return Messages.SearchResultsInfoTable_loading;
                    }
                } else if (element instanceof Object[]) {
                    Object[] castedVals = (Object[]) element;
                    if (castedVals[columnIndex] == null)
                        return ""; //$NON-NLS-1$
                    else {
                        if (castedVals[columnIndex] instanceof Date)
                            return DateFormatter
                                .formatAsDate((Date) castedVals[columnIndex]);
                        else
                            return castedVals[columnIndex].toString();
                    }
                }
                return "no label provider"; //$NON-NLS-1$
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
                sb.append("\t"); //$NON-NLS-1$
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
