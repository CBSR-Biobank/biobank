package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.treeview.Node;

public class BiobankCollectionModel implements Node {
    public int index;
    public Object o;
    private Node parent;

    public BiobankCollectionModel(int index) {
        this(null, index);
    }

    public BiobankCollectionModel(Node parent, int index) {
        this.parent = parent;
        this.index = index;
        this.o = null;
    }

    @Override
    public List<Node> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public void removeChild(Node o) {

    }
}
