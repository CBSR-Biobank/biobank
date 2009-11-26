package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.forms.ClinicEntryForm;
import edu.ualberta.med.biobank.forms.ClinicViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;

public class ClinicAdapter extends AdapterBase {

    public ClinicAdapter(AdapterBase parent, ClinicWrapper clinicWrapper) {
        super(parent, clinicWrapper);
    }

    public ClinicAdapter(AdapterBase parent, ClinicWrapper clinicWrapper,
        boolean enableActions) {
        super(parent, clinicWrapper, enableActions);
    }

    public ClinicWrapper getWrapper() {
        return (ClinicWrapper) modelObject;
    }

    @Override
    public String getName() {
        ClinicWrapper wrapper = getWrapper();
        Assert.isNotNull(wrapper.getWrappedObject(), "client is null");
        return wrapper.getName();
    }

    @Override
    public String getTitle() {
        return getTitle("Patient");
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
        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Delete Clinic");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                Boolean confirm = MessageDialog.openConfirm(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Confirm Delete",
                    "Are you sure you want to delete this clinic?");

                if (confirm) {
                    delete();
                }

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

}
