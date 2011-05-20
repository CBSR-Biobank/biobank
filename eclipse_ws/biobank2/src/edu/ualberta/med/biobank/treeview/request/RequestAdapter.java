package edu.ualberta.med.biobank.treeview.request;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.RequestEntryForm;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class RequestAdapter extends AdapterBase {

    public RequestAdapter(AdapterBase parent, RequestWrapper ship) {
        super(parent, ship);
    }

    public RequestWrapper getWrapper() {
        return (RequestWrapper) modelObject;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    protected String getLabelInternal() {
        RequestWrapper shipment = getWrapper();
        Assert.isNotNull(shipment, "Request is null");
        StudyWrapper study = shipment.getStudy();
        String label = shipment.getId() + " - ";
        label += study.getNameShort() + " - ";
        label += DateFormatter.formatAsDate(shipment.getCreated());
        return label;

    }

    @Override
    public String getTooltipText() {
        return getTooltipText("Request");
    }

    @Override
    public boolean isDeletable() {
        return false;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addViewMenu(menu, "Request");
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
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getViewFormId() {
        return RequestEntryForm.ID;
    }

    @Override
    public String getEntryFormId() {
        return RequestEntryForm.ID;
    }

}
