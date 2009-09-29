package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.Collections;
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
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.forms.ClinicEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.ClinicComparator;
import edu.ualberta.med.biobank.model.Site;

public class ClinicGroup extends AdapterBase {

    public ClinicGroup(SiteAdapter parent, int id) {
        super(parent, null, null, id, "Clinics", true);
    }

    @Override
    protected Integer getWrappedObjectId() {
        Assert.isTrue(false, "Should not be invoked for this type of adatper");
        return null;
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
                    ClinicGroup.this, clinic);
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
        Site currentSite = ((SiteAdapter) getParent()).getSite();
        Assert.isNotNull(currentSite, "null site");

        try {
            // read from database again
            currentSite = ModelUtils.getObjectWithId(getAppService(),
                Site.class, currentSite.getId());
            ((SiteAdapter) getParent()).setSite(currentSite);

            List<Clinic> clinics = new ArrayList<Clinic>(currentSite
                .getClinicCollection());
            Collections.sort(clinics, new ClinicComparator());
            for (Clinic clinic : clinics) {
                ClinicAdapter node = (ClinicAdapter) getChild(clinic.getId());

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

    @Override
    protected boolean integrityCheck() {
        return true;
    }

}
