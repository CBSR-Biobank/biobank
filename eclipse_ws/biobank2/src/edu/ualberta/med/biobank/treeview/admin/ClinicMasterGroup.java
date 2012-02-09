package edu.ualberta.med.biobank.treeview.admin;

import java.util.ArrayList;
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
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractClinicGroup;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicMasterGroup extends AbstractClinicGroup {

    private static BgcLogger LOGGER = BgcLogger
        .getLogger(ClinicMasterGroup.class.getName());

    private ClinicsInfo clinicsInfo;

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
    public void performExpand() {
        try {
            clinicsInfo = SessionManager.getAppService().doAction(
                new ClinicGetAllAction());
            super.performExpand();
        } catch (ApplicationException e) {
            // TODO: open an error dialog here?
            LOGGER.error("BioBankFormBase.createPartControl Error", e); //$NON-NLS-1$            
        }
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        List<ClinicWrapper> result = new ArrayList<ClinicWrapper>();

        for (Clinic clinic : clinicsInfo.getClinics()) {
            ClinicWrapper wrapper =
                new ClinicWrapper(SessionManager.getAppService(), clinic);
            result.add(wrapper);
        }

        return result;
    }

    public void addClinic() {
        ClinicWrapper clinic =
            new ClinicWrapper(SessionManager.getAppService());
        ClinicAdapter adapter = new ClinicAdapter(this, clinic);
        adapter.openEntryForm();
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return (int) ClinicWrapper.getCount(SessionManager.getAppService());
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        return 0;
    }
}
