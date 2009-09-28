package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.List;

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
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.ContainerTypeEntryForm;
import edu.ualberta.med.biobank.forms.ContainerTypeViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerTypeAdapter extends AdapterBase {

    public ContainerTypeAdapter(AdapterBase parent, ContainerType containerType) {
        super(parent, containerType);
        this.setContainerType(containerType);
    }

    public void setContainerType(ContainerType containerType) {
        object = containerType;
    }

    public ContainerType getContainerType() {
        return (ContainerType) object;
    }

    @Override
    public String getName() {
        ContainerType containerType = getContainerType();
        Assert.isNotNull(containerType, "storage type is null");
        return containerType.getName();
    }

    @Override
    public String getTitle() {
        return getTitle("Container Type");
    }

    @Override
    public void performDoubleClick() {
        openForm(new FormInput(this), ContainerTypeViewForm.ID);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Edit Container Type");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(ContainerTypeAdapter.this),
                    ContainerTypeEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("View Container Type");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(ContainerTypeAdapter.this),
                    ContainerTypeViewForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Delete Container Type");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                Boolean confirm = MessageDialog.openConfirm(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Confirm Delete",
                    "Are you sure you want to delete this container type?");

                if (confirm) {
                    delete();
                }

            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public void delete() {
        // FIXME when wrapper is used : remove this method to use the
        // parent one
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            ContainerType ct = getContainerType();
            SDKQuery query = new DeleteExampleQuery(ct);

            public void run() {

                if (!HQLSafeToDelete()) {
                    BioBankPlugin
                        .openError(
                            "Error",
                            "Unable to delete container type "
                                + ct.getName()
                                + ". A container of this type exists in storage. Remove all instances before deleting this type.");
                } else {
                    try {
                        getAppService().executeQuery(query);
                        ContainerTypeAdapter.this.getParent().removeChild(
                            ContainerTypeAdapter.this);
                    } catch (ApplicationException e) {
                        BioBankPlugin.openAsyncError("Delete error", e);
                    }
                }
            }
        });
    }

    private boolean HQLSafeToDelete() {
        String queryString = "select c.containerType from "
            + Container.class.getName() + " as c where c.containerType.id=?)";
        List<Object> params = new ArrayList<Object>();
        params.add(getContainerType().getId());
        HQLCriteria c = new HQLCriteria(queryString);
        c.setParameters(params);
        List<Object> results = new ArrayList<Object>();
        try {
            results = getAppService().query(c);
        } catch (ApplicationException e) {
            SessionManager.getLogger().error("HQLSafeToDelete error", e);
            return false;
        }
        return (results.size() == 0);
    }

    @Override
    public void loadChildren(boolean updateNode) {

    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return null;
    }

}
