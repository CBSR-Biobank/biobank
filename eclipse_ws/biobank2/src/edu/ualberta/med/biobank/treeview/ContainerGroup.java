package edu.ualberta.med.biobank.treeview;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.ContainerEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;

public class ContainerGroup extends AdapterBase {

    private static Logger LOGGER = Logger.getLogger(ContainerGroup.class
        .getName());

    public ContainerGroup(SiteAdapter parent, int id) {
        super(parent, id, "Containers", true);
    }

    @Override
    public void performDoubleClick() {
        performExpand();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (SessionManager.canCreate(ContainerWrapper.class)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Add a Container");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
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
                                SessionManager.getAppService());
                            c.setSite(getParentFromClass(SiteAdapter.class)
                                .getWrapper());
                            ContainerAdapter adapter = new ContainerAdapter(
                                ContainerGroup.this, c);
                            openForm(new FormInput(adapter),
                                ContainerEntryForm.ID);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Problem executing add container", e);
                    }
                }
            });
        }
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof ContainerWrapper);
        return new ContainerAdapter(this, (ContainerWrapper) child);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        SiteWrapper parentSite = ((SiteAdapter) getParent()).getWrapper();
        Assert.isNotNull(parentSite, "site null");
        parentSite.reload();
        return parentSite.getTopContainerCollection();
    }

    @Override
    public void notifyListeners(AdapterChangedEvent event) {
        getParent().notifyListeners(event);
    }
}
