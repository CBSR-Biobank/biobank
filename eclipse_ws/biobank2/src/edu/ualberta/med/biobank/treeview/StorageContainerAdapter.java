package edu.ualberta.med.biobank.treeview;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.StorageContainerEntryForm;
import edu.ualberta.med.biobank.forms.StorageContainerViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageContainer;

public class StorageContainerAdapter extends Node {

	private StorageContainer storageContainer;

	public StorageContainerAdapter(Node parent,
			StorageContainer storageContainer) {
		super(parent);
		this.storageContainer = storageContainer;
		setHasChildren(true);
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
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				loadChildren();
				SessionManager.getInstance().getTreeViewer().expandToLevel(
					StorageContainerAdapter.this, 1);
			}
		});
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

		mi.setText("Add a Storage Container");
		mi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				StorageContainer newStorageContainer = new StorageContainer();
				ContainerPosition position = new ContainerPosition();
				position.setParentContainer(storageContainer);
				position.setOccupiedContainer(newStorageContainer);
				newStorageContainer.setLocatedAtPosition(position);
				StorageContainerAdapter adapter = new StorageContainerAdapter(
					StorageContainerAdapter.this, newStorageContainer);
				openForm(new FormInput(adapter), StorageContainerEntryForm.ID);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	@Override
	public void loadChildren() {
		try {
			// read from database again
			StorageContainer searcSC = new StorageContainer();
			searcSC.setId(storageContainer.getId());
			List<StorageContainer> result = getAppService().search(
				StorageContainer.class, searcSC);
			Assert.isTrue(result.size() == 1);
			storageContainer = result.get(0);

			for (ContainerPosition childPosition : storageContainer
				.getOccupiedPositions()) {
				StorageContainer child = childPosition.getOccupiedContainer();
				StorageContainerAdapter node = (StorageContainerAdapter) getChild(child
					.getId());

				if (node == null) {
					node = new StorageContainerAdapter(this, child);
					addChild(node);
				}
				SessionManager.getInstance().getTreeViewer().update(node, null);
			}
		} catch (Exception e) {
			SessionManager.getLogger().error(
				"Error while loading storage container group children for storage container "
						+ storageContainer.getName(), e);
		}
	}

	@Override
	public Node accept(NodeSearchVisitor visitor) {
		return null;
	}

	public Site getSite() {
		Node parent = getParent();
		if (parent instanceof StorageContainerAdapter) {
			return ((StorageContainerAdapter) parent).getSite();
		} else if (parent instanceof StorageContainerGroup) {
			return ((SiteAdapter) ((StorageContainerGroup) parent).getParent())
				.getSite();
		}
		return null;
	}

}
