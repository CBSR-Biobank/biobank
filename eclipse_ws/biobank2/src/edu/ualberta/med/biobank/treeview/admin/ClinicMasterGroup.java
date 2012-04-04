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
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetAllAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetAllAction.ClinicsInfo;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractClinicGroup;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicMasterGroup extends AbstractClinicGroup {

    @SuppressWarnings("unused")
    private static BgcLogger LOGGER = BgcLogger
        .getLogger(ClinicMasterGroup.class.getName());

    private ClinicsInfo clinicsInfo = null;

    private boolean createAllowed;

    public ClinicMasterGroup(SessionAdapter sessionAdapter, int id) {
        super(sessionAdapter, id, "All Clinics");
        try {
            this.createAllowed =
                SessionManager.getAppService().isAllowed(
                    new ClinicCreatePermission());
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Error", "Unable to retrieve permissions");
        }
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (createAllowed) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Add Clinic");
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
        clinicsInfo = SessionManager.getAppService().doAction(
            new ClinicGetAllAction());

        return ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
            clinicsInfo.getClinics(), ClinicWrapper.class);
    }

    public void addClinic() {
        ClinicWrapper clinic =
            new ClinicWrapper(SessionManager.getAppService());
        ClinicAdapter adapter = new ClinicAdapter(this, clinic);
        adapter.openEntryForm();
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        return 0;
    }
}
