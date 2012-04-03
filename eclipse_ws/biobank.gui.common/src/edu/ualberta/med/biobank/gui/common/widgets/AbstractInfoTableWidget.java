package edu.ualberta.med.biobank.gui.common.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.common.util.DelegatingList;
import edu.ualberta.med.biobank.common.util.ListChangeHandler;
import edu.ualberta.med.biobank.common.util.ListChangeSource;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcClipboard;

/**
 * This abstract class is used to create most the tables used in the client. The
 * template parameter is the collection that contains the information to be
 * displayed in the table.
 * <p>
 * Derived classes must provide the following (through the use of abstract
 * methods):
 * <ul>
 * <li>label provider</li>
 * <li>table sorter</li>
 * </ul>
 * 
 * This class contains the following features:
 * <ul>
 * <li>column sorting</li>
 * <li>copy row to clipboard</li>
 * <li>pagination widget</li>
 * </ul>
 */
public abstract class AbstractInfoTableWidget<T> extends BgcBaseWidget
    implements IInfoTablePagination, ListChangeSource<T> {

    public static class RowItem {
        int itemNumber;
    }

    protected final DelegatingList<T> list = new DelegatingList<T>();

    protected TableViewer tableViewer;

    protected Menu menu;

    protected boolean paginationRequired;

    protected int start;

    protected int end;

    protected boolean reloadData = false;

    protected boolean autoSizeColumns;

    private BgcTableSorter tableSorter;

    private BgcLabelProvider labelProvider;

    protected PaginationWidget paginationWidget;

    // TODO: all listeners can be managed by an external class
    protected ListenerList addItemListeners = new ListenerList();
    protected ListenerList editItemListeners = new ListenerList();
    protected ListenerList deleteItemListeners = new ListenerList();
    protected ListenerList doubleClickListeners = new ListenerList();

    public AbstractInfoTableWidget(Composite parent, String[] headings,
        int[] columnWidths, int rowsPerPage) {
        super(parent, SWT.NONE);

        GridLayout gl = new GridLayout(1, false);
        gl.verticalSpacing = 1;
        setLayout(gl);
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        int style = SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL;

        tableViewer = new TableViewer(this, style);
        tableSorter = getTableSorter();
        labelProvider = getLabelProvider();

        Table table = tableViewer.getTable();
        table.setLayout(new TableLayout());
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData gd = new GridData(SWT.FILL, SWT.NONE, true, true);
        table.setLayoutData(gd);

        tableViewer.setSorter(tableSorter);
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                AbstractInfoTableWidget.this.doubleClick();
            }
        });

        setHeadings(headings, columnWidths);
        tableViewer.setUseHashlookup(true);
        tableViewer.setLabelProvider(labelProvider);
        tableViewer.setContentProvider(new ArrayContentProvider());

        paginationWidget = new PaginationWidget(this, SWT.NONE, this,
            PaginationWidget.NEXT_PAGE_BUTTON
                | PaginationWidget.LAST_PAGE_BUTTON, rowsPerPage);

        autoSizeColumns = (columnWidths == null) ? true : false;

        menu = new Menu(parent);
        tableViewer.getTable().setMenu(menu);

        // need to autosize at creation to be sure the size is well initialized
        // the first time. (if don't do that, display problems in UserManagement
        // Dialog):
        if (autoSizeColumns) {
            autoSizeColumns();
        }

        BgcClipboard.addClipboardCopySupport(tableViewer, menu, labelProvider,
            headings.length);

    }

    public void setHeadings(String[] headings) {
        setHeadings(headings, null);
    }

    public void setHeadings(String[] headings, int[] columnWidths) {
        int index = 0;
        if (headings != null) {
            for (String name : headings) {
                final TableViewerColumn viewerCol = new TableViewerColumn(
                    tableViewer, SWT.NONE);
                final TableColumn col = viewerCol.getColumn();
                final int fIndex = index;
                col.setText(name);
                if ((columnWidths == null) || (columnWidths[index] == -1)) {
                    col.pack();
                } else {
                    col.setWidth(columnWidths[index]);
                }
                col.setResizable(true);
                col.setMoveable(true);
                col.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        col.pack();
                        Table table = tableViewer.getTable();

                        if (tableSorter != null) {
                            tableSorter.setColumn(fIndex);
                            int dir = table.getSortDirection();
                            if (tableViewer.getTable().getSortColumn() == col) {
                                dir = (dir == SWT.UP) ? SWT.DOWN : SWT.UP;
                            } else {
                                dir = SWT.DOWN;
                            }
                            table.setSortDirection(dir);
                            table.setSortColumn(col);
                            tableViewer.refresh();
                        }
                    }
                });
                index++;
            }
            tableViewer.setColumnProperties(headings);
        }
    }

    protected abstract boolean isEditMode();

    protected abstract BgcLabelProvider getLabelProvider();

    protected abstract BgcTableSorter getTableSorter();

    public abstract void reload();

    @Override
    public boolean setFocus() {
        tableViewer.getControl().setFocus();
        return true;
    }

    protected TableViewer getTableViewer() {
        return tableViewer;
    }

    protected void autoSizeColumns() {
        // TODO: auto-size table initially based on headers? Sort of already
        // done with .pack().
        Table table = tableViewer.getTable();
        if (table.isDisposed()) {
            return;
        }
        final int[] maxCellContentsWidths = new int[table.getColumnCount()];
        Text textRenderer = new Text(menu.getShell(), SWT.NONE);
        textRenderer.setVisible(false);

        GridData gd = new GridData();
        gd.exclude = true;
        textRenderer.setLayoutData(gd);

        for (int i = 0; i < table.getColumnCount(); i++) {
            textRenderer.setText(table.getColumn(i).getText());
            maxCellContentsWidths[i] = textRenderer.computeSize(SWT.DEFAULT,
                SWT.DEFAULT).x;
        }

        for (TableItem row : table.getItems()) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                String rowText = row.getText(i);
                Image rowImage = row.getImage(i);
                int cellContentsWidth = 0;

                if (rowText != null) {
                    textRenderer.setText(rowText);
                    cellContentsWidth = textRenderer.computeSize(SWT.DEFAULT,
                        SWT.DEFAULT).x;
                } else if (rowImage != null) {
                    cellContentsWidth = rowImage.getImageData().width;
                }

                maxCellContentsWidths[i] = Math.max(cellContentsWidth,
                    maxCellContentsWidths[i]);
            }
        }

        textRenderer.dispose();

        int sumOfMaxTextWidths = 0;
        for (int width : maxCellContentsWidths) {
            sumOfMaxTextWidths += width;
        }

        // need to give default max=500 when can't know the size of
        // the table
        // yet (see UserManagementDialog)
        int tableWidth = Math.max(500, tableViewer.getTable().getSize().x);

        int totalWidths = 0;
        table.setVisible(false);
        for (int i = 0; i < table.getColumnCount(); i++) {
            int width =
                (int) (((double) maxCellContentsWidths[i] / sumOfMaxTextWidths) * tableWidth);
            if (i == (table.getColumnCount() - 1))
                table.getColumn(i).setWidth(tableWidth - totalWidths - 5);
            else
                table.getColumn(i).setWidth(width);
            totalWidths += width;
        }

        table.setVisible(true);
    }

    protected void resizeTable() {
        Table table = getTableViewer().getTable();
        GridData gd = (GridData) table.getLayoutData();
        int rows = Math.max(paginationWidget.getRowsPerPage(), 5);
        gd.heightHint = ((rows - 1) * table.getItemHeight())
            + table.getHeaderHeight() + table.getBorderWidth();
    }

    @Override
    public void setEnabled(boolean enabled) {
        tableViewer.getTable().setEnabled(enabled);
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    protected void showPaginationWidget() {
        paginationWidget.setVisible(true);
    }

    protected void enablePaginationWidget(boolean enable) {
        paginationWidget.setEnabled(enable);
        paginationWidget.enableWidgets(enable);
    }

    public void addSelectionListener(SelectionListener listener) {
        tableViewer.getTable().addSelectionListener(listener);
    }

    public void addClickListener(IInfoTableDoubleClickItemListener<T> listener) {
        doubleClickListeners.add(listener);
    }

    public void addAddItemListener(IInfoTableAddItemListener<T> listener) {
        addItemListeners.add(listener);

        Assert.isNotNull(menu);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.AbstractInfoTableWidget_add);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                addItem();
            }
        });
    }

    public void addEditItemListener(IInfoTableEditItemListener<T> listener) {
        editItemListeners.add(listener);

        Assert.isNotNull(menu);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.AbstractInfoTableWidget_edit);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                editItem();
            }
        });
    }

    public void addDeleteItemListener(IInfoTableDeleteItemListener<T> listener) {
        deleteItemListeners.add(listener);

        Assert.isNotNull(menu);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(Messages.AbstractInfoTableWidget_delete);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                deleteItem();
            }
        });
    }

    /**
     * IMPORTANT: Remember that your primary class in TableRowData must be of
     * type T.. can't enforce this from superclass
     * 
     * @return
     */
    public abstract T getSelection();

    public void doubleClick() {
        // get selection as derived class object
        Object selection = getSelection();

        final InfoTableEvent<T> event = new InfoTableEvent<T>(this,
            new InfoTableSelection(selection));
        Object[] listeners = doubleClickListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            final IInfoTableDoubleClickItemListener<T> l =
                (IInfoTableDoubleClickItemListener<T>) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                @Override
                public void run() {
                    l.doubleClick(event);
                }
            });
        }
    }

    protected void addItem() {
        Object objSelected = getSelection();

        if (objSelected == null)
            return;

        InfoTableSelection selection = new InfoTableSelection(objSelected);
        final InfoTableEvent<T> event = new InfoTableEvent<T>(this, selection);
        Object[] listeners = addItemListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            @SuppressWarnings("unchecked")
            final IInfoTableAddItemListener<T> l =
                (IInfoTableAddItemListener<T>) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                @Override
                public void run() {
                    l.addItem(event);
                }
            });
        }
    }

    public void editItem() {
        Object objSelected = getSelection();

        if (objSelected == null)
            return;

        InfoTableSelection selection = new InfoTableSelection(objSelected);
        final InfoTableEvent<T> event = new InfoTableEvent<T>(this, selection);
        Object[] listeners = editItemListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            @SuppressWarnings("unchecked")
            final IInfoTableEditItemListener<T> l =
                (IInfoTableEditItemListener<T>) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                @Override
                public void run() {
                    l.editItem(event);
                }
            });
        }
    }

    protected void deleteItem() {
        Object objSelected = getSelection();

        if (objSelected == null)
            return;

        InfoTableSelection selection = new InfoTableSelection(objSelected);
        final InfoTableEvent<T> event = new InfoTableEvent<T>(this, selection);
        Object[] listeners = deleteItemListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            @SuppressWarnings("unchecked")
            final IInfoTableDeleteItemListener<T> l =
                (IInfoTableDeleteItemListener<T>) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                @Override
                public void run() {
                    l.deleteItem(event);
                }
            });
        }
    }

    public void setList(final List<T> list) {
        this.list.setDelegate(list);
    }

    public void setCollection(final Collection<T> list) {
        this.list.setDelegate(new ArrayList<T>(list));
    }

    public List<T> getList() {
        return list;
    }

    @Override
    public void addListChangeHandler(ListChangeHandler<T> handler) {
        list.addListChangeHandler(handler);
    }

    @Override
    public void removeListChangeHandler(ListChangeHandler<T> handler) {
        list.removeListChangeHandler(handler);
    }
}
