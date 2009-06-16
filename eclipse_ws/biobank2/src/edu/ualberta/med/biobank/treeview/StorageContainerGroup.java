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
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageContainer;

public class StorageContainerGroup extends Node {

	public StorageContainerGroup(SiteAdapter parent, int id) {
		super(parent, id, "Storage Containers", true);
	}

	@Override
	public void performDoubleClick() {
		performExpand();
	}

	@Override
	public void performExpand() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				loadChildren();
				SessionManager.getInstance().getTreeViewer().expandToLevel(
					StorageContainerGroup.this, 1);
			}
		});
	}

	@Override
	public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
		MenuItem mi = new MenuItem(menu, SWT.PUSH);
		mi.setText("Add a Storage Container");
		mi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				StorageContainerAdapter adapter = new StorageContainerAdapter(
					StorageContainerGroup.this, new StorageContainer());
				openForm(new FormInput(adapter), StorageContainerEntryForm.ID);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	@Override
	public void loadChildren() {
		Site parentSite = ((SiteAdapter) getParent()).getSite();
		Assert.isNotNull(parentSite, "site null");
		try {
			// read from database again
			Site searchSite = new Site();
			searchSite.setId(parentSite.getId());
			List<Site> result = getAppService().search(Site.class, searchSite);
			Assert.isTrue(result.size() == 1);
			parentSite = result.get(0);
			((SiteAdapter) getParent()).setSite(parentSite);

			for (StorageContainer storageContainer : parentSite
				.getStorageContainerCollection()) {

				if (storageContainer.getLocatedAtPosition() == null
						|| storageContainer.getLocatedAtPosition()
							.getParentContainer() == null) {
					StorageContainerAdapter node = (StorageContainerAdapter) getChild(storageContainer
						.getId());

					if (node == null) {
						node = new StorageContainerAdapter(this,
							storageContainer);
						addChild(node);
					}
					SessionManager.getInstance().getTreeViewer().update(node,
						null);
				}
			}
		} catch (Exception e) {
			SessionManager.getLogger().error(
				"Error while loading storage container group children for site "
						+ parentSite.getName(), e);
		}
	}

	@Override
	public Node accept(NodeSearchVisitor visitor) {
		return visitor.visit(this);
	}
}
