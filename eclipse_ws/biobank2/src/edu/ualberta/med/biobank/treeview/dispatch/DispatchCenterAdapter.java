package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.SiteViewForm;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class DispatchCenterAdapter extends AdapterBase {

    private OutgoingNode out;
    private IncomingNode inc;

    public DispatchCenterAdapter(AdapterBase parent, CenterWrapper<?> center) {
        super(parent, center, false);
        out = new OutgoingNode(this, 0, center);
        out.setParent(this);
        this.addChild(out);

        inc = new IncomingNode(this, 1, center);
        inc.setParent(this);
        this.addChild(inc);
    }

    @Override
    protected String getLabelInternal() {
        CenterWrapper<?> site = (CenterWrapper<?>) getModelObject();
        Assert.isNotNull(site, "site is null"); //$NON-NLS-1$
        return site.getNameShort();
    }

    @Override
    public String getTooltipText() {
        return getTooltipText(Messages.DispatchCenterAdapter_site_label);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return null;
    }

    @Override
    public boolean isDeletable() {
        return internalIsDeletable();
    }

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        return searchChildren(searchedObject);
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
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren() {
        return null;
    }

    @Override
    protected int getWrapperChildCount() {
        return 0;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public String getViewFormId() {
        return SiteViewForm.ID;
    }

    @Override
    public void rebuild() {
        for (AdapterBase adaper : getChildren()) {
            adaper.rebuild();
        }
    }

    @Override
    public void performDoubleClick() {

    }

}
