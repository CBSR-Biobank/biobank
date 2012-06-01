package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import edu.ualberta.med.biobank.gui.common.BgcLogger;

/**
 * Used to display tabular information for a class in the object model or
 * combined information from several classes in the object model.
 * <p>
 * The information in the table is loaded in a background thread. By loading
 * object model data in a background thread, the main UI thread is not blocked
 * when displaying the cells of the table.
 * <p>
 * This widget supports the following listeners: double click listener, edit
 * listener, and delete listener. The double click listener is invoked when the
 * user double clicks on a row in the table. The edit and delete listeners are
 * invoked via the table's context menu. When one of these listeners is
 * registered, the widget adds an "Edit" and / or "Delete" item to the context
 * menu. The corresponding listener is then invoked when the user selects either
 * one of the two menu choices. The event passed to the listener contains the
 * current selection for the table.
 * <p>
 * This widget also allows for a row of information to be copied to the
 * clipboard. The "Copy" command is made available in the context menu. When
 * this command is selected by the user the rows that are currently selected are
 * copied to the clipboard.
 * <p>
 * If neither the edit or delete listeners are registered, then the table is
 * configured to be in multi select mode and the selection of multiple lines is
 * available to the user.
 * <p>
 * NOTE:
 * <p>
 * Care should be taken in the label provider so that blocking calls are not
 * made to the object model. All calls to the object model should be done in
 * abstract method getCollectionModelObject().
 * 
 * @param <T> The model object wrapper the table is based on.
 * 
 */
public abstract class InfoTableWidget<T> extends InfoTableBgrLoader<T> {

    /*
     * see http://lekkimworld.com/2008/03/27/setting_table_row_height_in_swt
     * .html for how to set row height.
     */

    private static BgcLogger logger = BgcLogger.getLogger(InfoTableWidget.class
        .getName());

    protected List<BiobankCollectionModel> model;

    @SuppressWarnings("unused")
    public InfoTableWidget(Composite parent, List<T> collection,
        String[] headings, int rowsPerPage, Class<?> wrapperClass) {
        super(parent, collection, headings, null, rowsPerPage);
    }

    public InfoTableWidget(Composite parent, List<T> list,
        String[] headings, Class<?> wrapperClass) {
        this(parent, list, headings, 5, wrapperClass);
    }

    @Override
    protected void init(List<T> list) {
        reloadData = true;

        model = new ArrayList<BiobankCollectionModel>();
        initModel(list);
        tableViewer.refresh();
    }

    @Override
    protected void setPaginationParams(List<T> collection) {
        paginationRequired = paginationWidget
            .setTableMaxRows(collection.size());
        if (paginationRequired) {
            getTableViewer().refresh();
        }
    }

    /**
     * Derived classes should override this method if info table support editing
     * of items in the table.
     * 
     * @return true if editing is allowed.
     */
    @Override
    protected boolean isEditMode() {
        return false;
    }

    protected BiobankCollectionModel getSelectionInternal() {
        Assert.isTrue(!tableViewer.getTable().isDisposed(),
            "widget is disposed"); //$NON-NLS-1$
        IStructuredSelection stSelection = (IStructuredSelection) tableViewer
            .getSelection();

        return (BiobankCollectionModel) stSelection.getFirstElement();
    }

    protected void initModel(List<?> collection) {
        if ((collection == null) || (model.size() == collection.size()))
            return;

        BiobankTableSorter comparator = getComparator();
        if (comparator != null)
            Collections.sort(collection, comparator);

        model.clear();
        for (int i = 0, n = collection.size(); i < n; ++i) {
            model.add(new BiobankCollectionModel(i));
        }

    }

    protected abstract BiobankTableSorter getComparator();

    protected abstract String getCollectionModelObjectToString(Object o);

    @Override
    protected void tableLoader(final List<T> collection, final T selection) {
        final TableViewer viewer = getTableViewer();
        final Table table = viewer.getTable();

        if (table.isDisposed()) return;

        Display display = viewer.getTable().getDisplay();

        initModel(collection);

        if (paginationRequired) {
            int rowsPerPage = paginationWidget.getRowsPerPage();
            start = paginationWidget.getCurrentPage() * rowsPerPage;
            end = Math.min(start + rowsPerPage, model.size());
        } else {
            start = 0;
            end = model.size();
        }

        final List<BiobankCollectionModel> modelSubList = model.subList(start,
            end);

        display.syncExec(new Runnable() {
            @Override
            public void run() {
                if (!table.isDisposed()) {
                    tableViewer.setInput(modelSubList);
                }
            }
        });

        try {
            BiobankCollectionModel selItem = null;
            for (int i = start; i < end; ++i) {
                if (table.isDisposed())
                    return;
                final BiobankCollectionModel item = model.get(i);
                Assert.isNotNull(item != null);
                if (reloadData || (item.o == null)) {
                    item.o = getCollectionModelObject(collection
                        .get(item.index));
                }

                display.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (!table.isDisposed()) {
                            viewer.refresh(item, false);
                        }
                    }
                });

                if ((selection != null) && selection.equals(item.o)) {
                    selItem = item;
                }
            }
            reloadData = false;

            final BiobankCollectionModel selectedItem = selItem;
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

    public Object getCollectionModelObject(Object item) throws Exception {
        return item;
    }

}
