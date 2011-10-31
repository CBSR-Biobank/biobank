package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;
import edu.ualberta.med.biobank.gui.common.widgets.Messages;

public abstract class InfoTableBgrLoader<T> extends AbstractInfoTableWidget<T> {
    protected Thread backgroundThread;

    public InfoTableBgrLoader(Composite parent, List<T> list,
        String[] headings, int[] columnWidths, int rowsPerPage) {
        super(parent, headings, columnWidths, rowsPerPage);
        setList(list);
    }

    /**
     * This method is used to load object model data in the background thread.
     * 
     * @param item the model object representing the base object to get
     *            information from.
     * @return an non-object model object with the table data.
     * 
     * @throws Exception
     */
    protected abstract void tableLoader(final List<T> list, final T Selection);

    @Override
    public void setList(List<T> collection) {
        setList(collection, null);
    }

    public void setList(final List<T> list, final T selection) {
        try {
            if ((list == null)
                || ((backgroundThread != null) && backgroundThread.isAlive())) {
                return;
            } else if (getList() != list) {
                super.setList(list);
                init(list);
                setPaginationParams(list);
            }
            if (paginationRequired) {
                showPaginationWidget();
                paginationWidget.setPageLabelText();
                enablePaginationWidget(false);
            } else if (paginationWidget != null) {
                paginationWidget.setVisible(false);
            }
            final Display display = getTableViewer().getTable().getDisplay();

            resizeTable();
            backgroundThread = new Thread() {
                @Override
                public void run() {
                    tableLoader(list, selection);
                    if (autoSizeColumns) {
                        display.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                autoSizeColumns();
                            }
                        });
                    }
                }
            };
            backgroundThread.start();
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.AbstractInfoTableWidget_load_error_title, e);
        }
    }

    protected abstract void init(List<T> list);

    protected abstract void setPaginationParams(List<T> list);

    @Override
    protected BgcTableSorter getTableSorter() {
        // sorting not supported on background loading tables because
        // not all rows have been populated on the client side
        return null;
    }

    @Override
    public void firstPage() {
        setList(getList());
    }

    @Override
    public void lastPage() {
        setList(getList());
    }

    @Override
    public void prevPage() {
        setList(getList());
    }

    @Override
    public void nextPage() {
        setList(getList());
    }

    @Override
    public void reload() {
        setList(getList());
    }

}
