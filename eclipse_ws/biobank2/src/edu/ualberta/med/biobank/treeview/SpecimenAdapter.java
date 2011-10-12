package edu.ualberta.med.biobank.treeview;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.SpecimenEntryForm;
import edu.ualberta.med.biobank.forms.SpecimenViewForm;

public class SpecimenAdapter extends AdapterBase {

    public SpecimenAdapter(AdapterBase parent, SpecimenWrapper sample) {
        super(parent, sample);
    }

    @Override
    public void addChild(AbstractAdapterBase child) {
        Assert.isTrue(false, "Cannot add children to this adapter"); //$NON-NLS-1$
    }

    @Override
    protected String getLabelInternal() {
        Assert.isNotNull(getModelObject(), "specimen is null"); //$NON-NLS-1$
        return ((SpecimenWrapper) getModelObject()).getInventoryId();
    }

    @Override
    public String getTooltipTextInternal() {
        return getTooltipText(Messages.SpecimenAdapter_specimen_label);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addViewMenu(menu, Messages.SpecimenAdapter_specimen_label);
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        return null;
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getEntryFormId() {
        return SpecimenEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return SpecimenViewForm.ID;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof SpecimenAdapter)
            return internalCompareTo(o);
        return 0;
    }
}
