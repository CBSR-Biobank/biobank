package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.SampleStorage;

public class SampleStorageInfoTable extends BiobankCollectionTable {

    private static final String[] headings = new String[] { "Sample type",
        "Volume", "Quantity" };

    private static final int[] bounds = new int[] { 300, 130, 100, -1, -1, -1,
        -1 };

    private Map<Integer, SampleStorage> sampleStorageMap;

    private List<BiobankCollectionModel> model;

    public SampleStorageInfoTable(Composite parent,
        Collection<SampleStorage> sampleStorageCollection) {
        super(parent, SWT.NONE, headings, bounds, null);
        int size = sampleStorageCollection.size();

        sampleStorageMap = new HashMap<Integer, SampleStorage>();
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
                BiobankCollectionModel item;
                int count = 0;
                for (SampleStorage sampleStorage : sampleStorageCollection) {
                    if (getTableViewer().getTable().isDisposed()) {
                        return;
                    }
                    if (count < model.size()) {
                        item = model.get(count);
                    } else {
                        item = new BiobankCollectionModel();
                        model.add(item);

                    }
                    item.o = sampleStorage;
                    sampleStorageMap.put(sampleStorage.getId(), sampleStorage);

                    getTableViewer().getTable().getDisplay().asyncExec(
                        new Runnable() {

                            public void run() {
                                getTableViewer().refresh();
                            }

                        });
                    ++count;
                }
            }
        };
        t.start();
    }
}
