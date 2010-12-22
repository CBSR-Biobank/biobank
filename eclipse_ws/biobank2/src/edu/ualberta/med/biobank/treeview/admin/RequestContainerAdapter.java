package edu.ualberta.med.biobank.treeview.admin;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class RequestContainerAdapter extends ContainerAdapter {

    public RequestContainerAdapter(AdapterBase parent,
        ContainerWrapper container) {
        super(parent, container);
    }

    @Override
    protected String getLabelInternal() {
        return getContainer().getLabel() + " ("
            + getContainer().getContainerType().getNameShort() + ")" + " ("
            + getAliquotCount() + ")";
    }

    private Integer getAliquotCount() {
        Integer aliquots = 0;
        for (AdapterBase child : getChildren()) {
            if (child instanceof RequestContainerAdapter)
                aliquots += ((RequestContainerAdapter) child).getAliquotCount();
            else
                aliquots++;
        }
        return aliquots;
    }
}
