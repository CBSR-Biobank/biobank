package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.util.AbstractBiobankListProxy;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTablePaginationWidget;

public class ReportTableWidget<T> extends InfoTableBgrLoader<T> {

    private static BgcLogger logger = BgcLogger
        .getLogger(ReportTableWidget.class.getName());

    public ReportTableWidget(Composite parent, List<T> collection,
        String[] headings) {
        super(parent, collection, headings, null, 24);
    }

    public ReportTableWidget(Composite parent, List<T> collection,
        String[] headings, int rowsPerPage) {
        super(parent, collection, headings, null, rowsPerPage);
    }

    @Override
    protected void setPaginationParams(List<T> collection) {
        int rowsPerPage = paginationWidget.getRowsPerPage();
        if (collection != null
            && (collection.size() == -1 || collection.size() > rowsPerPage)) {
            if (collection.get(rowsPerPage) != null) {
                paginationRequired = true;
                init(collection);
            }
        } else
            paginationRequired = false;
    }

    @Override
    public BgcLabelProvider getLabelProvider() {
        return getLabelProvider(true);
    }

    public BgcLabelProvider getLabelProvider(final boolean formatNumbers) {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof Object[]) {
                    Object[] castedVals = (Object[]) element;
                    if (castedVals[columnIndex] == null)
                        return ""; //$NON-NLS-1$
                    else {
                        if (castedVals[columnIndex] instanceof Date)
                            return DateFormatter
                                .formatAsDate((Date) castedVals[columnIndex]);
                        if (formatNumbers
                            && castedVals[columnIndex] instanceof Number)
                            return NumberFormatter
                                .format((Number) castedVals[columnIndex]);
                        return castedVals[columnIndex].toString();
                    }
                }
                return "no label provider"; //$NON-NLS-1$
            }
        };
    }

    @Override
    public void tableLoader(final List<T> collection, final T selection) {
        final TableViewer viewer = getTableViewer();
        final Table table = viewer.getTable();
        Display display = viewer.getTable().getDisplay();

        int rowsPerPage = paginationWidget.getRowsPerPage();
        if (paginationRequired) {
            start = paginationWidget.getCurrentPage() * rowsPerPage;
            end = start + rowsPerPage;
        } else {
            start = 0;
            end = rowsPerPage;
        }
        final Collection<T> collSubList;

        // if we are not dealing with a biobanklistproxy we need to
        // check for bounds
        if (collection.size() != -1) {
            start = Math.min(start, collection.size());
            end = Math.min(end, collection.size());
        }
        collSubList = collection.subList(start, end);

        init(collection);

        display.syncExec(new Runnable() {
            @Override
            public void run() {
                if (!table.isDisposed()) {
                    if (paginationRequired) {
                        paginationWidget.setPageLabelText();
                        ReportTableWidget.this.getShell().layout(true, true);
                    }
                    tableViewer.setInput(collSubList);
                }
            }
        });

        try {
            Object selItem = null;
            Iterator<T> it = collSubList.iterator();
            for (int i = start; i < end && it.hasNext(); ++i) {
                if (table.isDisposed())
                    return;
                final Object item = it.next();
                if (item == null) {
                    end = i;
                    break;
                }
                display.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (!table.isDisposed()) {
                            viewer.refresh(item, false);
                        }
                    }
                });

                if ((selection != null) && selection.equals(item)) {
                    selItem = item;
                }
            }

            final Object selectedItem = selItem;
            display.syncExec(new Runnable() {
                @Override
                public void run() {
                    if (!table.isDisposed()) {
                        if (paginationRequired) {
                            enablePaginationWidget(true);
                        }

                        if (selectedItem != null) {
                            tableViewer.setSelection(new StructuredSelection(
                                selectedItem));
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error("setCollection error", e); //$NON-NLS-1$
        }

    }

    @Override
    protected void init(List<T> collection) {
        if (paginationWidget.getTotalPages() == InfoTablePaginationWidget.TOTAL_PAGES_UNKNOWN) {
            int size;
            if (collection instanceof AbstractBiobankListProxy) {
                size = ((AbstractBiobankListProxy<?>) collection).getRealSize();
            } else {
                size = collection.size();
            }

            if (size > 0) {
                paginationWidget.setTableMaxRows(size);
            }
        }
    }

    @Override
    protected boolean isEditMode() {
        return false;
    }

}
