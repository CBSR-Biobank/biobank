package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.ContainerTypeEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;

public class ContainerTypeGroup extends AdapterBase {

    public ContainerTypeGroup(SiteAdapter parent, int id) {
        super(parent, id, "Container Types", true);
    }

    @Override
    protected String getNameInternal() {
        return null;
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Add Container Type");
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                ContainerTypeWrapper ct = new ContainerTypeWrapper(
                    getAppService());
                ct.setSite(getParentFromClass(SiteAdapter.class).getWrapper());
                ContainerTypeAdapter adapter = new ContainerTypeAdapter(
                    ContainerTypeGroup.this, ct);
                openForm(new FormInput(adapter), ContainerTypeEntryForm.ID);
            }
        });
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
    protected AdapterBase createChildNode() {
        return new ContainerTypeAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof ContainerTypeWrapper);
        return new ContainerTypeAdapter(this, (ContainerTypeWrapper) child);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        SiteWrapper currentSite = ((SiteAdapter) getParent()).getWrapper();
        Assert.isNotNull(currentSite, "null site");
        currentSite.reload();
        return new ArrayList<ContainerTypeWrapper>(currentSite
            .getContainerTypeCollection());
    }

    @Override
    public void notifyListeners(AdapterChangedEvent event) {
        getParent().notifyListeners(event);
    }

}
