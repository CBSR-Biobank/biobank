package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.AbstractBiobankListProxy;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ReportTableWidget<T> extends AbstractInfoTableWidget<T> {

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
    protected void setDefaultWidgetsEnabled() {
        lastButton.setEnabled(false);
    }

    @Override
    protected void setPaginationParams(List<T> collection) {
        if (collection != null
            && (collection.size() == -1 || collection.size() > pageInfo.rowsPerPage)) {
            if (collection.get(pageInfo.rowsPerPage) != null) {
                paginationRequired = true;
                init(collection);
            }
        } else
            paginationRequired = false;
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
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
                        else
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

        init(collection);

        display.syncExec(new Runnable() {
            @Override
            public void run() {
                if (!table.isDisposed()) {
                    if (paginationRequired) {
                        setPageLabelText();
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
    protected void enableWidgets(boolean enable) {
        if (enable && (pageInfo.page > 0)) {
            firstButton.setEnabled(true);
            prevButton.setEnabled(true);
        } else {
            firstButton.setEnabled(false);
            prevButton.setEnabled(false);
        }

        if (!enable) {
            nextButton.setEnabled(false);
            lastButton.setEnabled(false);
        } else {
            if (pageInfo.pageTotal == 0) {
                nextButton.setEnabled(true);
            } else if (pageInfo.page < pageInfo.pageTotal - 1) {
                lastButton.setEnabled(true);
                nextButton.setEnabled(true);
            }
        }

    }

    @Override
    protected void firstPage() {
        pageInfo.page = 0;
    }

    @Override
    protected void prevPage() {
        if (pageInfo.page == 0)
            return;
        pageInfo.page--;
    }

    @Override
    protected void nextPage() {
        pageInfo.page++;
    }

    @Override
    protected void lastPage() {
        pageInfo.page = pageInfo.pageTotal - 1;
    }

    @Override
    protected void setPageLabelText() {
        String total;
        if (pageInfo.pageTotal > 0)
            total = String.valueOf(pageInfo.pageTotal);
        else
            total = "?"; //$NON-NLS-1$
        pageLabel.setText(NLS.bind(Messages.ReportTableWidget_pages_label, (pageInfo.page + 1),
            total));
    }

    @Override
    protected void init(List<T> collection) {
        if (pageInfo.pageTotal == 0) {
            if (collection instanceof AbstractBiobankListProxy) {
                int realSize = ((AbstractBiobankListProxy<?>) collection)
                    .getRealSize();
                if (realSize != -1)
                    pageInfo.pageTotal = (realSize - 1) / pageInfo.rowsPerPage
                        + 1;
            } else
                pageInfo.pageTotal = (collection.size() - 1)
                    / pageInfo.rowsPerPage + 1;
        }
    }

    @Override
    protected boolean isEditMode() {
        return false;
    }

}
