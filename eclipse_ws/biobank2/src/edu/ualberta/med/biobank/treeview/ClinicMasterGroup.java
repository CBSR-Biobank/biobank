package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

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

public class ClinicMasterGroup extends AbstractClinicGroup {

    public ClinicMasterGroup(SessionAdapter sessionAdapter, int id) {
        super(sessionAdapter, id, "Clinics Master");
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (SessionManager.canCreate(ClinicWrapper.class)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Add Clinic");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addClinic(SessionManager.getInstance().getSession(), false);
                }
            });
        }
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return ClinicWrapper.getAllClinics(SessionManager.getAppService());
    }

    public static void addClinic(SessionAdapter sessAdapter,
        boolean hasPreviousForm) {
        ClinicWrapper clinic = new ClinicWrapper(sessAdapter.getAppService());
        ClinicAdapter adapter = new ClinicAdapter(
            sessAdapter.getClinicGroupNode(), clinic);
        adapter.openEntryForm(hasPreviousForm);
    }

}
