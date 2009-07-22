package edu.ualberta.med.biobank.widgets;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class SamplesListWidget extends BiobankCollectionTable {

    private static final String[] headings = new String[] { "Inventory ID",
        "Type", "Position", "Process Date", "Available", "Quantity", "Comment" };

    private static final int[] bounds = new int[] { 130, 130, 150, 150, -1, -1,
        -1 };

    private Map<Integer, Sample> samples;

    public SamplesListWidget(Composite parent, final SiteAdapter siteAdapter) {
        super(parent, SWT.NONE, headings, bounds, null);
        GridData tableData = ((GridData) getLayoutData());
        tableData.heightHint = 500;

        getTableViewer().setContentProvider(new ArrayContentProvider());

        if (siteAdapter != null) {
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
                        Node node = siteAdapter.accept(new NodeSearchVisitor(
                            Container.class, sc.getId()));
                        if (node != null) {
                            SessionManager.getInstance().getTreeViewer()
                                .setSelection(new StructuredSelection(node));
                            node.performDoubleClick();
                        }
                    }
                }
            });
        }
    }

    public void adaptToToolkit(FormToolkit toolkit, boolean paintBorder) {
        adaptToToolkit(toolkit);
        if (paintBorder) {
            toolkit.paintBordersFor(this);
        }
    }

    public void setSamples(Collection<Sample> sampleCollection) {
        samples = new HashMap<Integer, Sample>();
        for (Sample s : sampleCollection) {
            samples.put(s.getId(), s);
        }
        getTableViewer().setInput(samples.values());
    }

    public void setSamplePositions(
        Collection<SamplePosition> samplePositionCollection) {
        samples = new HashMap<Integer, Sample>();
        for (SamplePosition s : samplePositionCollection) {
            samples.put(s.getSample().getId(), s.getSample());
        }
        getTableViewer().setInput(samples.values());
    }

    public void setSelection(Sample selectedSample) {
        if (selectedSample != null) {
            // we need to do that, as the equals method from the cacore object
            // doesn't work well !
            Sample s = samples.get(selectedSample.getId());
            getTableViewer().setSelection(new StructuredSelection(s), true);
        }
    }
}
