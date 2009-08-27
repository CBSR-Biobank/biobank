package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.forms.ClinicEntryForm;
import edu.ualberta.med.biobank.forms.ClinicViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Clinic;

public class ClinicAdapter extends AdapterBase {

    private Clinic clinic;

    public ClinicAdapter(AdapterBase parent, Clinic clinic) {
        super(parent);
        this.clinic = clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Clinic getClinic() {
        return clinic;
    }

    @Override
    public void addChild(AdapterBase child) {
        Assert.isTrue(false, "Cannot add children to this adapter");
    }

    @Override
    public Integer getId() {
        Assert.isNotNull(clinic, "Clinic is null");
        return clinic.getId();
    }

    @Override
    public String getName() {
        Assert.isNotNull(clinic, "Clinic is null");
        return clinic.getName();
    }

    @Override
    public String getTitle() {
        return getTitle("Clinic");
    }

    @Override
    public void performDoubleClick() {
        openForm(new FormInput(this), ClinicViewForm.ID);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Edit Clinic");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(ClinicAdapter.this), ClinicEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("View Clinic");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(ClinicAdapter.this), ClinicViewForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {

    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return null;
    }

    public Clinic loadClinic() {
        try {
            clinic = ModelUtils.getObjectWithId(getAppService(), Clinic.class,
                clinic.getId());
            Assert.isNotNull(clinic, "clinic not in database");
            return clinic;
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while retrieving the clinic", e);
        }
        return null;
    }

}
