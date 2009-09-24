package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class SamplesListWidget extends InfoTableWidget<Sample> {

    private static final String[] headings = new String[] { "Inventory ID",
        "Type", "Position", "Link Date", "Quantity (ml)", "Quantity Used",
        "Comment" };

    private static final int[] bounds = new int[] { 130, 130, 150, 150, -1, -1,
        -1 };

    private SiteAdapter siteAdapter;

    private Map<Integer, Sample> samples;

    private SamplesListWidget(Composite parent) {
        super(parent, null, headings, bounds);
        GridData tableData = ((GridData) getLayoutData());
        tableData.heightHint = 500;
        samples = new HashMap<Integer, Sample>();
    }

    public SamplesListWidget(Composite parent,
        Collection<SamplePosition> samplePositionCollection) {
        this(parent);
        setSamplePositions(samplePositionCollection);
    }

    public SamplesListWidget(Composite parent, final SiteAdapter siteAdapter,
        Collection<Sample> sampleCollection) {
        this(parent);
        this.siteAdapter = siteAdapter;
        assignDoubleClickListener();
        setCollection(sampleCollection);
    }

    private void assignDoubleClickListener() {
        // if site adapter is not null, can search for another node from the
        // same site
        addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object selection = event.getSelection();
                BiobankCollectionModel item = (BiobankCollectionModel) ((StructuredSelection) selection)
                    .getFirstElement();
                Assert
                    .isTrue(item.o instanceof Sample,
                        "Invalid class where sample expected: "
                            + item.o.getClass());

                Sample sample = (Sample) item.o;
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

    private void setSamplePositions(
        final Collection<SamplePosition> samplePositionCollection) {
        if (samplePositionCollection == null)
            return;

        Thread t = new Thread() {
            @Override
            public void run() {
                if (getTableViewer().getTable().isDisposed())
                    return;

                BiobankCollectionModel modelItem;
                model.clear();

                for (SamplePosition position : samplePositionCollection) {
                    modelItem = new BiobankCollectionModel();
                    model.add(modelItem);
                    modelItem.o = position.getSample();
                }
                launchAsyncRefresh();
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
