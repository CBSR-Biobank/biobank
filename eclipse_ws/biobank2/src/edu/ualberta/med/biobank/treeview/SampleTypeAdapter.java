package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.model.SampleType;

public class SampleTypeAdapter extends AdapterBase {

    public SampleTypeAdapter(AdapterBase parent, SampleType sampleType) {
        super(parent, sampleType, SampleType.class);
        setHasChildren(true);
    }

    public SampleType getSampleType() {
        return (SampleType) getWrappedObject();
    }

    public void setSampleType(SampleType sampleType) {
        setWrappedObject(sampleType, SampleType.class);
    }

    @Override
    protected Integer getWrappedObjectId() {
        return getSampleType().getId();
    }

    @Override
    public Integer getId() {
        SampleType sampleType = getSampleType();
        Assert.isNotNull(sampleType, "patient is null");
        return sampleType.getId();
    }

    @Override
    public String getName() {
        SampleType sampleType = getSampleType();
        Assert.isNotNull(sampleType, "storage type is null");
        return sampleType.getNameShort();
    }

    @Override
    public void performDoubleClick() {
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {

    }

    @Override
    public void loadChildren(boolean updateNode) {
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getTitle() {
        return getTitle("Sample Type");
    }

    @Override
    protected boolean integrityCheck() {
        return true;
    }
}
