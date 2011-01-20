package edu.ualberta.med.biobank.views;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.SiteViewForm;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.dispatch.IncomingNode;
import edu.ualberta.med.biobank.treeview.dispatch.OutgoingNode;

public class DispatchSiteAdapter extends AdapterBase {

    private OutgoingNode out;
    private IncomingNode inc;

    public DispatchSiteAdapter(AdapterBase parent, SiteWrapper site) {
        super(parent, site, false);
        out = new OutgoingNode(this, 0, site);
        out.setParent(this);
        this.addChild(out);

        inc = new IncomingNode(this, 1, site);
        inc.setParent(this);
        this.addChild(inc);
    }

    public SiteWrapper getWrapper() {
        return (SiteWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        SiteWrapper site = getWrapper();
        Assert.isNotNull(site, "site is null");
        return site.getNameShort();
    }

    @Override
    public String getTooltipText() {
        return getTooltipText("Repository Site");
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
