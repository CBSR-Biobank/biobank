package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SearchResultsInfoTable extends InfoTableWidget<Object> {

    public SearchResultsInfoTable(Composite parent, List<Object> collection,
        String[] headings, int[] bounds) {
        super(parent, true, collection, headings, bounds, 24);
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
                    else
                        return castedVals[columnIndex].toString();
                }
                return "no label provider";
            }
        };
    }

    @Override
    protected BiobankTableSorter getTableSorter() {
        return null;
    }

    @Override
    public List<Object> getCollection() {
        return super.getCollectionInternal();
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
}
