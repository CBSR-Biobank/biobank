package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.treeview.admin.RequestContainerAdapter;

public class RequestAliquotAdapter implements Node {

    // variables are replicated here to avoid repeated calls to remote api

    public RequestContainerAdapter parent;
    public RequestSpecimenWrapper raw;

    public RequestAliquotAdapter(RequestContainerAdapter parent,
        RequestSpecimenWrapper raw) {
        this.parent = parent;
        this.raw = raw;
    }

    public RequestSpecimenWrapper getSpecimen() {
        return raw;
    }

    public String getLabelInternal() {
        return raw.getSpecimen().getInventoryId();
    }

    public String getClaimedBy() {
        return raw.getClaimedBy();
    }

    public String getSpecimenType() {
        return raw.getSpecimen().getSpecimenType().getNameShort();
    }

    public String getPosition() {
        return raw.getSpecimen().getPositionString();
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
