package edu.ualberta.med.biobank.treeview;

/**
 * Base class for all "Session" tree view nodes. Generally, most of the nodes in
 * the tree are adapters for classes in the ORM model.
 */
public abstract class AbstractNewAdapterBase extends AbstractAdapterBase {

    public AbstractNewAdapterBase(AbstractAdapterBase parent, int id,
        String label, boolean hasChildren) {
        super(parent, id, label, hasChildren);
        // TODO Auto-generated constructor stub
    }

}