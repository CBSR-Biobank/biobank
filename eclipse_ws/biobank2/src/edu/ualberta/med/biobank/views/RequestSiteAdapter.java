package edu.ualberta.med.biobank.views;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.SiteViewForm;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.request.AcceptedRequestNode;
import edu.ualberta.med.biobank.treeview.request.ApprovedRequestNode;
import edu.ualberta.med.biobank.treeview.request.FilledRequestNode;
import edu.ualberta.med.biobank.treeview.request.ShippedRequestNode;

public class RequestSiteAdapter extends AdapterBase {

    private ApprovedRequestNode approvedNode;
    private AcceptedRequestNode acceptedNode;
    private FilledRequestNode filledNode;
    private ShippedRequestNode shippedNode;

    public RequestSiteAdapter(AdapterBase parent, SiteWrapper site) {
        super(parent, site, false);

        approvedNode = new ApprovedRequestNode(this, 0, site);
        approvedNode.setParent(this);
        this.addChild(approvedNode);

        acceptedNode = new AcceptedRequestNode(this, 1, site);
        acceptedNode.setParent(this);
        this.addChild(acceptedNode);

        filledNode = new FilledRequestNode(this, 2, site);
        filledNode.setParent(this);
        this.addChild(filledNode);

        shippedNode = new ShippedRequestNode(this, 3, site);
        shippedNode.setParent(this);
        this.addChild(shippedNode);

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
    public AdapterBase search(Object searchedObject) {
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
