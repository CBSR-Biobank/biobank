package edu.ualberta.med.biobank.treeview.admin;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AbstractClinicGroup;

public class ClinicMasterGroup extends AbstractClinicGroup {

    public ClinicMasterGroup(SessionAdapter sessionAdapter, int id) {
        super(sessionAdapter, id, Messages.ClinicMasterGroup_clinics_node_label);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (SessionManager.canCreate(ClinicWrapper.class)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(Messages.ClinicMasterGroup_add_label);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addClinic();
                }
            });
        }
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return ClinicWrapper.getAllClinics(SessionManager.getAppService());
    }

    public void addClinic() {
        ClinicWrapper clinic = new ClinicWrapper(getAppService());
        ClinicAdapter adapter = new ClinicAdapter(this, clinic);
        adapter.openEntryForm();
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return (int) ClinicWrapper.getCount(SessionManager.getAppService());
    }

}
