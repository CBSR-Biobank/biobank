package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.utils.BiobankClipboard;

public abstract class AbstractInfoTableWidget<T> extends BgcBaseWidget {

    class PageInformation {
        int page;
        int rowsPerPage;
        int pageTotal;
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

    private List<T> collection;

    protected int start;

    protected int end;

    protected boolean reloadData = false;

    private int size;

    private boolean autoSizeColumns;

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

        Table table = tableViewer.getTable();
        table.setLayout(new TableLayout());
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        table.setLayoutData(gd);

        setHeadings(headings, columnWidths);
        tableViewer.setUseHashlookup(true);
        tableViewer.setLabelProvider(getLabelProvider());
        tableViewer.setContentProvider(new ArrayContentProvider());

        addPaginationWidget();

        if (collection != null)
            setCollection(collection);

        menu = new Menu(parent);
        tableViewer.getTable().setMenu(menu);

        autoSizeColumns = columnWidths == null ? true : false;

        BiobankClipboard.addClipboardCopySupport(tableViewer, menu,
            (BiobankLabelProvider) getLabelProvider(), headings.length);

    }

    public void setHeadings(String[] headings) {
        setHeadings(headings, null);
    }

    public void setHeadings(String[] headings, int[] columnWidths) {
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
                    @Override
                    public void handleEvent(Event event) {
                        col.getColumn().pack();
                    }
                });
                index++;
            }
            tableViewer.setColumnProperties(headings);
        }
    }

    protected abstract boolean isEditMode();

    protected abstract IBaseLabelProvider getLabelProvider();

    public List<T> getCollection() {
        return collection;
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
     * This method is used to load object model data in the background thread.
     * 
     * @param item the model object representing the base object to get
     *            information from.
     * @return an non-object model object with the table data.
     * 
     * @throws Exception
     */
    protected abstract void tableLoader(final List<T> collection,
        final T Selection);

    public void setCollection(final List<T> collection) {
        setCollection(collection, null);
        if (collection != null) {
            size = collection.size();
        }
    }

    public void setCollection(final List<T> collection, final T selection) {
        try {
            if ((collection == null)
                || ((backgroundThread != null) && backgroundThread.isAlive())) {
                return;
            } else if (this.collection != collection
                || size != collection.size()) {
                this.collection = collection;
                init(collection);
                setPaginationParams(collection);
            }

            if (paginationRequired) {
                showPaginationWidget();
                setPageLabelText();
                enablePaginationWidget(false);
            } else if (paginationWidget != null)
                paginationWidget.setVisible(false);

            final Display display = getTableViewer().getTable().getDisplay();
            resizeTable();
            backgroundThread = new Thread() {
                @Override
                public void run() {
                    tableLoader(collection, selection);
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

        layout(true, true);
    }

    private void autoSizeColumns() {
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

        int tableWidth = Math.max(500, tableViewer.getTable().getSize().x);

        int totalWidths = 0;
        tableViewer.getTable().setVisible(false);
        for (int i = 0; i < table.getColumnCount(); i++) {
            int width = (int) ((double) maxCellContentsWidths[i]
                / sumOfMaxTextWidths * tableWidth);
            if (i == table.getColumnCount() - 1)
                table.getColumn(i).setWidth(tableWidth - totalWidths - 5);
            else
                table.getColumn(i).setWidth(width);
            totalWidths += width;
        }
        tableViewer.getTable().setVisible(true);
    }

    protected abstract void init(List<T> collection);

    private void resizeTable() {
        Table table = getTableViewer().getTable();
        GridData gd = (GridData) table.getLayoutData();
        int rows = Math.max(pageInfo.rowsPerPage, 5);
        gd.heightHint = (rows - 1) * table.getItemHeight()
            + table.getHeaderHeight() + 4;
        layout(true, true);

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
        GridData gd = new GridData(SWT.END, SWT.TOP, true, true);
        gd.exclude = false;
        paginationWidget.setLayoutData(gd);
        layout(true, true);
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    protected abstract void setDefaultWidgetsEnabled();

    private void showPaginationWidget() {
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

    private void newPage() {
        setCollection(collection);
        setPageLabelText();
    }

    protected abstract void firstPage();

    protected abstract void prevPage();

    protected abstract void nextPage();

    protected abstract void lastPage();

    protected abstract void setPageLabelText();

}
