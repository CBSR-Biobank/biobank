package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class InfoTableWidget<T> extends BiobankCollectionTable {

    // FIXME - used to inform listeners of changes to the widget
    // could be done in a better way
    private int setCollectionCount;

    private List<BiobankCollectionModel> model;

    public InfoTableWidget(Composite parent, Collection<T> collection,
        String[] headings, int[] bounds) {
        super(parent, SWT.NONE, headings, bounds, null);
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
        getTableViewer().addDoubleClickListener(listener);
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
