package edu.ualberta.med.biobank.widgets.infotables;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.util.ListChangeEvent;
import edu.ualberta.med.biobank.common.util.ListChangeHandler;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;

public abstract class InfoTableBgrLoader<T> extends AbstractInfoTableWidget<T> {
    public static final I18n i18n = I18nFactory
        .getI18n(InfoTableBgrLoader.class);
    @SuppressWarnings("unused")
    private final Queue<ListUpdater> updateListQueue =
        new LinkedList<ListUpdater>();
    private final InfoTableListChangeHandler infoTableListChangeHandler =
        new InfoTableListChangeHandler();
    private Thread previousThread;

    public InfoTableBgrLoader(Composite parent, List<T> list,
        String[] headings, int[] columnWidths, int rowsPerPage) {
        super(parent, headings, columnWidths, rowsPerPage);
        addListChangeHandler(infoTableListChangeHandler);
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
    public synchronized void setList(List<T> collection) {
        setList(collection, null);
    }

    public synchronized void setList(final List<T> list, final T selection) {
        if (list == null) return;

        // set the list here, which sets the delegate, so events fired by the
        // delegate can be properly paid attention to or ignored.
        super.setList(list);
        updateList(list, selection);
    }

    private void updateList(List<T> list, T selection) {

        final ListUpdater updater = new ListUpdater(list, selection);
        final Thread previousThread = this.previousThread;

        resizeTable();

        Thread thread = new Thread() {
            @Override
            public void run() {
                boolean ran = false;
                while (!ran) {
                    try {
                        if (previousThread != null) {
                            // if there is a previous thread, wait until it
                            // finishes
                            previousThread.join();
                        }

                        ran = true;
                        updater.run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();
        this.previousThread = thread;
    }

    private class ListUpdater implements Runnable {
        private final List<T> list;
        private final T selection;

        private ListUpdater(List<T> list, T selection) {
            this.list = list;
            this.selection = selection;
        }

        @SuppressWarnings("nls")
        @Override
        public void run() {
            try {
                final Display display = getTableViewer()
                    .getTable().getDisplay();
                display.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (paginationRequired) {
                            showPaginationWidget();
                            paginationWidget.setPageLabelText();
                            enablePaginationWidget(false);
                        } else if (paginationWidget != null
                            && !paginationWidget.isDisposed()) {
                            paginationWidget.setVisible(false);
                        }

                        tableLoader(list, selection);

                        if (autoSizeColumns) {
                            autoSizeColumns();
                        }
                    }
                });
            } catch (Exception e) {
                BgcPlugin.openAsyncError(
                    // dialog title.
                    i18n.tr("Cannot Load Table Data"), e);
            }
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

    private class InfoTableListChangeHandler implements ListChangeHandler<T> {
        private boolean ignoreEvents = false;

        @Override
        public void onListChange(ListChangeEvent<T> event) {
            // init() may cause ListChangeEvent-s to be fired, so don't listen
            // for them when init() is called.
            if (!ignoreEvents) {
                try {
                    ignoreEvents = true;
                    List<T> list = getList();
                    init(list);
                    setPaginationParams(list);
                    updateList(list, null);
                } finally {
                    ignoreEvents = false;
                }
            }
        }
    }
}
