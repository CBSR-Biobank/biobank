package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.forms.ContainerTypeEntryForm;
import edu.ualberta.med.biobank.forms.ContainerTypeViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.ContainerType;

public class ContainerTypeAdapter extends AdapterBase {

    public ContainerTypeAdapter(AdapterBase parent, ContainerType containerType) {
        super(parent, containerType, ContainerType.class);
        this.setContainerType(containerType);
    }

    public void setContainerType(ContainerType containerType) {
        setWrappedObject(containerType, ContainerType.class);
    }

    public ContainerType getContainerType() {
        return (ContainerType) getWrappedObject();
    }

    @Override
    protected Integer getWrappedObjectId() {
        return getContainerType().getId();
    }

    @Override
    public Integer getId() {
        ContainerType containerType = getContainerType();
        Assert.isNotNull(containerType, "storage type is null");
        return containerType.getId();
    }

    @Override
    public String getName() {
        ContainerType containerType = getContainerType();
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
    }

    @Override
    public void loadChildren(boolean updateNode) {

    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return null;
    }

    @Override
    protected boolean integrityCheck() {
        return true;
    }

}
