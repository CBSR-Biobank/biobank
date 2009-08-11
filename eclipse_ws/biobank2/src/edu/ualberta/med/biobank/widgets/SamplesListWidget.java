package edu.ualberta.med.biobank.widgets;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class SamplesListWidget extends BiobankCollectionTable {

    private static final String[] headings = new String[] { "Inventory ID",
        "Type", "Position", "Link Date", "Available", "Quantity", "Comment" };

    private static final int[] bounds = new int[] { 130, 130, 150, 150, -1, -1,
        -1 };

    private SiteAdapter siteAdapter;

    private Map<Integer, Sample> samples;

    private BiobankCollectionModel[] model;

    private Collection<Sample> sampleCollection;

    private Collection<SamplePosition> samplePositionCollection;

    private SamplesListWidget(Composite parent, int sampleSize) {
        super(parent, SWT.NONE, headings, bounds, null);
        GridData tableData = ((GridData) getLayoutData());
        tableData.heightHint = 500;

        samples = new HashMap<Integer, Sample>();
        model = new BiobankCollectionModel[sampleSize];
        for (int i = 0; i < sampleSize; ++i) {
            model[i] = new BiobankCollectionModel();
        }

        getTableViewer().setInput(model);
    }

    public SamplesListWidget(Composite parent,
        Collection<SamplePosition> samplePositionCollection) {
        this(parent, samplePositionCollection.size());
        this.samplePositionCollection = samplePositionCollection;
        setSamplePositions();
    }

    public SamplesListWidget(Composite parent, final SiteAdapter siteAdapter,
        Collection<Sample> sampleCollection) {
        this(parent, sampleCollection.size());
        this.siteAdapter = siteAdapter;
        this.sampleCollection = sampleCollection;
        assignDoubleClickListener();
        setSamples();
    }

    private void assignDoubleClickListener() {
        // if site adapter is not null, can search for another node from the
        // same site
        getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object selection = event.getSelection();
                Sample sample = (Sample) ((StructuredSelection) selection)
                    .getFirstElement();
                SamplePosition sp = sample.getSamplePosition();
                if (sp != null) {
                    Container sc = sp.getContainer();
                    AdapterBase node = siteAdapter
                        .accept(new NodeSearchVisitor(Container.class, sc
                            .getId()));
                    if (node != null) {
                        SessionManager.getInstance().getTreeViewer()
                            .setSelection(new StructuredSelection(node));
                        node.performDoubleClick();
                    }
                }
            }
        });
    }

    private void setSamples() {

        // getClinicsAdapters(clinicGroupParent, clinics)
        Thread t = new Thread() {
            @Override
            public void run() {
                int count = 0;
                for (Sample sample : sampleCollection) {
                    if (getTableViewer().getTable().isDisposed()) {
                        return;
                    }
                    final int j = count;
                    final Sample s = sample;
                    getTableViewer().getTable().getDisplay().asyncExec(
                        new Runnable() {

                            public void run() {
                                model[j].o = s;
                                samples.put(s.getId(), s);
                                getTableViewer().update(model[j], null);
                            }

                        });
                    ++count;
                }
            }
        };
        t.start();
    }

    private void setSamplePositions() {
        Thread t = new Thread() {
            @Override
            public void run() {
                int count = 0;
                for (SamplePosition samplePosition : samplePositionCollection) {
                    if (getTableViewer().getTable().isDisposed()) {
                        return;
                    }
                    final int j = count;
                    final Sample sample = samplePosition.getSample();
                    getTableViewer().getTable().getDisplay().asyncExec(
                        new Runnable() {

                            public void run() {
                                model[j].o = sample;
                                samples.put(sample.getId(), sample);
                                getTableViewer().update(model[j], null);
                            }

                        });
                    ++count;
                }
            }
        };
        t.start();
    }

    public void setSelection(Sample selectedSample) {
        if (selectedSample == null)
            return;

        // we need to get sample by ID, as the equals method from the cacore
        // object doesn't work well !
        Sample s = samples.get(selectedSample.getId());
        getTableViewer().setSelection(new StructuredSelection(s), true);
    }
}
