package edu.ualberta.med.biobank.treeview;

import java.util.List;

public interface Node {

    public List<Node> getChildren();

    public Node getParent();

    public void removeChild(Node o);

}
