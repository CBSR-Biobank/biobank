package edu.ualberta.med.biobank.forms.utils;

import java.util.List;
import java.util.regex.Pattern;

import edu.ualberta.med.biobank.common.util.ItemState;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.Node;

public abstract class TableGroup<T extends ModelWrapper<?>> implements Node {

    protected Integer numAliquots = 0;
    protected ItemState state;
    protected List<Node> tops;
    protected static final Pattern p = Pattern.compile("/");
    protected Object parent = null;

    protected TableGroup(ItemState state, T request) {
        this.state = state;
        createAdapterTree(state.getId(), request);
    }

    @Override
    public String toString() {
        return state.getLabel();
    }

    public String getTitle() {
        return state.getLabel() + " (" + numAliquots + ")";
    }

    protected abstract void createAdapterTree(Integer state, T request);

    @Override
    public List<Node> getChildren() {
        return tops;
    }

    @Override
    public Object getParent() {
        return parent;
    }
}
