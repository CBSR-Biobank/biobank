package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SearchResultsInfoTable extends InfoTableWidget<Object> {

    public SearchResultsInfoTable(Composite parent,
        Collection<Object> collection, String[] headings, int[] bounds) {
        super(parent, false, collection, headings, bounds);
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
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
    public List<Object> getCollection() {
        return super.getCollectionInternal();
    }

    @Override
    public Object getSelection() {
        return getSelectionInternal();
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        return o.toString();
    }
}
