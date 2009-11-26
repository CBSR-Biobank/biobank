package edu.ualberta.med.biobank.treeview;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.forms.PatientVisitEntryForm;
import edu.ualberta.med.biobank.forms.ShipmentEntryForm;
import edu.ualberta.med.biobank.forms.ShipmentViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;

public class ShipmentAdapter extends AdapterBase {

    private static Logger LOGGER = Logger.getLogger(ShipmentAdapter.class
        .getName());

    public ShipmentAdapter(AdapterBase parent, ShipmentWrapper shipment) {
        super(parent, shipment);
        setHasChildren(true);
    }

    public ShipmentWrapper getWrapper() {
        return (ShipmentWrapper) modelObject;
    }

    @Override
    public String getName() {
        ShipmentWrapper shipment = getWrapper();
        Assert.isNotNull(shipment.getWrappedObject(), "shipment is null");
        return shipment.getFormattedDateShipped();
    }

    @Override
    public String getTitle() {
        return getTitle("Shipment");
    }

    @Override
    public void loadChildren(boolean updateNode) {
        try {
            ShipmentWrapper shipment = getWrapper();
            // read from database again
            shipment.reload();

            List<PatientVisitWrapper> visits = shipment
                .getPatientVisitCollection();
            for (PatientVisitWrapper visit : visits) {
                PatientVisitAdapter node = (PatientVisitAdapter) getChild(visit
                    .getId());
                if (node == null) {
                    node = new PatientVisitAdapter(this, visit);
                    addChild(node);
                }
                if (updateNode) {
                    SessionManager.getInstance().updateTreeNode(node);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while loading children of shipment "
                + getWrapper().getFormattedDateShipped(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void performDoubleClick() {
        openForm(new FormInput(this), ShipmentViewForm.ID);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Edit Shipment");
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(ShipmentAdapter.this),
                    ShipmentEntryForm.ID);
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("View Shipment");
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(ShipmentAdapter.this),
                    ShipmentEntryForm.ID);
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Add Patient Visit");
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                PatientVisitAdapter adapter = new PatientVisitAdapter(
                    ShipmentAdapter.this, new PatientVisitWrapper(
                        getAppService()));
                adapter.getWrapper().setPatient(
                    ((PatientAdapter) getParent()).getWrapper());
                openForm(new FormInput(adapter), PatientVisitEntryForm.ID);
            }
        });
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return null;
    }

}
