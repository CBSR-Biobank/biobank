package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class SamplesListWidget extends InfoTableWidget<Sample> {

    private static final String[] HEADINGS = new String[] { "Inventory ID",
        "Type", "Position", "Link Date", "Quantity (ml)", "Quantity Used",
        "Comment" };

    private static final int[] BOUNDS = new int[] { 130, 130, 150, 150, -1, -1,
        -1 };

    private SiteAdapter siteAdapter;

    private Map<Integer, Sample> samples;

    private SamplesListWidget(Composite parent) {
        super(parent, null, HEADINGS, BOUNDS);
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
                        SessionManager.getInstance().setSelectedNode(node);
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

        // Initialise collection
        for (int i = 0, n = samplePositionCollection.size(); i < n; ++i) {
            model.add(new BiobankCollectionModel());
        }
        getTableViewer().refresh();

        Thread t = new Thread() {
            @Override
            public void run() {
                final TableViewer viewer = getTableViewer();
                if (viewer.getTable().isDisposed())
                    return;

                Display display = viewer.getTable().getDisplay();
                int count = 0;

                for (SamplePosition position : samplePositionCollection) {
                    final BiobankCollectionModel modelItem = model.get(count);
                    Sample sample = position.getSample();
                    sample.getInventoryId();
                    sample.getSampleType().getName();
                    SampleWrapper.getPositionString(sample);
                    sample.getLinkDate();
                    sample.getQuantity();
                    sample.getQuantityUsed();
                    sample.getComment();
                    modelItem.o = sample;
                    ++count;

                    display.asyncExec(new Runnable() {
                        public void run() {
                            viewer.refresh(modelItem, false);
                        }
                    });
                }
                // launchAsyncRefresh();
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
