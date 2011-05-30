package edu.ualberta.med.biobank.widgets.trees.infos;

import java.util.List;

import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.widgets.BiobankClipboard;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.BiobankWidget;

public abstract class AbstractInfoTreeWidget<T> extends BiobankWidget {

    class PageInformation {
        int page;
        int rowsPerPage;
        int pageTotal;
    }

    protected TreeViewer treeViewer;

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

    protected Node root;

    public AbstractInfoTreeWidget(Composite parent, List<T> collection,
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

        treeViewer = new TreeViewer(this, style);

        Tree tree = treeViewer.getTree();
        // table.setLayout(new TableLayout());
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        tree.setLayoutData(gd);

        setHeadings(headings, columnWidths);
        // tableViewer.setUseHashlookup(true);
        treeViewer.setLabelProvider(getLabelProvider());
        treeViewer.setContentProvider(new ITreeContentProvider() {

            @Override
            public void inputChanged(Viewer viewer, Object oldInput,
                Object newInput) {
                // TODO Auto-generated method stub

            }

            @Override
            public void dispose() {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean hasChildren(Object element) {
                return getChildren(element).length > 0;
            }

            @Override
            public Object getParent(Object element) {
                if (element instanceof Node)
                    return ((Node) element).getParent();
                return null;
            }

            @Override
            public Object[] getElements(Object inputElement) {
                return getChildren(inputElement);
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                if (parentElement instanceof Node)
                    try {
                        return getNodeChildren((Node) parentElement).toArray();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                return new Object[0];
            }
        });
        root = new Node() {
            @Override
            public List<Node> getChildren() {
                return getRootChildren();
            }

            @Override
            public Object getParent() {
                return null;
            }
        };
        treeViewer.setInput(root);

        addPaginationWidget();

        if (collection != null)
            setCollection(collection);

        menu = new Menu(parent);
        treeViewer.getTree().setMenu(menu);

        autoSizeColumns = columnWidths == null ? true : false;

        BiobankClipboard.addClipboardCopySupport(treeViewer, menu,
            (BiobankLabelProvider) getLabelProvider(), headings.length);

    }

    protected abstract List<Node> getNodeChildren(Node node) throws Exception;

    protected abstract List<Node> getRootChildren();

    public void setHeadings(String[] headings) {
        setHeadings(headings, null);
    }

    public void setHeadings(String[] headings, int[] columnWidths) {
        int index = 0;
        if (headings != null) {
            for (String name : headings) {
                final TreeViewerColumn col = new TreeViewerColumn(treeViewer,
                    SWT.NONE);
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
                col.setEditingSupport(getEditingSupport(index));
                index++;
            }
            treeViewer.setColumnProperties(headings);
        }
    }

    protected EditingSupport getEditingSupport(
        @SuppressWarnings("unused") int index) {
        return null;
    }

    protected abstract boolean isEditMode();

    protected abstract IBaseLabelProvider getLabelProvider();

    public List<T> getCollection() {
        return collection;
    }

    @Override
    public boolean setFocus() {
        treeViewer.getControl().setFocus();
        return true;
    }

    public void addSelectionListener(SelectionListener listener) {
        treeViewer.getTree().addSelectionListener(listener);
    }

    protected TreeViewer getTreeViewer() {
        return treeViewer;
    }

    /**
     * This method is used to load object model data in the background thread.
     * 
     * @param item the model object representing the base object to get
     *            information from.
     * @return an non-object model object with the tree data.
     * 
     * @throws Exception
     */
    protected abstract void treeLoader(final List<T> collection,
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

            final Display display = getTreeViewer().getTree().getDisplay();
            resizeTree();
            backgroundThread = new Thread() {
                @Override
                public void run() {
                    treeLoader(collection, selection);
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
            BiobankPlugin.openAsyncError("Cannot Load Tree Data", e);
        }

        layout(true, true);
    }

    private void autoSizeColumns() {
        // TODO: auto-size tree initially based on headers? Sort of already
        // done with .pack().
        Tree tree = treeViewer.getTree();
        if (tree.isDisposed()) {
            return;
        }
        final int[] maxCellContentsWidths = new int[tree.getColumnCount()];
        Text textRenderer = new Text(menu.getShell(), SWT.NONE);
        textRenderer.setVisible(false);

        GridData gd = new GridData();
        gd.exclude = true;
        textRenderer.setLayoutData(gd);

        for (int i = 0; i < tree.getColumnCount(); i++) {
            textRenderer.setText(tree.getColumn(i).getText());
            maxCellContentsWidths[i] = textRenderer.computeSize(SWT.DEFAULT,
                SWT.DEFAULT).x;
        }

        for (TreeItem row : tree.getItems()) {
            for (int i = 0; i < tree.getColumnCount(); i++) {
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

        int treeWidth = Math.max(500, treeViewer.getTree().getSize().x);

        int totalWidths = 0;
        treeViewer.getTree().setVisible(false);
        for (int i = 0; i < tree.getColumnCount(); i++) {
            int width = (int) ((double) maxCellContentsWidths[i]
                / sumOfMaxTextWidths * treeWidth);
            if (i == tree.getColumnCount() - 1)
                tree.getColumn(i).setWidth(treeWidth - totalWidths - 5);
            else
                tree.getColumn(i).setWidth(width);
            totalWidths += width;
        }
        treeViewer.getTree().setVisible(true);
    }

    protected abstract void init(List<T> collection);

    private void resizeTree() {
        Tree tree = getTreeViewer().getTree();
        GridData gd = (GridData) tree.getLayoutData();
        int rows = Math.max(pageInfo.rowsPerPage, 5);
        gd.heightHint = (rows - 1) * tree.getItemHeight()
            + tree.getHeaderHeight() + 4;
        layout(true, true);

    }

    protected abstract void setPaginationParams(List<T> collection);

    @Override
    public void setEnabled(boolean enabled) {
        treeViewer.getTree().setEnabled(enabled);
    }

    protected void addPaginationWidget() {
        if (paginationWidget != null)
            paginationWidget.dispose();
        paginationWidget = new Composite(this, SWT.NONE);
        paginationWidget.setLayout(new GridLayout(5, false));

        firstButton = new Button(paginationWidget, SWT.NONE);
        firstButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_RESULTSET_FIRST));
        firstButton.setToolTipText("First page");
        firstButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                firstP();
            }
        });

        prevButton = new Button(paginationWidget, SWT.NONE);
        prevButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_RESULTSET_PREV));
        prevButton.setToolTipText("Previous page");
        prevButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                prevP();
            }
        });

        pageLabel = new Label(paginationWidget, SWT.NONE);

        nextButton = new Button(paginationWidget, SWT.NONE);
        nextButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_RESULTSET_NEXT));
        nextButton.setToolTipText("Next page");
        nextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                nextP();
            }
        });

        lastButton = new Button(paginationWidget, SWT.NONE);
        lastButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_RESULTSET_LAST));
        lastButton.setToolTipText("Last page");
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
