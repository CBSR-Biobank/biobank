package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ReportTableWidget<T> extends AbstractInfoTableWidget<T> {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ReportTableWidget.class.getName());

    public ReportTableWidget(Composite parent, List<T> collection,
        String[] headings, int[] columnWidths) {
        super(parent, collection, headings, columnWidths, 40, false);
    }

    public ReportTableWidget(Composite parent, List<T> collection,
        String[] headings, int[] columnWidths, int rowsPerPage) {
        super(parent, collection, headings, columnWidths, rowsPerPage, false);
    }

    @Override
    protected void setDefaultWidgetsEnabled() {
        lastButton.setEnabled(false);
    }

    @Override
    protected void setPaginationParams(List<T> collection) {
        if (collection != null
            && (collection.size() == -1 || collection.size() > pageInfo.rowsPerPage)) {
            if (collection.get(pageInfo.rowsPerPage) != null) {
                paginationRequired = true;
                addPaginationWidget();
            }
        } else
            paginationRequired = false;
    }

    @Override
    protected IBaseLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof Object[]) {
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
    public void tableLoader(final List<T> collection, final T selection) {
        final TableViewer viewer = getTableViewer();
        final Table table = viewer.getTable();
        Display display = viewer.getTable().getDisplay();

        if (paginationRequired) {
            start = pageInfo.page * pageInfo.rowsPerPage;
            end = start + pageInfo.rowsPerPage;
        } else {
            start = 0;
            end = pageInfo.rowsPerPage;
        }
        final Collection<T> collSubList;

        // if we are not dealing with a biobanklistproxy we need to
        // check for bounds
        if (collection.size() != -1) {
            start = Math.min(start, collection.size());
            end = Math.min(end, collection.size());
        }
        collSubList = collection.subList(start, end);

        display.syncExec(new Runnable() {
            public void run() {
                if (!table.isDisposed()) {
                    tableViewer.setInput(collSubList);
                }
            }
        });

        try {
            Object selItem = null;
            for (int i = start; i < end; ++i) {
                if (table.isDisposed())
                    return;
                final Object item = collection.get(i);
                if (item == null) {
                    end = i;
                    break;
                }
                display.syncExec(new Runnable() {
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
            logger.error("setCollection error", e);
        }
    }

    @Override
    protected void enableWidgets(boolean enable) {
        if (enable && (pageInfo.page > 0)) {
            firstButton.setEnabled(true);
            prevButton.setEnabled(true);
        } else {
            firstButton.setEnabled(false);
            prevButton.setEnabled(false);
        }

        if (!enable
            || getTableViewer().getTable().getItemCount() < pageInfo.rowsPerPage) {
            nextButton.setEnabled(false);
            lastButton.setEnabled(false);
        } else {
            lastButton.setEnabled(false);
            nextButton.setEnabled(true);
        }
    }

    @Override
    protected void firstPage() {
        pageInfo.page = 0;
        firstButton.setEnabled(false);
        prevButton.setEnabled(false);
        lastButton.setEnabled(true);
        nextButton.setEnabled(true);
    }

    @Override
    protected void prevPage() {
        if (pageInfo.page == 0)
            return;
        pageInfo.page--;
        if (pageInfo.page == 0) {
            firstButton.setEnabled(false);
            prevButton.setEnabled(false);
        }
    }

    @Override
    protected void nextPage() {
        pageInfo.page++;
        if (pageInfo.page == 1) {
            firstButton.setEnabled(true);
            prevButton.setEnabled(true);
        }
    }

    @Override
    protected void lastPage() {
    }

    @Override
    protected void setPageLabelText() {
        pageLabel.setText("Page: " + (pageInfo.page + 1) + " of " + "?");
    }

    @Override
    protected void init(List<T> collection) {

    }

    @Override
    protected boolean isEditMode() {
        return false;
    }

}
