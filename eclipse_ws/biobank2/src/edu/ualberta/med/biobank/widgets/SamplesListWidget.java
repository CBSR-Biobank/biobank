package edu.ualberta.med.biobank.widgets;

import java.util.Collection;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.SessionAdapter;

public class SamplesListWidget extends BiobankCollectionTable {

    private static final String[] headings = new String[] { "Inventory ID",
        "Type", "Position", "Process Date", "Available", "Available quantity",
        "Comment" };

    private static final int[] bounds = new int[] { 130, 130, 150, 150, -1, -1,
        -1 };

    public SamplesListWidget(Composite parent,
        final SessionAdapter sessionAdapter) {
        super(parent, SWT.NONE, headings, bounds, null);
        GridData tableData = ((GridData) getLayoutData());
        tableData.heightHint = 500;

        if (sessionAdapter != null) {
            getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
                @Override
                public void doubleClick(DoubleClickEvent event) {
                    Object selection = event.getSelection();
                    Sample sample = (Sample) ((StructuredSelection) selection)
                        .getFirstElement();
                    SamplePosition sp = sample.getSamplePosition();
                    if (sp != null) {
                        Container sc = sp.getContainer();
                        Node node = sessionAdapter
                            .accept(new NodeSearchVisitor(
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
        getTableViewer().setInput(getSamplesArray(sampleCollection));
    }

    public void setSamplesFromPositions(
        Collection<SamplePosition> samplePositions) {
        getTableViewer().setInput(getSamplesArrayFromPosition(samplePositions));
    }

    private Sample[] getSamplesArray(Collection<Sample> samples) {
        Sample[] samplesArray = new Sample[samples.size()];
        int i = 0;
        for (Sample s : samples) {
            samplesArray[i] = s;
            i++;
        }
        return samplesArray;
    }

    private Sample[] getSamplesArrayFromPosition(
        Collection<SamplePosition> samplePositions) {
        if (samplePositions.size() == 0) {
            return new Sample[0];
        }

        Sample[] samples = new Sample[samplePositions.size()];
        int i = 0;
        for (SamplePosition sp : samplePositions) {
            samples[i] = sp.getSample();
            i++;
        }
        return samples;
    }

}
