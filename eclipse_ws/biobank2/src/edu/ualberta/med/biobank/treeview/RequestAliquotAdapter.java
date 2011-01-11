package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.RequestAliquotWrapper;
import edu.ualberta.med.biobank.treeview.admin.RequestContainerAdapter;

public class RequestAliquotAdapter implements Node {

    // variables are replicated here to avoid repeated calls to remote api

    public RequestContainerAdapter parent;
    public RequestAliquotWrapper raw;

    public RequestAliquotAdapter(RequestContainerAdapter parent,
        RequestAliquotWrapper raw) {
        this.parent = parent;
        this.raw = raw;
    }

    public RequestAliquotWrapper getAliquot() {
        return raw;
    }

    public String getLabelInternal() {
        return raw.getAliquot().getInventoryId();
    }

    public String getClaimedBy() {
        return raw.getClaimedBy();
    }

    public String getSampleType() {
        return raw.getAliquot().getSampleType().getNameShort();
    }

    public String getPosition() {
        return raw.getAliquot().getPositionString();
    }

    @Override
    public List<Object> getChildren() {
        return new ArrayList<Object>();
    }

    @Override
    public Object getParent() {
        return parent;
    }

}
