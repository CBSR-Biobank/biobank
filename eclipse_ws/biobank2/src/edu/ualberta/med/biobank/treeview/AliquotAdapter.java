package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.AliquotEntryForm;
import edu.ualberta.med.biobank.forms.AliquotViewForm;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class AliquotAdapter extends AdapterBase {

    public AliquotAdapter(AdapterBase parent, AliquotWrapper sample) {
        super(parent, sample);
    }

    public AliquotWrapper getAliquot() {
        return (AliquotWrapper) modelObject;
    }

    @Override
    public void addChild(AdapterBase child) {
        Assert.isTrue(false, "Cannot add children to this adapter");
    }

    @Override
    protected String getLabelInternal() {
        AliquotWrapper aliquot = getAliquot();
        Assert.isNotNull(aliquot, "sample is null");
        return aliquot.getInventoryId();
    }

    @Override
    public String getTooltipText() {
        return getTooltipText("Aliquot");
    }

    @Override
    public WritableApplicationService getAppService() {
        return modelObject.getAppService();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addViewMenu(menu, "Aliquot");
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

    @Override
    public String getEntryFormId() {
        return AliquotEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return AliquotViewForm.ID;
    }

}
