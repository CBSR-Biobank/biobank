package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.BiobankWidget;

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
public abstract class InfoTableWidget<T> extends BiobankWidget {

    /*
     * see http://lekkimworld.com/2008/03/27/setting_table_row_height_in_swt
     * .html for how to set row height.
     */

    private static BiobankLogger logger = BiobankLogger
        .getLogger(InfoTableWidget.class.getName());

    protected TableViewer tableViewer;

    protected List<BiobankCollectionModel> model;

    private Thread backgroundThread;

    protected Menu menu;

    protected ListenerList addItemListeners = new ListenerList();

    protected ListenerList editItemListeners = new ListenerList();

    protected ListenerList deleteItemListeners = new ListenerList();

    protected ListenerList doubleClickListeners = new ListenerList();

    private boolean paginationRequired;

    private Composite paginationWidget;

    protected PageInformation pageInfo = new PageInformation();

    private Button firstButton;

    private Button lastButton;

    private Button prevButton;

    private Button nextButton;

    private Label pageLabel;

    private List<T> collection;

    public InfoTableWidget(Composite parent, boolean multilineSelection,
        List<T> collection, String[] headings, int[] columnWidths) {
        super(parent, SWT.NONE);

        pageInfo.rowsPerPage = 0;
        GridLayout gl = new GridLayout(1, false);
        gl.verticalSpacing = 1;
        setLayout(gl);
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
            | SWT.FULL_SELECTION | SWT.VIRTUAL;
        if (multilineSelection) {
            style |= SWT.MULTI;
        }

        tableViewer = new TableViewer(this, style);

        Table table = tableViewer.getTable();
        table.setLayout(new TableLayout());
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 100;
        table.setLayoutData(gd);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        int index = 0;
        if (headings != null) {
            for (String name : headings) {
                final TableViewerColumn col = new TableViewerColumn(
                    tableViewer, SWT.NONE);
                col.getColumn().setText(name);
                if (columnWidths == null || columnWidths[index] == -1) {
                    col.getColumn().pack();
                } else {
                    col.getColumn().setWidth(columnWidths[index]);
                }
                col.getColumn().setResizable(true);
                col.getColumn().setMoveable(true);
                col.getColumn().addListener(SWT.SELECTED, new Listener() {
                    public void handleEvent(Event event) {
                        col.getColumn().pack();
                    }
                });
                index++;
            }
            tableViewer.setColumnProperties(headings);
        }
        tableViewer.setUseHashlookup(true);
        tableViewer.setLabelProvider(getLabelProvider());
        tableViewer.setContentProvider(new ArrayContentProvider());

        menu = new Menu(parent);
        tableViewer.getTable().setMenu(menu);

        model = new ArrayList<BiobankCollectionModel>();

        if (collection != null) {
            initModel(collection);
            getTableViewer().refresh();
            setCollection(collection);
        }

        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                if (doubleClickListeners.size() > 0) {
                    InfoTableWidget.this.doubleClick();
                }
            }
        });
        addClipboadCopySupport();
    }

    public InfoTableWidget(Composite parent, List<T> collection,
        String[] headings, int[] columnWidths) {
        this(parent, false, collection, headings, columnWidths);
    }

    public InfoTableWidget(Composite parent, boolean multilineSelection,
        List<T> collection, String[] headings, int[] columnWidths,
        int rowsPerPage) {
        this(parent, multilineSelection, null, headings, columnWidths);
        pageInfo.rowsPerPage = rowsPerPage;
        addPaginationWidget(this);
        if (collection != null) {
            initModel(collection);
            setCollection(collection);
        }
        Table table = getTableViewer().getTable();
        GridData gd = (GridData) table.getLayoutData();
        gd.heightHint = (table.getItemHeight() + 2) * rowsPerPage;
        table.setLayoutData(gd);
    }

    private void initModel(List<T> collection) {
        if ((collection == null) || (model.size() == collection.size()))
            return;

        for (int i = 0, n = collection.size(); i < n; ++i) {
            model.add(new BiobankCollectionModel(i));
        }
    }

    private void addClipboadCopySupport() {
        Assert.isNotNull(menu);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Copy");
        item.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public void widgetSelected(SelectionEvent event) {
                List<BiobankCollectionModel> selectedRows = new ArrayList<BiobankCollectionModel>();
                IStructuredSelection sel = (IStructuredSelection) tableViewer
                    .getSelection();
                for (Iterator<BiobankCollectionModel> iterator = sel.iterator(); iterator
                    .hasNext();) {
                    selectedRows.add(iterator.next());
                }
                StringBuilder sb = new StringBuilder();
                for (BiobankCollectionModel row : selectedRows) {
                    if (sb.length() != 0) {
                        sb.append(System.getProperty("line.separator"));
                    }
                    sb.append(getCollectionModelObjectToString(row.o));
                }
                TextTransfer textTransfer = TextTransfer.getInstance();
                Clipboard cb = new Clipboard(Display.getDefault());
                cb.setContents(new Object[] { sb.toString() },
                    new Transfer[] { textTransfer });
            }
        });
    }

    protected abstract String getCollectionModelObjectToString(Object o);

    protected void setSorter(final BiobankTableSorter tableSorter) {
        tableViewer.setSorter(tableSorter);
        final Table table = tableViewer.getTable();
        for (int i = 0, n = table.getColumnCount(); i < n; ++i) {
            final TableColumn col = table.getColumn(i);
            final int index = i;
            col.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    tableSorter.setColumn(index);
                    int dir = table.getSortDirection();
                    if (table.getSortColumn() == col) {
                        dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
                    } else {
                        dir = SWT.DOWN;
                    }
                    table.setSortDirection(dir);
                    table.setSortColumn(col);
                    tableViewer.refresh();
                }
            });
        }
    }

    protected void sortOnFirstColumn() {
        Table table = tableViewer.getTable();
        table.setSortDirection(SWT.DOWN);
        table.setSortColumn(table.getColumn(0));
        tableViewer.refresh();
    }

    public abstract BiobankLabelProvider getLabelProvider();

    @Override
    public boolean setFocus() {
        tableViewer.getControl().setFocus();
        return true;
    }

    public void addSelectionListener(SelectionListener listener) {
        tableViewer.getTable().addSelectionListener(listener);
    }

    protected TableViewer getTableViewer() {
        return tableViewer;
    }

    public void setCollection(final List<T> collection) {
        this.collection = collection;
        if ((collection == null)
            || ((backgroundThread != null) && backgroundThread.isAlive())) {
            return;
        }

        if ((pageInfo.rowsPerPage != 0)
            && (collection.size() > pageInfo.rowsPerPage)
            && !paginationWidget.getVisible()) {
            pageInfo.page = 0;
            pageInfo.pageTotal = collection.size() / pageInfo.rowsPerPage + 1;
            enablePaginationWidget();
            setPageLabelText();
            paginationRequired = true;
        }

        backgroundThread = new Thread() {
            @Override
            public void run() {
                final TableViewer viewer = getTableViewer();
                final Table table = viewer.getTable();
                Display display = viewer.getTable().getDisplay();
                int start;
                int end;

                initModel(collection);
                if (paginationRequired) {
                    start = pageInfo.page * pageInfo.rowsPerPage;
                    end = Math.min(start + pageInfo.rowsPerPage, model.size());
                } else {
                    start = 0;
                    end = model.size();
                }

                final List<BiobankCollectionModel> modelSubList = model
                    .subList(start, end);

                display.syncExec(new Runnable() {
                    public void run() {
                        if (!table.isDisposed()) {
                            tableViewer.setInput(modelSubList);
                        }
                    }
                });

                try {

                    for (int i = start; i < end; ++i) {
                        if (table.isDisposed())
                            return;
                        final BiobankCollectionModel item = model.get(i);
                        Assert.isNotNull(item != null);
                        if (item.o == null) {
                            item.o = getCollectionModelObject(collection
                                .get(item.index));
                        }

                        display.syncExec(new Runnable() {
                            public void run() {
                                if (!table.isDisposed()) {
                                    viewer.refresh(item, false);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    logger.error("setCollection error", e);
                }
            }

        };
        backgroundThread.start();
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
    @SuppressWarnings("unused")
    public Object getCollectionModelObject(T item) throws Exception {
        return item;
    }

    @SuppressWarnings("unchecked")
    protected List<T> getCollectionInternal() {
        List<T> collection = new ArrayList<T>();
        for (BiobankCollectionModel item : model) {
            collection.add((T) item.o);
        }
        return collection;
    }

    public abstract List<T> getCollection();

    @Override
    public void setEnabled(boolean enabled) {
        tableViewer.getTable().setEnabled(enabled);
    }

    public abstract T getSelection();

    protected BiobankCollectionModel getSelectionInternal() {
        Assert.isTrue(!tableViewer.getTable().isDisposed(),
            "widget is disposed");
        IStructuredSelection stSelection = (IStructuredSelection) tableViewer
            .getSelection();

        return (BiobankCollectionModel) stSelection.getFirstElement();
    }

    public void addDoubleClickListener(IDoubleClickListener listener) {
        doubleClickListeners.add(listener);
    }

    public void doubleClick() {
        // get selection as derived class object
        T selection = getSelection();

        final DoubleClickEvent event = new DoubleClickEvent(tableViewer,
            new InfoTableSelection(selection));
        Object[] listeners = doubleClickListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            final IDoubleClickListener l = (IDoubleClickListener) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
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
        item.setText("Add");
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
        item.setText("Edit");
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
        item.setText("Delete");
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
                public void run() {
                    l.deleteItem(event);
                }
            });
        }
    }

    protected void addPaginationWidget(Composite parent) {
        paginationWidget = new Composite(parent, SWT.NONE);
        paginationWidget.setLayout(new GridLayout(5, false));

        firstButton = new Button(paginationWidget, SWT.NONE);
        firstButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_2_ARROW_LEFT));
        firstButton.setToolTipText("First page");
        firstButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false,
            false));
        firstButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                firstPage();
            }
        });

        prevButton = new Button(paginationWidget, SWT.NONE);
        prevButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_ARROW_LEFT));
        prevButton.setToolTipText("Previous page");
        prevButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false,
            false));
        prevButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                prevPage();
            }
        });

        pageLabel = new Label(paginationWidget, SWT.NONE);
        GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        gd.widthHint = 80;
        pageLabel.setLayoutData(gd);

        nextButton = new Button(paginationWidget, SWT.NONE);
        nextButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_ARROW_RIGHT));
        nextButton.setToolTipText("Next page");
        nextButton.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));
        nextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                nextPage();
            }
        });

        lastButton = new Button(paginationWidget, SWT.NONE);
        lastButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_2_ARROW_RIGHT));
        lastButton.setToolTipText("Last page");
        lastButton.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));
        lastButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                lastPage();
            }
        });

        // do not display it yet, wait till collection is added
        paginationWidget.setVisible(false);
        gd = new GridData(SWT.CENTER, SWT.TOP, true, true);
        gd.exclude = true;
        paginationWidget.setLayoutData(gd);
    }

    private void enablePaginationWidget() {
        if (paginationWidget.getVisible())
            return;

        GridData gd = (GridData) paginationWidget.getLayoutData();
        gd.exclude = false;
        paginationWidget.setVisible(true);
        paginationWidget.setEnabled(true);
        firstButton.setEnabled(false);
        prevButton.setEnabled(false);
        layout(true);
    }

    private void firstPage() {
        pageInfo.page = 0;
        firstButton.setEnabled(false);
        prevButton.setEnabled(false);
        lastButton.setEnabled(true);
        nextButton.setEnabled(true);
        setPageLabelText();
        setCollection(collection);
    }

    private void lastPage() {
        pageInfo.page = pageInfo.pageTotal - 1;
        firstButton.setEnabled(true);
        prevButton.setEnabled(true);
        lastButton.setEnabled(false);
        nextButton.setEnabled(false);
        setPageLabelText();
        setCollection(collection);
    }

    private void prevPage() {
        if (pageInfo.page == 0)
            return;
        pageInfo.page--;
        if (pageInfo.page == 0) {
            firstButton.setEnabled(false);
            prevButton.setEnabled(false);
        } else if (pageInfo.page == pageInfo.pageTotal - 2) {
            lastButton.setEnabled(true);
            nextButton.setEnabled(true);
        }
        setPageLabelText();
        setCollection(collection);
    }

    private void nextPage() {
        if (pageInfo.page >= pageInfo.pageTotal)
            return;
        pageInfo.page++;
        if (pageInfo.page == 1) {
            firstButton.setEnabled(true);
            prevButton.setEnabled(true);
        } else if (pageInfo.page == pageInfo.pageTotal - 1) {
            lastButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
        setPageLabelText();
        setCollection(collection);
    }

    private void setPageLabelText() {
        pageLabel.setText("Page: " + (pageInfo.page + 1) + " of "
            + pageInfo.pageTotal);
        layout(true, true);
    }
}

class PageInformation {
    Integer pageTotal;
    Integer page;
    Integer rowsPerPage;
}
