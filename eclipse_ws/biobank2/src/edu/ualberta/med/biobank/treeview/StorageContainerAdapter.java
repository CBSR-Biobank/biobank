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
import edu.ualberta.med.biobank.forms.StorageContainerViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.StorageContainer;

public class StorageContainerAdapter extends Node {

	private StorageContainer storageContainer;

	public StorageContainerAdapter(Node parent,
			StorageContainer storageContainer) {
		super(parent);
		this.storageContainer = storageContainer;
	}

	@Override
	public Integer getId() {
		Assert.isNotNull(storageContainer, "storageContainer is null");
		return storageContainer.getId();
	}

	@Override
	public String getName() {
		Assert.isNotNull(storageContainer, "storageContainer is null");
		return storageContainer.getName();
	}

	@Override
	public String getTitle() {
		return getTitle("Storage Container");
	}

	@Override
	public void performDoubleClick() {
		openForm(new FormInput(this), StorageContainerViewForm.ID);
	}

	public StorageContainer getStorageContainer() {
		return storageContainer;
	}

	public void setStorageContainer(StorageContainer storageContainer) {
		this.storageContainer = storageContainer;
	}

	@Override
	public void performExpand() {
	}

	@Override
	public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
		MenuItem mi = new MenuItem(menu, SWT.PUSH);
		mi.setText("Edit Storage Container");
		mi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				openForm(new FormInput(StorageContainerAdapter.this),
					StorageContainerEntryForm.ID);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		mi = new MenuItem(menu, SWT.PUSH);
		mi.setText("View Storage Container");
		mi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				openForm(new FormInput(StorageContainerAdapter.this),
					StorageContainerViewForm.ID);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	@Override
	public void loadChildren() {

	}

	@Override
	public Node accept(NodeSearchVisitor visitor) {
		return null;
	}

}
