package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;

public class ReportResultsTableWidget<T> extends ReportTableWidget<T> {
    public ReportResultsTableWidget(Composite parent, List<T> collection,
        String[] headings) {
        this(parent, collection, headings, 24);
    }

    public ReportResultsTableWidget(Composite parent, List<T> collection,
        String[] headings, int rowsPerPage) {
        super(parent, collection, headings, rowsPerPage);

        disableColumnMoving();
    }

    @Override
    public BgcLabelProvider getLabelProvider(final boolean formatNumbers) {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof Object[]) {
                    Object[] row = (Object[]) element;

                    // first column is always the id of the entity (or one of
                    // them)
                    columnIndex++;

                    if (columnIndex < row.length) {
                        Object cell = row[columnIndex];
                        if (cell == null)
                            return StringUtil.EMPTY_STRING;
                        if (formatNumbers && cell instanceof Number)
                            return NumberFormatter.format((Number) cell);
                        return cell.toString();
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

    private void disableColumnMoving() {
        for (TableColumn tableColumn : tableViewer.getTable().getColumns()) {
            tableColumn.setMoveable(false);
        }
    }
}
