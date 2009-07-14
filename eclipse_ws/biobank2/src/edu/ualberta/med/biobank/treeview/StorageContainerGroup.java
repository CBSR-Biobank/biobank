package edu.ualberta.med.biobank.treeview;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.StorageContainerEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.ModelUtils;
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
	public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
		MenuItem mi = new MenuItem(menu, SWT.PUSH);
		mi.setText("Add a Storage Container");
		mi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				StorageContainerAdapter adapter = new StorageContainerAdapter(
					StorageContainerGroup.this, ModelUtils
						.newStorageContainer(null));
				openForm(new FormInput(adapter), StorageContainerEntryForm.ID);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	@Override
	public void loadChildren(boolean updateNode) {
		Site parentSite = ((SiteAdapter) getParent()).getSite();
		Assert.isNotNull(parentSite, "site null");
		try {
			// read from database again
			parentSite = (Site) ModelUtils.getObjectWithId(getAppService(),
				Site.class, parentSite.getId());
			((SiteAdapter) getParent()).setSite(parentSite);

			List<StorageContainer> containers = ModelUtils
				.getTopContainersForSite(getAppService(), parentSite);
			for (StorageContainer storageContainer : containers) {
				StorageContainerAdapter node = (StorageContainerAdapter) getChild(storageContainer
					.getId());
				if (node == null) {
					node = new StorageContainerAdapter(this, storageContainer);
					addChild(node);
				}
				if (updateNode) {
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
