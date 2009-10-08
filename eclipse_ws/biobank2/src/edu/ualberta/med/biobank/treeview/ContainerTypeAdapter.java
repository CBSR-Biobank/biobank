package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.forms.ContainerTypeEntryForm;
import edu.ualberta.med.biobank.forms.ContainerTypeViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;

public class ContainerTypeAdapter extends AdapterBase {

    public ContainerTypeAdapter(AdapterBase parent,
        ContainerTypeWrapper containerType) {
        super(parent, containerType);
    }

    public void setContainerType(ContainerTypeWrapper containerType) {
        object = containerType;
    }

    public ContainerTypeWrapper getContainerType() {
        return (ContainerTypeWrapper) object;
    }

    @Override
    public String getName() {
        ContainerTypeWrapper containerType = getContainerType();
        Assert.isNotNull(containerType, "storage type is null");
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
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Edit Container Type");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(ContainerTypeAdapter.this),
                    ContainerTypeEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("View Container Type");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(ContainerTypeAdapter.this),
                    ContainerTypeViewForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Delete Container Type");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                delete("Are you sure you want to delete this container type?");
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {

    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return null;
    }

}
