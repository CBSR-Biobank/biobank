package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.DispatchShipmentReceivingEntryForm;
import edu.ualberta.med.biobank.forms.DispatchShipmentSendingEntryForm;
import edu.ualberta.med.biobank.forms.DispatchShipmentViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;

public class DispatchShipmentAdapter extends AdapterBase {

    public DispatchShipmentAdapter(AdapterBase parent,
        DispatchShipmentWrapper ship) {
        super(parent, ship);
    }

    public DispatchShipmentWrapper getWrapper() {
        return (DispatchShipmentWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        DispatchShipmentWrapper shipment = getWrapper();
        Assert.isNotNull(shipment, "shipment is null");
        String label = new String();
        StudyWrapper study = shipment.getStudy();

        if (study != null) {
            label += study.getNameShort() + " - ";
        }

        label += shipment.getFormattedDateShipped();
        return label;

    }

    @Override
    public String getTooltipText() {
        return getTooltipText("Dispatch Shipment");
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addViewMenu(menu, "Dispatch Shipment");
        if (SessionManager.canUpdate(DispatchShipmentWrapper.class)) {
            if (SessionManager.getInstance().getCurrentSite()
                .equals(((DispatchShipmentWrapper) modelObject).getReceiver())) {
                MenuItem mi = new MenuItem(menu, SWT.PUSH);
                mi.setText("Process reception");
                mi.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        openForm(new FormInput(DispatchShipmentAdapter.this,
                            false), DispatchShipmentReceivingEntryForm.ID);
                    }
                });
            }
            if (SessionManager.getInstance().getCurrentSite()
                .equals(((DispatchShipmentWrapper) modelObject).getSender())) {
                addEditMenu(menu, "Shipment");
            }
        }
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getViewFormId() {
        return DispatchShipmentViewForm.ID;
    }

    @Override
    public String getEntryFormId() {
        SiteWrapper currentSite = SessionManager.getInstance().getCurrentSite();
        if (currentSite.equals(((DispatchShipmentWrapper) modelObject)
            .getSender()))
            return DispatchShipmentSendingEntryForm.ID;
        return DispatchShipmentReceivingEntryForm.ID;
    }

}
