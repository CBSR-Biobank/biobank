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
import edu.ualberta.med.biobank.treeview.order.AcceptedOrderNode;
import edu.ualberta.med.biobank.treeview.order.ApprovedOrderNode;
import edu.ualberta.med.biobank.treeview.order.FilledOrderNode;
import edu.ualberta.med.biobank.treeview.order.ShippedOrderNode;

public class OrderSiteAdapter extends AdapterBase {

    private ApprovedOrderNode approvedOrderNode;
    private AcceptedOrderNode acceptedNode;
    private FilledOrderNode filledOrderNode;
    private ShippedOrderNode shippedOrderNode;

    public OrderSiteAdapter(AdapterBase parent, SiteWrapper site) {
        super(parent, site, false);

        approvedOrderNode = new ApprovedOrderNode(this, 0, site);
        approvedOrderNode.setParent(this);
        this.addChild(approvedOrderNode);

        acceptedNode = new AcceptedOrderNode(this, 1, site);
        acceptedNode.setParent(this);
        this.addChild(acceptedNode);

        filledOrderNode = new FilledOrderNode(this, 2, site);
        filledOrderNode.setParent(this);
        this.addChild(filledOrderNode);

        shippedOrderNode = new ShippedOrderNode(this, 3, site);
        shippedOrderNode.setParent(this);
        this.addChild(shippedOrderNode);

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
