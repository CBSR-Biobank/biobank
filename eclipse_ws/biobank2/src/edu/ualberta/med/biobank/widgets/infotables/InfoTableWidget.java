package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.BiobankWidget;

public class InfoTableWidget<T> extends BiobankWidget {

    private static Logger LOGGER = Logger.getLogger(InfoTableWidget.class
        .getName());

    protected TableViewer tableViewer;

    protected List<BiobankCollectionModel> model;

    protected List<TableViewerColumn> tableViewColumns;

    public InfoTableWidget(Composite parent, Collection<T> collection,
        String[] headings, int[] bounds) {
        super(parent, SWT.NONE);

        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        setLayout(new GridLayout(1, false));

        tableViewer = new TableViewer(this, SWT.BORDER // | SWT.MULTI
            | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.VIRTUAL);

        Table table = tableViewer.getTable();
        table.setLayout(new TableLayout());
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 100;
        table.setLayoutData(gd);
        // table.setFont(getFont());
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        tableViewColumns = new ArrayList<TableViewerColumn>();

        int index = 0;
        for (String name : headings) {
            final TableViewerColumn col = new TableViewerColumn(tableViewer,
                SWT.NONE);
            col.getColumn().setText(name);
            if (bounds == null || bounds[index] == -1) {
                col.getColumn().pack();
            } else {
                col.getColumn().setWidth(bounds[index]);
            }
            col.getColumn().setResizable(true);
            col.getColumn().setMoveable(true);
            col.getColumn().addListener(SWT.SELECTED, new Listener() {
                public void handleEvent(Event event) {
                    col.getColumn().pack();
                }
            });
            tableViewColumns.add(col);
            index++;
        }
        tableViewer.setColumnProperties(headings);
        tableViewer.setUseHashlookup(true);
        tableViewer.setLabelProvider(getLabelProvider());
        tableViewer.setContentProvider(new ArrayContentProvider());

        model = new ArrayList<BiobankCollectionModel>();
        tableViewer.setInput(model);

        if (collection != null) {
            for (int i = 0, n = collection.size(); i < n; ++i) {
                model.add(new BiobankCollectionModel());
            }
            getTableViewer().refresh();
            setCollection(collection);
        }
    }

    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider();
    }

    public void addDoubleClickListener(IDoubleClickListener listener) {
        tableViewer.addDoubleClickListener(listener);
    }

    @Override
    public boolean setFocus() {
        tableViewer.getControl().setFocus();
        return true;
    }

    public void addSelectionListener(SelectionListener listener) {
        tableViewer.getTable().addSelectionListener(listener);
    }

    public TableViewer getTableViewer() {
        return tableViewer;
    }

    public void setCollection(final Collection<T> collection) {
        if (collection == null)
            return;

        Thread t = new Thread() {
            @Override
            public void run() {
                final TableViewer viewer = getTableViewer();
                Display display = viewer.getTable().getDisplay();
                int count = 0;

                if (model.size() != collection.size()) {
                    model.clear();
                    for (int i = 0, n = collection.size(); i < n; ++i) {
                        model.add(new BiobankCollectionModel());
                    }
                    display.asyncExec(new Runnable() {
                        public void run() {
                            if (!viewer.getTable().isDisposed())
                                getTableViewer().refresh();
                        }
                    });
                }

                try {
                    for (T item : collection) {
                        if (viewer.getTable().isDisposed())
                            return;

                        final BiobankCollectionModel modelItem = model
                            .get(count);
                        modelItem.o = getCollectionModelObject(item);
                        if (item instanceof ModelWrapper<?>) {
                            ((ModelWrapper<?>) item).loadAttributes();
                        }

                        if (!isDisposed()) {
                            display.asyncExec(new Runnable() {
                                public void run() {
                                    if (!viewer.getTable().isDisposed())
                                        viewer.refresh(modelItem, false);
                                }
                            });
                        }
                        ++count;
                    }
                } catch (Exception e) {
                    LOGGER.error("setCollection error", e);
                }
            }

        };
        t.start();
    }

    @SuppressWarnings("unused")
    public Object getCollectionModelObject(T item) throws Exception {
        return item;
    }

    @SuppressWarnings("unchecked")
    public List<T> getCollection() {
        List<T> collection = new ArrayList<T>();
        for (BiobankCollectionModel item : model) {
            collection.add((T) item.o);
        }
        return collection;
    }

    @Override
    public void setEnabled(boolean enabled) {
        tableViewer.getTable().setEnabled(enabled);
    }

    @SuppressWarnings("unchecked")
    public T getSelection() {
        Assert.isTrue(!tableViewer.getTable().isDisposed(),
            "widget is disposed");
        IStructuredSelection stSelection = (IStructuredSelection) tableViewer
            .getSelection();

        BiobankCollectionModel item = (BiobankCollectionModel) stSelection
            .getFirstElement();
        if (item == null)
            return null;
        return (T) item.o;
    }

}
