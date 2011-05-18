package edu.ualberta.med.biobank.treeview.admin;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;

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
        return container.getLabel() + " ("
            + container.getContainerType().getNameShort() + ")" + " ("
            + getSpecimenCount() + ")";
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

    public List<RequestSpecimenWrapper> getSpecimenChildren() {
        List<RequestSpecimenWrapper> specs = new ArrayList<RequestSpecimenWrapper>();
        for (Object child : getChildren()) {
            if (child instanceof RequestContainerAdapter)
                specs.addAll(((RequestContainerAdapter) child)
                    .getSpecimenChildren());
            else
                specs.add((RequestSpecimenWrapper) ((TreeItemAdapter) child)
                    .getSpecimen());
        }
        return specs;
    }
}
