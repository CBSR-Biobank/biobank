package edu.ualberta.med.biobank.treeview.request;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;

public class RequestContainerAdapter implements Node {

    public Node parent;
    public ContainerWrapper container;
    List<Node> children;

    public RequestContainerAdapter(Node parent, ContainerWrapper container) {
        this.parent = parent;
        this.container = container;
        this.children = new ArrayList<Node>();
    }

    @Override
    public Node getParent() {
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

    @Override
    public void removeChild(Node c) {
        children.remove(c);
    }

    public void setParent(Node p) {
        this.parent = p;
    }

    public List<TreeItemAdapter> getSpecimenChildren() {
        List<TreeItemAdapter> specs = new ArrayList<TreeItemAdapter>();
        for (Object child : getChildren()) {
            if (child instanceof RequestContainerAdapter)
                specs.addAll(((RequestContainerAdapter) child)
                    .getSpecimenChildren());
            else
                specs.add((TreeItemAdapter) child);
        }
        return specs;
    }

}
