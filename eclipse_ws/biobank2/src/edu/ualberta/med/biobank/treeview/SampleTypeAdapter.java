package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;

public class SampleTypeAdapter extends AdapterBase {

    public SampleTypeAdapter(AdapterBase parent, SampleTypeWrapper sampleType) {
        super(parent, sampleType);
        setHasChildren(true);
    }

    public SampleTypeWrapper getSampleType() {
        return (SampleTypeWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        SampleTypeWrapper sampleType = getSampleType();
        Assert.isNotNull(sampleType, "sample type is null");
        return sampleType.getNameShort();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {

    }

    @Override
    public String getTooltipText() {
        return getTooltipText("Aliquot Type");
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

}
