package edu.ualberta.med.biobank.treeview.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.dispatch.OrderAdapter;

public class FilledOrderNode extends AdapterBase {

    public FilledOrderNode(AdapterBase parent, int id) {
        super(parent, id, "Filled", true, false);
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

    @Override
    public String getTooltipText() {
        return null;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (SessionManager.canCreate(DispatchWrapper.class, null)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Add Dispatch");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    // addDispatch();
                }
            });
        }
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return new OrderAdapter(this, (DispatchWrapper) child);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        StudyWrapper s = new StudyWrapper(SessionManager.getAppService());
        s.setNameShort("HEART");
        DispatchWrapper fake = new DispatchWrapper(
            SessionManager.getAppService());
        fake.setStudy(s);
        List<DispatchWrapper> list = new ArrayList<DispatchWrapper>();
        list.add(fake);
        return list;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public void rebuild() {
        for (AdapterBase adaper : getChildren()) {
            adaper.rebuild();
        }
    }

    @Override
    public AdapterBase search(Object searchedObject) {
        return searchChildren(searchedObject);
    }

}
