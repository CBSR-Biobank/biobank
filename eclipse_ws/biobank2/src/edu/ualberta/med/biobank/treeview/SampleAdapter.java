package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.forms.SampleViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Sample;

public class SampleAdapter extends AdapterBase {

    public SampleAdapter(AdapterBase parent, Sample sample) {
        super(parent, sample, Sample.class);
    }

    public void setSample(Sample sample) {
        setWrappedObject(sample, Sample.class);
    }

    public Sample getSample() {
        return (Sample) getWrappedObject();
    }

    @Override
    protected Integer getModelObjectId() {
        return getSample().getId();
    }

    @Override
    public void addChild(AdapterBase child) {
        Assert.isTrue(false, "Cannot add children to this adapter");
    }

    @Override
    public Integer getId() {
        Sample sample = getSample();
        Assert.isNotNull(sample, "Sample is null");
        return sample.getId();
    }

    @Override
    public String getName() {
        Sample sample = getSample();
        Assert.isNotNull(sample, "Clinic is null");
        return sample.getInventoryId();
    }

    @Override
    public String getTitle() {
        return getTitle("Sample");
    }

    @Override
    public void performDoubleClick() {
        openForm(new FormInput(this), SampleViewForm.ID);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("View Sample");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(SampleAdapter.this), SampleViewForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return null;
    }

    @Override
    protected boolean integrityCheck() {
        return true;
    }

}
