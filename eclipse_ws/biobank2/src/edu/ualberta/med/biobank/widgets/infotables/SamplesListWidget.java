package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class SamplesListWidget extends InfoTableWidget<SampleWrapper> {

    private static final String[] HEADINGS = new String[] { "Inventory ID",
        "Type", "Position", "Link Date", "Quantity (ml)", "Quantity Used",
        "Comment" };

    private static final int[] BOUNDS = new int[] { 130, 130, 150, 150, -1, -1,
        -1 };

    private SiteAdapter siteAdapter;

    public SamplesListWidget(Composite parent,
        Collection<SampleWrapper> sampleCollection) {
        this(parent, sampleCollection, null);
    }

    public SamplesListWidget(Composite parent,
        Collection<SampleWrapper> sampleCollection, SiteAdapter siteAdapter) {
        super(parent, sampleCollection, HEADINGS, BOUNDS);
        GridData tableData = ((GridData) getLayoutData());
        tableData.heightHint = 500;
        this.siteAdapter = siteAdapter;
        if (siteAdapter != null) {
            assignDoubleClickListener();
        }
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
                    .isTrue(item.o instanceof SampleWrapper,
                        "Invalid class where sample expected: "
                            + item.o.getClass());

                SampleWrapper sample = (SampleWrapper) item.o;
                if (sample.hasParent()) {
                    AdapterBase node = siteAdapter
                        .accept(new NodeSearchVisitor(Container.class, sample
                            .getParent().getId()));
                    if (node != null) {
                        SessionManager.getInstance().setSelectedNode(node);
                        node.performDoubleClick();
                    }
                }
            }
        });
    }

    @Override
    public Object getCollectionModelObject(SampleWrapper sample)
        throws Exception {
        sample.loadAttributes();
        return sample;

    }

    public void setSelection(SampleWrapper selectedSample) {
        if (selectedSample == null)
            return;
        getTableViewer().setSelection(new StructuredSelection(selectedSample),
            true);
    }
}
