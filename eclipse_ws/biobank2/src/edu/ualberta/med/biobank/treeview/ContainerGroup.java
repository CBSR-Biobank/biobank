package edu.ualberta.med.biobank.treeview;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.forms.ContainerEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerGroup extends AdapterBase {

    public ContainerGroup(SiteAdapter parent, int id) {
        super(parent, id, "Containers", true);
    }

    @Override
    public void performDoubleClick() {
        performExpand();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Add a Container");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                try {
                    List<ContainerTypeWrapper> top = ContainerTypeWrapper
                        .getTopContainerTypesInSite(SessionManager
                            .getAppService(), ((SiteAdapter) parent)
                            .getWrapper());
                    if (top.size() == 0) {
                        MessageDialog
                            .openError(PlatformUI.getWorkbench()
                                .getActiveWorkbenchWindow().getShell(),
                                "Unable to create container",
                                "You must define a top-level container type before initializing storage.");
                    } else {
                        ContainerWrapper c = new ContainerWrapper(
                            SessionManager.getAppService(), new Container());
                        c.setSite(getParentFromClass(SiteAdapter.class)
                            .getWrapper());
                        ContainerAdapter adapter = new ContainerAdapter(
                            ContainerGroup.this, c);
                        openForm(new FormInput(adapter), ContainerEntryForm.ID);
                    }
                } catch (ApplicationException ae) {
                    SessionManager.getLogger().error(
                        "Problem executing add container", ae);
                }
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
            parentSite = ModelUtils.getObjectWithId(getAppService(),
                Site.class, parentSite.getId());
            SiteAdapter siteAdapter = (SiteAdapter) getParent();
            siteAdapter.setSite(parentSite);

            for (ContainerWrapper containerWrapper : siteAdapter.getWrapper()
                .getTopContainerWrapperCollectionSorted()) {
                ContainerAdapter node = (ContainerAdapter) getChild(containerWrapper
                    .getId());
                if (node == null) {
                    node = new ContainerAdapter(this, containerWrapper);
                    addChild(node);
                }
                if (updateNode) {
                    SessionManager.getInstance().updateTreeNode(node);
                }
            }
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while loading storage container group children for site "
                    + parentSite.getName(), e);
        }
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getTitle() {
        return null;
    }

}
