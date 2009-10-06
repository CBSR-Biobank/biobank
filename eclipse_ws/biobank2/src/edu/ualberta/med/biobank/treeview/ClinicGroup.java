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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.ClinicEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Clinic;

public class ClinicGroup extends AdapterBase {

    public ClinicGroup(SiteAdapter parent, int id) {
        super(parent, id, "Clinics", true);
    }

    @Override
    public void performDoubleClick() {
        performExpand();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Add Clinic");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                Clinic clinic = new Clinic();
                clinic.setSite(getParentFromClass(SiteAdapter.class).getSite());
                ClinicAdapter clinicAdapter = new ClinicAdapter(
                    ClinicGroup.this,
                    new ClinicWrapper(getAppService(), clinic));
                FormInput input = new FormInput(clinicAdapter);
                try {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage().openEditor(input, ClinicEntryForm.ID,
                            true);
                } catch (PartInitException exp) {
                    exp.printStackTrace();
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {
        SiteWrapper currentSite = ((SiteAdapter) getParent()).getWrapper();
        Assert.isNotNull(currentSite, "null site");

        try {
            // read from database again
            currentSite.reload();
            ((SiteAdapter) getParent()).setSite(currentSite.getWrappedObject());

            List<ClinicWrapper> clinics = currentSite.getClinicCollection(true);
            if (clinics != null)
                for (ClinicWrapper clinic : clinics) {
                    ClinicAdapter node = (ClinicAdapter) getChild(clinic
                        .getId());

                    if (node == null) {
                        node = new ClinicAdapter(this, clinic);
                        addChild(node);
                    }
                    if (updateNode) {
                        SessionManager.getInstance().updateTreeNode(node);
                    }
                }
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while loading clinic group children for site "
                    + currentSite.getName(), e);
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
