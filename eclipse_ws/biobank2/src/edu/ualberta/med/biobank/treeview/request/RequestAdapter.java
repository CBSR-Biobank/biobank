package edu.ualberta.med.biobank.treeview.request;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.forms.RequestEntryForm;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class RequestAdapter extends AdapterBase {

    public RequestAdapter(AdapterBase parent, RequestWrapper ship) {
        super(parent, ship);
        this.isReadable = true;
        this.isDeletable = false;
        this.isEditable = false;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getLabelInternal() {
        RequestWrapper shipment = (RequestWrapper) getModelObject();
        Assert.isNotNull(shipment, "Request is null");
        ResearchGroupWrapper study = shipment.getResearchGroup();
        String label = shipment.getId() + " - ";
        label += study.getNameShort() + " - ";
        label += DateFormatter.formatAsDate(shipment.getCreated());
        return label;

    }

    @Override
    public String getTooltipTextInternal() {
        return getTooltipText(Request.NAME.singular().toString());
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addViewMenu(menu, Request.NAME.singular().toString());
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
    public String getViewFormId() {
        return RequestEntryForm.ID;
    }

    @Override
    public String getEntryFormId() {
        return RequestEntryForm.ID;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof RequestAdapter)
            return internalCompareTo(o);
        return 0;
    }
}
