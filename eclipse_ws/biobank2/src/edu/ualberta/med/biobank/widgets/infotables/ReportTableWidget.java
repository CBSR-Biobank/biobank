package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
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

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
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
public class ReportTableWidget<T> extends BiobankWidget {

    class PageInformation {
        int pageTotal;
        int page;
        int rowsPerPage;
    }

    /*
     * see http://lekkimworld.com/2008/03/27/setting_table_row_height_in_swt
     * .html for how to set row height.
     */

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ReportTableWidget.class.getName());

    protected TableViewer tableViewer;

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

    private boolean reloadData;

    protected int start;

    protected int end;

    public ReportTableWidget(Composite parent, List<T> collection,
        String[] headings, int[] columnWidths) {
        super(parent, SWT.NONE);

        reloadData = true;
        pageInfo.rowsPerPage = 0;
        GridLayout gl = new GridLayout(1, false);
        gl.verticalSpacing = 1;
        setLayout(gl);
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
            | SWT.FULL_SELECTION | SWT.VIRTUAL;
        if (isEditMode()) {
            style |= SWT.MULTI;
        }

        tableViewer = new TableViewer(this, style);

        Table table = tableViewer.getTable();
        table.setLayout(new TableLayout());
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        table.setLayoutData(gd);

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

        if (collection != null) {
            getTableViewer().refresh();
            setCollection(collection);
        }

        addClipboadCopySupport();
        addPaginationWidget();
    }

    private IBaseLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof BiobankCollectionModel) {
                    BiobankCollectionModel m = (BiobankCollectionModel) element;
                    if (m.o != null) {
                        return getColumnText(m.o, columnIndex);
                    } else if (columnIndex == 0) {
                        return "loading ...";
                    }
                } else if (element instanceof Object[]) {
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

    public ReportTableWidget(Composite parent, List<T> collection,
        String[] headings, int[] columnWidths, int rowsPerPage) {
        this(parent, null, headings, columnWidths);
        pageInfo.rowsPerPage = rowsPerPage;
        if (collection != null) {
            setCollection(collection);
        }
        resizeTable();
    }

    /**
     * Derived classes should override this method if info table support editing
     * of items in the table.
     * 
     * @return true if editing is allowed.
     */
    protected boolean isEditMode() {
        return false;
    }

    private void addClipboadCopySupport() {
        Assert.isNotNull(menu);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Copy");
        item.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public void widgetSelected(SelectionEvent event) {
                List<Object> selectedRows = new ArrayList<Object>();
                IStructuredSelection sel = (IStructuredSelection) tableViewer
                    .getSelection();
                for (Iterator<Object> iterator = sel.iterator(); iterator
                    .hasNext();) {
                    selectedRows.add(iterator.next());
                }
                StringBuilder sb = new StringBuilder();
                for (Object row : selectedRows) {
                    if (sb.length() != 0) {
                        sb.append(System.getProperty("line.separator"));
                    }
                    sb.append(row);
                }
                TextTransfer textTransfer = TextTransfer.getInstance();
                Clipboard cb = new Clipboard(Display.getDefault());
                cb.setContents(new Object[] { sb.toString() },
                    new Transfer[] { textTransfer });
            }
        });
    }

    protected void sortOnFirstColumn() {
        Table table = tableViewer.getTable();
        table.setSortDirection(SWT.DOWN);
        table.setSortColumn(table.getColumn(0));
        tableViewer.refresh();
    }

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

    public void setCollection(final List<T> collection) {
        setCollection(collection, null);
    }

    public void setCollection(final List<T> collection, final T selection) {
        this.collection = collection;
        if ((collection == null)
            || ((backgroundThread != null) && backgroundThread.isAlive())) {
            return;
        }

        if (!isEditMode() && (pageInfo.rowsPerPage != 0)
            && (collection.size() > pageInfo.rowsPerPage)
            && !paginationWidget.getVisible()) {
            pageInfo.page = 0;
            Double size = new Double(collection.size());
            Double pageSize = new Double(pageInfo.rowsPerPage);
            if (size < 1000)
                pageInfo.pageTotal = new Double(Math.ceil(size / pageSize))
                    .intValue();
            else
                pageInfo.pageTotal = -1;
            setPageLabelText();
            showPaginationWidget();
            paginationRequired = true;
        }

        if (paginationRequired) {
            enablePaginationWidget(false);
        }

        resizeTable();

        backgroundThread = new Thread() {
            @Override
            public void run() {
                final TableViewer viewer = getTableViewer();
                final Table table = viewer.getTable();
                Display display = viewer.getTable().getDisplay();

                if (paginationRequired) {
                    start = pageInfo.page * pageInfo.rowsPerPage;
                    end = Math.min(start + pageInfo.rowsPerPage, collection
                        .size());
                } else {
                    start = 0;
                    end = collection.size();
                }

                final Collection<T> collSubList = collection
                    .subList(start, end);

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
                    reloadData = false;

                    final Object selectedItem = selItem;
                    display.syncExec(new Runnable() {
                        public void run() {
                            if (!table.isDisposed()) {
                                if (paginationRequired) {
                                    enablePaginationWidget(true);
                                }

                                if (selectedItem != null) {
                                    tableViewer
                                        .setSelection(new StructuredSelection(
                                            selectedItem));
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    logger.error("setCollection error", e);
                }
            }

        };
        backgroundThread.start();
    }

    @Override
    public void setEnabled(boolean enabled) {
        tableViewer.getTable().setEnabled(enabled);
    }

    protected BiobankCollectionModel getSelectionInternal() {
        Assert.isTrue(!tableViewer.getTable().isDisposed(),
            "widget is disposed");
        IStructuredSelection stSelection = (IStructuredSelection) tableViewer
            .getSelection();

        return (BiobankCollectionModel) stSelection.getFirstElement();
    }

    protected void addPaginationWidget() {
        paginationWidget = new Composite(this, SWT.NONE);
        paginationWidget.setLayout(new GridLayout(5, false));

        firstButton = new Button(paginationWidget, SWT.NONE);
        firstButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_RESULTSET_FIRST));
        firstButton.setToolTipText("First page");
        firstButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                firstPage();
            }
        });

        prevButton = new Button(paginationWidget, SWT.NONE);
        prevButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_RESULTSET_PREV));
        prevButton.setToolTipText("Previous page");
        prevButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                prevPage();
            }
        });

        pageLabel = new Label(paginationWidget, SWT.NONE);

        nextButton = new Button(paginationWidget, SWT.NONE);
        nextButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_RESULTSET_NEXT));
        nextButton.setToolTipText("Next page");
        nextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                nextPage();
            }
        });
        lastButton = new Button(paginationWidget, SWT.NONE);
        lastButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_RESULTSET_LAST));
        lastButton.setToolTipText("Last page");
        lastButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                lastPage();
            }
        });

        // do not display it yet, wait till collection is added
        paginationWidget.setVisible(false);
        GridData gd = new GridData(SWT.END, SWT.TOP, true, true);
        gd.exclude = true;
        paginationWidget.setLayoutData(gd);
    }

    private void showPaginationWidget() {
        GridData gd = (GridData) paginationWidget.getLayoutData();
        gd.exclude = false;
        paginationWidget.setVisible(true);
    }

    private void enablePaginationWidget(boolean enable) {
        paginationWidget.setEnabled(enable);

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
        } else if (enable && (pageInfo.pageTotal == -1)) {
            nextButton.setEnabled(true);
            lastButton.setEnabled(false);
        } else {
            lastButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
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
        }
        if (pageInfo.page == pageInfo.pageTotal - 2) {
            lastButton.setEnabled(true);
            nextButton.setEnabled(true);
        }
        setPageLabelText();
        setCollection(collection);
    }

    private void nextPage() {
        if (pageInfo.page >= pageInfo.pageTotal && pageInfo.pageTotal != -1)
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
        setPageLabelText();
        setCollection(collection);
        paginationWidget.layout(true);
    }

    private void setPageLabelText() {
        if (pageInfo.pageTotal == -1)
            pageLabel.setText("Page: " + (pageInfo.page + 1) + " of " + "?");
        else
            pageLabel.setText("Page: " + (pageInfo.page + 1) + " of "
                + pageInfo.pageTotal);
    }

    private void resizeTable() {
        int rows = 5;
        if (!isEditMode() && (pageInfo.rowsPerPage > 0) && (collection != null)) {
            rows = Math.min(collection.size(), pageInfo.rowsPerPage);
        } else if (!isEditMode() && (collection != null)) {
            rows = Math.min(collection.size(), rows);
        }

        Table table = getTableViewer().getTable();
        GridData gd = (GridData) table.getLayoutData();
        gd.heightHint = rows * table.getItemHeight() + table.getHeaderHeight();
        layout(true);
    }
}
