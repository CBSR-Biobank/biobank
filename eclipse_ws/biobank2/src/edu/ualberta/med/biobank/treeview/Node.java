package edu.ualberta.med.biobank.treeview;

import java.util.List;

public interface Node {

    public List<Object> getChildren();

    public Object getParent();

}
