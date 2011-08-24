package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.util.AdapterFactory;

/**
 * Used to display tabular information for an object in the object model or
 * combined information from several objects in the object model.
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
public abstract class InfoTableWidget<T> extends AbstractInfoTableWidget<T> {

    /*
     * see http://lekkimworld.com/2008/03/27/setting_table_row_height_in_swt
     * .html for how to set row height.
     */

    private static BgcLogger logger = BgcLogger.getLogger(InfoTableWidget.class
        .getName());

    protected List<BiobankCollectionModel> model;

    protected ListenerList addItemListeners = new ListenerList();

    protected ListenerList editItemListeners = new ListenerList();

    protected ListenerList deleteItemListeners = new ListenerList();

    protected ListenerList doubleClickListeners = new ListenerList();

    private MenuItem editItem;

    private final Class<?> wrapperClass;

    public InfoTableWidget(Composite parent, List<T> collection,
        String[] headings, Class<T> wrapperClass) {
        super(parent, collection, headings, null, 5);
        this.wrapperClass = wrapperClass;
        addTableClickListener();
    }

    public InfoTableWidget(Composite parent, List<T> collection,
        String[] headings, int rowsPerPage, Class<?> wrapperClass) {
        super(parent, collection, headings, null, rowsPerPage);
        this.wrapperClass = wrapperClass;
        addTableClickListener();
    }

    private void addTableClickListener() {
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                if (doubleClickListeners.size() > 0) {
                    InfoTableWidget.this.doubleClick();
                }
            }
        });
        menu.addMenuListener(new MenuAdapter() {

            @Override
            public void menuShown(MenuEvent e) {
                if (editItem == null)
                    return;

                if (getSelection() instanceof ModelWrapper<?>
                    && !(getSelection() instanceof DispatchSpecimenWrapper))
                    editItem.setEnabled(((ModelWrapper<?>) getSelection())
                        .canUpdate(SessionManager.getUser()));
                else
                    editItem.setEnabled(false);
            }
        });
    }

    @Override
    protected void init(List<T> collection) {
        reloadData = true;

        model = new ArrayList<BiobankCollectionModel>();
        initModel(collection);
    }

    @Override
    protected void setPaginationParams(List<T> collection) {
        if (pageInfo.rowsPerPage != 0
            && (collection.size() > pageInfo.rowsPerPage)) {
            Double size = new Double(collection.size());
            Double pageSize = new Double(pageInfo.rowsPerPage);
            pageInfo.pageTotal = Double.valueOf(Math.ceil(size / pageSize))
                .intValue();
            paginationRequired = true;
            if (pageInfo.page == pageInfo.pageTotal)
                pageInfo.page--;
            getTableViewer().refresh();
        } else
            paginationRequired = false;
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

    protected void initModel(List<T> collection) {
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
    public boolean setFocus() {
        tableViewer.getControl().setFocus();
        return true;
    }

    /**
     * Should be used by info tables that allow editing of data. Use this method
     * instead of setCollection().
     * 
     * @param collection
     */
    public void reloadCollection(final List<T> collection, T selection) {
        reloadData = true;
        setCollection(collection, selection);
    }

    public void reloadCollection(final List<T> collection) {
        reloadData = true;
        setCollection(collection, null);
    }

    @Override
    protected void tableLoader(final List<T> collection, final T selection) {
        final TableViewer viewer = getTableViewer();
        final Table table = viewer.getTable();
        Display display = viewer.getTable().getDisplay();

        initModel(collection);

        if (paginationRequired) {
            start = pageInfo.page * pageInfo.rowsPerPage;
            end = Math.min(start + pageInfo.rowsPerPage, model.size());
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

    @SuppressWarnings("unused")
    public Object getCollectionModelObject(T item) throws Exception {
        return item;
    }

    public Object getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        Object object = item.o;
        Assert.isNotNull(object);
        return object;
    }

    public void addClickListener(IDoubleClickListener listener) {
        doubleClickListeners.add(listener);
        if (SessionManager.canUpdate(wrapperClass)) {
            editItem = new MenuItem(getMenu(), SWT.PUSH);
            editItem.setText(Messages.InfoTableWidget_edit_label);
            editItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    ModelWrapper<?> selection = (ModelWrapper<?>) InfoTableWidget.this
                        .getSelection();
                    if (selection != null) {
                        AdapterBase adapter = AdapterFactory
                            .getAdapter(selection);
                        adapter.openEntryForm();
                    }
                }
            });
        }
    }

    public void doubleClick() {
        // get selection as derived class object
        Object selection = getSelection();

        final DoubleClickEvent event = new DoubleClickEvent(tableViewer,
            new InfoTableSelection(selection));
        Object[] listeners = doubleClickListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            final IDoubleClickListener l = (IDoubleClickListener) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                @Override
                public void run() {
                    l.doubleClick(event);
                }
            });
        }
    }

    public void addAddItemListener(IInfoTableAddItemListener listener) {
        addItemListeners.add(listener);

        Assert.isNotNull(menu);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.InfoTableWidget_add_label);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                addItem();
            }
        });
    }

    public void addEditItemListener(IInfoTableEditItemListener listener) {
        editItemListeners.add(listener);

        Assert.isNotNull(menu);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.InfoTableWidget_edit_label);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                editItem();
            }
        });
    }

    public void addDeleteItemListener(IInfoTableDeleteItemListener listener) {
        deleteItemListeners.add(listener);

        Assert.isNotNull(menu);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.InfoTableWidget_delete_label);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                deleteItem();
            }
        });
    }

    protected void addItem() {
        InfoTableSelection selection = new InfoTableSelection(getSelection());
        final InfoTableEvent event = new InfoTableEvent(this, selection);
        Object[] listeners = addItemListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            final IInfoTableAddItemListener l = (IInfoTableAddItemListener) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                @Override
                public void run() {
                    l.addItem(event);
                }
            });
        }
    }

    protected void editItem() {
        InfoTableSelection selection = new InfoTableSelection(getSelection());
        final InfoTableEvent event = new InfoTableEvent(this, selection);
        Object[] listeners = editItemListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            final IInfoTableEditItemListener l = (IInfoTableEditItemListener) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                @Override
                public void run() {
                    l.editItem(event);
                }
            });
        }
    }

    protected void deleteItem() {
        InfoTableSelection selection = new InfoTableSelection(getSelection());
        final InfoTableEvent event = new InfoTableEvent(this, selection);
        Object[] listeners = deleteItemListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            final IInfoTableDeleteItemListener l = (IInfoTableDeleteItemListener) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                @Override
                public void run() {
                    l.deleteItem(event);
                }
            });
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

        if (enable && (pageInfo.page < pageInfo.pageTotal - 1)) {
            lastButton.setEnabled(true);
            nextButton.setEnabled(true);
        } else {
            lastButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
    }

    @Override
    protected void setDefaultWidgetsEnabled() {
        firstButton.setEnabled(false);
        prevButton.setEnabled(false);
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
    protected void lastPage() {
        pageInfo.page = pageInfo.pageTotal - 1;
        firstButton.setEnabled(true);
        prevButton.setEnabled(true);
        lastButton.setEnabled(false);
        nextButton.setEnabled(false);
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
        if (pageInfo.page == pageInfo.pageTotal - 2) {
            lastButton.setEnabled(true);
            nextButton.setEnabled(true);
        }
    }

    @Override
    protected void nextPage() {
        if (pageInfo.page >= pageInfo.pageTotal)
            return;
        pageInfo.page++;
        if (pageInfo.page == 1) {
            firstButton.setEnabled(true);
            prevButton.setEnabled(true);
        }
        if (pageInfo.page == pageInfo.pageTotal - 1) {
            lastButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
    }

    @Override
    protected void setPageLabelText() {
        pageLabel.setText(NLS.bind(Messages.InfoTableWidget_pages_label,
            (pageInfo.page + 1), +pageInfo.pageTotal));
    }

}
