package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.forms.StorageContainerEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.StorageContainer;

public class StorageContainerAdapter extends Node {
    
    private StorageContainer storageContainer;

    public StorageContainerAdapter(Node parent, StorageContainer storageContainer) {
        super(parent);
        this.storageContainer = storageContainer;
    }

    @Override
    public int getId() {
        Assert.isNotNull(storageContainer, "storageContainer is null");
        Object o = (Object) storageContainer.getId();
        if (o == null) return 0;
        return storageContainer.getId();
    }

    @Override
    public String getName() {
        Assert.isNotNull(storageContainer, "storageContainer is null");
        Object o = (Object) storageContainer.getName();
        if (o == null) return null;
        return storageContainer.getName();
    }
    
    public void performDoubleClick() {
        //openForm(new FormInput(this), StorageContainerViewForm.ID);
    }
    
    public StorageContainer getStorageContainer() {
        return storageContainer;
    }
    
    public void popupMenu(TreeViewer tv, Tree tree,  Menu menu) {
        MenuItem mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("Edit Storage Container");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(StorageContainerAdapter.this), 
                    StorageContainerEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        });

        mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("View Storage Container");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                //openForm(new FormInput(StorageContainerAdapter.this), 
                //    StorageContainerViewForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        }); 
    }

}
