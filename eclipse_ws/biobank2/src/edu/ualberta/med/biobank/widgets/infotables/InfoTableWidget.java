package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.BiobankWidget;

public class InfoTableWidget<T> extends BiobankWidget {

    private TableViewer tableViewer;

    // FIXME - used to inform listeners of changes to the widget
    // could be done in a better way
    private int setCollectionCount;

    private List<BiobankCollectionModel> model;

    public InfoTableWidget(Composite parent, Collection<T> collection,
        String[] headings, int[] bounds) {
        super(parent, SWT.NONE);

        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        setLayout(new GridLayout(1, false));

        tableViewer = new TableViewer(this, SWT.BORDER | SWT.MULTI
            | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.VIRTUAL);
        tableViewer.setLabelProvider(new BiobankLabelProvider());
        tableViewer.setContentProvider(new ArrayContentProvider());

        Table table = tableViewer.getTable();
        table.setLayout(new TableLayout());
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 100;
        table.setLayoutData(gd);
        // table.setFont(getFont());
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        int index = 0;
        for (String name : headings) {
            final TableColumn col = new TableColumn(table, SWT.NONE);
            col.setText(name);
            if (bounds == null || bounds[index] == -1) {
                col.pack();
            } else {
                col.setWidth(bounds[index]);
            }
            col.setResizable(true);
            col.addListener(SWT.SELECTED, new Listener() {
                public void handleEvent(Event event) {
                    col.pack();
                }
            });
            index++;
        }
        tableViewer.setColumnProperties(headings);
        tableViewer.setUseHashlookup(true);

        model = new ArrayList<BiobankCollectionModel>();
        getTableViewer().setInput(model);

        if (collection != null) {
            for (int i = 0, n = collection.size(); i < n; ++i) {
                model.add(new BiobankCollectionModel());
            }
            setCollection(collection);
            setCollectionCount = 0;
        } else
            setCollectionCount = 1;
    }

    public void addDoubleClickListener(IDoubleClickListener listener) {
        tableViewer.addDoubleClickListener(listener);
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
                if (getTableViewer().getTable().isDisposed())
                    return;

                BiobankCollectionModel modelItem;
                model.clear();

                for (T item : collection) {
                    modelItem = new BiobankCollectionModel();
                    model.add(modelItem);
                    modelItem.o = item;
                }

                getTableViewer().getTable().getDisplay().asyncExec(
                    new Runnable() {
                        public void run() {
                            getTableViewer().refresh();

                            // only notify listeners if collection has been
                            // assigned other than by constructor
                            if (setCollectionCount > 0)
                                InfoTableWidget.this.notifyListeners();
                            ++setCollectionCount;
                        }
                    });
            }
        };
        t.start();
    }

    @SuppressWarnings("unchecked")
    public Collection<T> getCollection() {
        Collection<T> collection = new HashSet<T>();
        for (BiobankCollectionModel item : model) {
            collection.add((T) item.o);
        }
        return collection;
    }

}
