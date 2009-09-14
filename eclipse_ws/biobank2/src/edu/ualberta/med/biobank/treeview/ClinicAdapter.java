package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.ClinicEntryForm;
import edu.ualberta.med.biobank.forms.ClinicViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Clinic;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.DeleteExampleQuery;

public class ClinicAdapter extends AdapterBase {

    public ClinicAdapter(AdapterBase parent, Clinic clinic) {
        super(parent, clinic, Clinic.class);
    }

    public void setClinic(Clinic clinic) {
        setWrappedObject(clinic, Clinic.class);
    }

    public Clinic getClinic() {
        return (Clinic) getWrappedObject();
    }

    @Override
    protected Integer getWrappedObjectId() {
        return getClinic().getId();
    }

    @Override
    public void addChild(AdapterBase child) {
        Assert.isTrue(false, "Cannot add children to this adapter");
    }

    @Override
    public Integer getId() {
        Clinic clinic = getClinic();
        Assert.isNotNull(clinic, "Clinic is null");
        return clinic.getId();
    }

    @Override
    public String getName() {
        Clinic clinic = getClinic();
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

    public void delete() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            Clinic clinic = getClinic();
            SDKQuery query = new DeleteExampleQuery(clinic);

            public void run() {
                if (clinic.getPatientVisitCollection().size() > 0) {
                    BioBankPlugin
                        .openError(
                            "Error",
                            "Unable to delete clinic "
                                + clinic.getName()
                                + ". All defined patient visits must be removed first.");
                } else
                    try {
                        getAppService().executeQuery(query);
                        // TODO update tree
                    } catch (ApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

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

    public Clinic loadClinic() throws Exception {
        return (Clinic) loadWrappedObject();
    }

    @Override
    protected boolean integrityCheck() {
        return true;
    }

}
