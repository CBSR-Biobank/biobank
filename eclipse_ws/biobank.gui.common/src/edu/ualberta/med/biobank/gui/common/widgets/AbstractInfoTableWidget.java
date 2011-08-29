package edu.ualberta.med.biobank.gui.common.widgets;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
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
 * 
 * @param <T> The information to be displayed in the table.
 */
public abstract class AbstractInfoTableWidget<T> extends BgcBaseWidget {

    public class PageInformation {
        public int page;
        public int rowsPerPage;
        public int pageTotal;
    }

    protected TableViewer tableViewer;

    protected Thread backgroundThread;

    protected Menu menu;

    protected boolean paginationRequired;

    protected Composite paginationWidget;

    protected PageInformation pageInfo;

    protected Button firstButton;

    protected Button lastButton;

    protected Button prevButton;

    protected Button nextButton;

    protected Label pageLabel;

    protected int start;

    protected int end;

    protected boolean reloadData = false;

    protected boolean autoSizeColumns;

    private BgcTableSorter tableSorter;

    private BgcLabelProvider labelProvider;

    public AbstractInfoTableWidget(Composite parent, List<T> collection,
        String[] headings, int[] columnWidths, int rowsPerPage) {
        super(parent, SWT.NONE);

        pageInfo = new PageInformation();
        pageInfo.rowsPerPage = rowsPerPage;
        pageInfo.page = 0;
        GridLayout gl = new GridLayout(1, false);
        gl.verticalSpacing = 1;
        setLayout(gl);
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        int style = SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL;

        if (!isEditMode())
            style = style | SWT.MULTI;

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

        setHeadings(headings, columnWidths);
        tableViewer.setUseHashlookup(true);
        tableViewer.setLabelProvider(labelProvider);
        tableViewer.setContentProvider(new ArrayContentProvider());

        addPaginationWidget();

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

        // load the data
        setCollection(collection);
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

                        if (tableSorter != null) {
                            tableSorter.setColumn(fIndex);
                            int dir = tableViewer.getTable().getSortDirection();
                            if (tableViewer.getTable().getSortColumn() == col) {
                                dir = (dir == SWT.UP ? SWT.DOWN : SWT.UP);
                            } else {
                                dir = SWT.DOWN;
                            }
                            tableViewer.getTable().setSortDirection(dir);
                            tableViewer.getTable().setSortColumn(col);
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

    // TODO: convert to abstract method
    // protected abstract BgcTableSorter getTableSorter();
    protected BgcTableSorter getTableSorter() {
        return null;
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

    public abstract void setCollection(final List<T> collection);

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
            int width = (int) (((double) maxCellContentsWidths[i] / sumOfMaxTextWidths) * tableWidth);
            if (i == (table.getColumnCount() - 1))
                table.getColumn(i).setWidth(tableWidth - totalWidths - 5);
            else
                table.getColumn(i).setWidth(width);
            totalWidths += width;
        }

        table.setVisible(true);
    }

    protected abstract void init(List<T> collection);

    protected void resizeTable() {
        Table table = getTableViewer().getTable();
        GridData gd = (GridData) table.getLayoutData();
        int rows = Math.max(pageInfo.rowsPerPage, 5);
        gd.heightHint = ((rows - 1) * table.getItemHeight())
            + table.getHeaderHeight() + table.getBorderWidth();
    }

    protected abstract void setPaginationParams(List<T> collection);

    @Override
    public void setEnabled(boolean enabled) {
        tableViewer.getTable().setEnabled(enabled);
    }

    protected void addPaginationWidget() {
        if (paginationWidget != null)
            paginationWidget.dispose();
        paginationWidget = new Composite(this, SWT.NONE);
        paginationWidget.setLayout(new GridLayout(5, false));

        firstButton = new Button(paginationWidget, SWT.NONE);
        firstButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_RESULTSET_FIRST));
        firstButton
            .setToolTipText(Messages.AbstractInfoTableWidget_first_label);
        firstButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                firstP();
            }
        });

        prevButton = new Button(paginationWidget, SWT.NONE);
        prevButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_RESULTSET_PREV));
        prevButton
            .setToolTipText(Messages.AbstractInfoTableWidget_previous_label);
        prevButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                prevP();
            }
        });

        pageLabel = new Label(paginationWidget, SWT.NONE);

        nextButton = new Button(paginationWidget, SWT.NONE);
        nextButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_RESULTSET_NEXT));
        nextButton.setToolTipText(Messages.AbstractInfoTableWidget_next_label);
        nextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                nextP();
            }
        });

        lastButton = new Button(paginationWidget, SWT.NONE);
        lastButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_RESULTSET_LAST));
        lastButton.setToolTipText(Messages.AbstractInfoTableWidget_last_label);
        lastButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                lastP();
            }
        });

        setDefaultWidgetsEnabled();

        setPageLabelText();

        // do not display it yet, wait till collection is added
        paginationWidget.setVisible(false);
        GridData gd = new GridData(SWT.END, SWT.TOP, true, false);
        gd.exclude = false;
        paginationWidget.setLayoutData(gd);
        layout(true, true);
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    protected abstract void setDefaultWidgetsEnabled();

    protected void showPaginationWidget() {
        paginationWidget.setVisible(true);
    }

    protected void enablePaginationWidget(boolean enable) {
        paginationWidget.setEnabled(enable);
        enableWidgets(enable);
    }

    protected abstract void enableWidgets(boolean enable);

    private void firstP() {
        firstPage();
        newPage();
    }

    private void nextP() {
        nextPage();
        newPage();
    }

    private void prevP() {
        prevPage();
        newPage();
    }

    private void lastP() {
        lastPage();
        newPage();
    }

    protected void newPage() {
        setPageLabelText();
    }

    protected abstract void firstPage();

    protected abstract void prevPage();

    protected abstract void nextPage();

    protected abstract void lastPage();

    protected abstract void setPageLabelText();

}
