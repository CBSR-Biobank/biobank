package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.forms.StorageTypeEntryForm;
import edu.ualberta.med.biobank.forms.StorageTypeViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.StorageType;

public class StorageTypeAdapter extends Node {

	private StorageType storageType;

	public StorageTypeAdapter(Node parent, StorageType storageType) {
		super(parent);
		this.setStorageType(storageType);
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}

	public StorageType getStorageType() {
		return storageType;
	}

	@Override
	public Integer getId() {
		Assert.isNotNull(storageType, "storage type is null");
		return storageType.getId();
	}

	@Override
	public String getName() {
		Assert.isNotNull(storageType, "storage type is null");
		return storageType.getName();
	}

	@Override
	public String getTitle() {
		return getTitle("Storage Type");
	}

	@Override
	public void performDoubleClick() {
		openForm(new FormInput(this), StorageTypeViewForm.ID);
	}

	@Override
	public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
		MenuItem mi = new MenuItem(menu, SWT.PUSH);
		mi.setText("Edit Storage Type");
		mi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				openForm(new FormInput(StorageTypeAdapter.this),
					StorageTypeEntryForm.ID);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		mi = new MenuItem(menu, SWT.PUSH);
		mi.setText("View Storage Type");
		mi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				openForm(new FormInput(StorageTypeAdapter.this),
					StorageTypeViewForm.ID);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	@Override
	public void loadChildren(boolean updateNode) {

	}

	@Override
	public Node accept(NodeSearchVisitor visitor) {
		return null;
	}

}
