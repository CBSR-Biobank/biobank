package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.ContainerTypeEntryForm;
import edu.ualberta.med.biobank.forms.ContainerTypeViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;

public class ContainerTypeAdapter extends AdapterBase {

    public ContainerTypeAdapter(AdapterBase parent,
        ContainerTypeWrapper containerType) {
        super(parent, containerType);
    }

    public ContainerTypeWrapper getContainerType() {
        return (ContainerTypeWrapper) modelObject;
    }

    @Override
    public String getName() {
        ContainerTypeWrapper containerType = getContainerType();
        if (containerType == null) {
            return "loading...";
        }
        return containerType.getName();
    }

    @Override
    public String getTitle() {
        return getTitle("Container Type");
    }

    @Override
    public void performDoubleClick() {
        openForm(new FormInput(this), ContainerTypeViewForm.ID);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Container Type", ContainerTypeEntryForm.ID);
        addViewMenu(menu, "Container Type", ContainerTypeViewForm.ID);
        addDeleteMenu(menu, "Container Type",
            "Are you sure you want to delete this container type?");
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return null;
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

}
