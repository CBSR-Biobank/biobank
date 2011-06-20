package edu.ualberta.med.biobank.treeview.admin;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.treeview.Node;

public class RequestContainerAdapter implements Node {

    public Object parent;
    public ContainerWrapper container;
    List<Node> children;

    public RequestContainerAdapter(Object parent, ContainerWrapper container) {
        this.parent = parent;
        this.container = container;
        this.children = new ArrayList<Node>();
    }

    @Override
    public Object getParent() {
        return parent;
    }

    public boolean hasChildren() {
        return getChildren().size() != 0;
    }

    @Override
    public List<Node> getChildren() {
        return children;
    }

    public String getLabelInternal() {
        return container.getLabel() + " (" //$NON-NLS-1$
            + container.getContainerType().getNameShort() + ")" + " (" //$NON-NLS-1$ //$NON-NLS-2$
            + getSpecimenCount() + ")"; //$NON-NLS-1$
    }

    private Integer getSpecimenCount() {
        Integer specimens = 0;
        for (Object child : getChildren()) {
            if (child instanceof RequestContainerAdapter)
                specimens += ((RequestContainerAdapter) child)
                    .getSpecimenCount();
            else
                specimens++;
        }
        return specimens;
    }

    public void addChild(Node c) {
        children.add(c);
    }
}
