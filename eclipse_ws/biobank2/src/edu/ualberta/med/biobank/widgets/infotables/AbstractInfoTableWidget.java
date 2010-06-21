package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
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

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.BiobankWidget;

public abstract class AbstractInfoTableWidget<T> extends BiobankWidget {

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

    private boolean fitToInputSize;

    protected boolean reloadData = false;

    private int size;

    public AbstractInfoTableWidget(Composite parent, List<T> collection,
        String[] headings, int[] columnWidths, int rowsPerPage,
        boolean fitToInputSize) {
        super(parent, SWT.NONE);

        this.fitToInputSize = fitToInputSize;

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

        if (collection != null)
            setCollection(collection);

        menu = new Menu(parent);
        tableViewer.getTable().setMenu(menu);

        addClipboardCopySupport();
    }

    protected abstract boolean isEditMode();

    protected abstract IBaseLabelProvider getLabelProvider();

    public List<T> getCollection() {
        return collection;
    }

    private void addClipboardCopySupport() {
        Assert.isNotNull(menu);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Copy");
        item.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public void widgetSelected(SelectionEvent event) {
                BiobankLabelProvider lp = (BiobankLabelProvider) getLabelProvider();
                int numCols = getTableViewer().getTable().getColumnCount();
                List<Object> selectedRows = new ArrayList<Object>();
                IStructuredSelection sel = (IStructuredSelection) tableViewer
                    .getSelection();
                for (Iterator<Object> iterator = sel.iterator(); iterator
                    .hasNext();) {
                    Object item = iterator.next();
                    String row = "";
                    for (int i = 0; i < numCols; i++) {
                        String text = lp.getColumnText(item, i);
                        if (text != null)
                            row += text;
                        if (i < numCols - 1)
                            row += ", ";
                    }
                    selectedRows.add(row);
                }
                if (selectedRows.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (Object row : selectedRows) {
                        if (sb.length() != 0) {
                            sb.append(System.getProperty("line.separator"));
                        }
                        sb.append(row.toString());
                    }
                    TextTransfer textTransfer = TextTransfer.getInstance();
                    Clipboard cb = new Clipboard(Display.getDefault());
                    cb.setContents(new Object[] { sb.toString() },
                        new Transfer[] { textTransfer });
                }
            }
        });
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
        if ((collection == null)
            || ((backgroundThread != null) && backgroundThread.isAlive())) {
            return;
        } else if (this.collection != collection || size != collection.size()) {
            this.collection = collection;
            init(collection);
            setPaginationParams(collection);
        }

        resizeTable();

        if (paginationRequired) {
            setPageLabelText();
            showPaginationWidget();
            enablePaginationWidget(false);
        } else if (paginationWidget != null)
            paginationWidget.setVisible(false);

        backgroundThread = new Thread() {
            @Override
            public void run() {
                tableLoader(collection, selection);
            }

        };
        backgroundThread.start();
        this.getShell().layout(true, true);
    }

    protected abstract void init(List<T> collection);

    protected void fitToInputSize(boolean fit) {
        this.fitToInputSize = fit;
    }

    private void resizeTable() {
        Table table = getTableViewer().getTable();
        GridData gd = (GridData) table.getLayoutData();

        int rows = 5;
        if (fitToInputSize && (pageInfo.rowsPerPage > 0)) {
            rows = Math.min(collection.size(), pageInfo.rowsPerPage);
        } else if (fitToInputSize && (collection != null)) {
            rows = Math.min(collection.size(), rows);
        } else if (!fitToInputSize) {
            rows = pageInfo.rowsPerPage;
        }

        gd.heightHint = rows * table.getItemHeight() + table.getHeaderHeight();
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
        firstButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_RESULTSET_FIRST));
        firstButton.setToolTipText("First page");
        firstButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                firstP();
            }
        });

        prevButton = new Button(paginationWidget, SWT.NONE);
        prevButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_RESULTSET_PREV));
        prevButton.setToolTipText("Previous page");
        prevButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                prevP();
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
                nextP();
            }
        });

        lastButton = new Button(paginationWidget, SWT.NONE);
        lastButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_RESULTSET_LAST));
        lastButton.setToolTipText("Last page");
        lastButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                lastP();
            }
        });

        setDefaultWidgetsEnabled();

        // do not display it yet, wait till collection is added
        paginationWidget.setVisible(false);
        GridData gd = new GridData(SWT.END, SWT.TOP, true, true);
        gd.exclude = true;
        paginationWidget.setLayoutData(gd);
        layout(true, true);
    }

    protected abstract void setDefaultWidgetsEnabled();

    private void showPaginationWidget() {
        GridData gd = (GridData) paginationWidget.getLayoutData();
        gd.exclude = false;
        paginationWidget.setVisible(true);
    }

    protected void enablePaginationWidget(boolean enable) {
        paginationWidget.setEnabled(enable);
        enableWidgets(enable);
        layout(true);
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
