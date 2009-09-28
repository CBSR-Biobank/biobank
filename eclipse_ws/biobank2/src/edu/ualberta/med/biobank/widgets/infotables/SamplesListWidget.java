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
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SamplesListWidget extends InfoTableWidget<SampleWrapper> {

    private static final String[] HEADINGS = new String[] { "Inventory ID",
        "Type", "Position", "Link Date", "Quantity (ml)", "Quantity Used",
        "Comment" };

    private static final int[] BOUNDS = new int[] { 130, 130, 150, 150, -1, -1,
        -1 };

    private WritableApplicationService appService;

    private SiteAdapter siteAdapter;

    private Map<Integer, SampleWrapper> samples;

    private SamplesListWidget(Composite parent) {
        super(parent, null, HEADINGS, BOUNDS);
        GridData tableData = ((GridData) getLayoutData());
        tableData.heightHint = 500;
        samples = new HashMap<Integer, SampleWrapper>();
    }

    public SamplesListWidget(Composite parent,
        WritableApplicationService appService,
        Collection<SamplePosition> samplePositionCollection) {
        this(parent);
        this.appService = appService;
        setSamplePositions(samplePositionCollection);
    }

    public SamplesListWidget(Composite parent, SiteAdapter siteAdapter,
        Collection<SampleWrapper> sampleCollection) {
        this(parent);
        this.siteAdapter = siteAdapter;
        assignDoubleClickListener();

        // Initialise collection
        for (SampleWrapper sampleWrapper : sampleCollection) {
            model.add(new BiobankCollectionModel());
            samples.put(sampleWrapper.getId(), sampleWrapper);
        }
        getTableViewer().refresh();
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
                Display display = viewer.getTable().getDisplay();
                int count = 0;

                try {
                    for (SamplePosition position : samplePositionCollection) {
                        if (viewer.getTable().isDisposed())
                            return;

                        final BiobankCollectionModel modelItem = model
                            .get(count);
                        SampleWrapper w = new SampleWrapper(appService,
                            position.getSample());
                        w.loadAttributes();
                        modelItem.o = w.getWrappedObject();
                        ++count;

                        display.asyncExec(new Runnable() {
                            public void run() {
                                if (!viewer.getTable().isDisposed())
                                    viewer.refresh(modelItem, false);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
        SampleWrapper sw = samples.get(selectedSample.getId());
        getTableViewer().setSelection(new StructuredSelection(sw), true);
    }
}
