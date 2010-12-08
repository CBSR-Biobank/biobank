package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ReportResultsTableWidget<T> extends ReportTableWidget<T> {
    private String[] headings;

    public ReportResultsTableWidget(Composite parent, List<T> collection,
        String[] headings) {
        this(parent, collection, headings, 24);
        this.headings = headings;
    }

    public ReportResultsTableWidget(Composite parent, List<T> collection,
        String[] headings, int rowsPerPage) {
        super(parent, collection, headings, rowsPerPage);
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof Object[]) {
                    Object[] row = (Object[]) element;

                    // first column is always the id of the entity (or one of
                    // them)
                    columnIndex++;

                    if (columnIndex < row.length) {
                        if (row[columnIndex] == null)
                            return "";
                        else {
                            return row[columnIndex].toString();
                        }
                    }
                }
                return element.toString();
            }
        };
    }

    public void addDoubleClickListener(IDoubleClickListener listener) {
        tableViewer.addDoubleClickListener(listener);
    }

    public void removeDoubleClickListener(IDoubleClickListener listener) {
        tableViewer.removeDoubleClickListener(listener);
    }

    @Override
    protected boolean isEditMode() {
        return false;
    }
}
