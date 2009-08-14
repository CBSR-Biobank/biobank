package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.SampleStorage;

public class SampleStorageInfoTable extends BiobankCollectionTable {

    private static final String[] headings = new String[] { "Sample type",
        "Volume (ml)", "Quantity" };

    private static final int[] bounds = new int[] { 300, 130, 100, -1, -1, -1,
        -1 };

    private List<BiobankCollectionModel> model;

    public SampleStorageInfoTable(Composite parent,
        Collection<SampleStorage> sampleStorageCollection) {
        super(parent, SWT.NONE, headings, bounds, null);
        int size = sampleStorageCollection.size();

        model = new ArrayList<BiobankCollectionModel>();
        for (int i = 0; i < size; ++i) {
            model.add(new BiobankCollectionModel());
        }

        getTableViewer().setInput(model);
        getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
            }
        });

        setSampleStorage(sampleStorageCollection);
    }

    public void setSampleStorage(
        final Collection<SampleStorage> sampleStorageCollection) {

        Thread t = new Thread() {
            @Override
            public void run() {
                if (getTableViewer().getTable().isDisposed())
                    return;

                BiobankCollectionModel item;
                model.clear();

                for (SampleStorage sampleStorage : sampleStorageCollection) {
                    item = new BiobankCollectionModel();
                    model.add(item);
                    item.o = sampleStorage;
                }

                getTableViewer().getTable().getDisplay().asyncExec(
                    new Runnable() {
                        public void run() {
                            getTableViewer().refresh();
                        }
                    });
            }
        };
        t.start();
    }

    public Collection<SampleStorage> getSampleStorage() {
        Collection<SampleStorage> sampleStorage = new HashSet<SampleStorage>();
        for (BiobankCollectionModel item : model) {
            sampleStorage.add((SampleStorage) item.o);
        }
        return sampleStorage;
    }
}
