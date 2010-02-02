package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.forms.SampleViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SampleAdapter extends AdapterBase {

    public SampleAdapter(AdapterBase parent, SampleWrapper sample) {
        super(parent, sample);
    }

    public SampleWrapper getSample() {
        return (SampleWrapper) modelObject;
    }

    @Override
    public void addChild(AdapterBase child) {
        Assert.isTrue(false, "Cannot add children to this adapter");
    }

    @Override
    public String getName() {
        SampleWrapper sample = getSample();
        Assert.isNotNull(sample, "Clinic is null");
        return sample.getInventoryId();
    }

    @Override
    public String getTitle() {
        return getTitle("Sample");
    }

    @Override
    public WritableApplicationService getAppService() {
        return modelObject.getAppService();
    }

    @Override
    public void performDoubleClick() {
        openForm(new FormInput(this), SampleViewForm.ID);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addViewMenu(menu, "Sample", SampleViewForm.ID);
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return null;
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

}
