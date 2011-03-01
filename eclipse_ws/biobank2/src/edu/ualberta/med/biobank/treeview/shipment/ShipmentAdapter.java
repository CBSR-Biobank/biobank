package edu.ualberta.med.biobank.treeview.shipment;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.forms.ShipmentEntryForm;
import edu.ualberta.med.biobank.forms.ShipmentViewForm;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;

@Deprecated
public class ShipmentAdapter extends AdapterBase {

    public ShipmentAdapter(AdapterBase parent, CollectionEventWrapper shipment) {
        super(parent, shipment);
        setHasChildren(true);
    }

    public CollectionEventWrapper getWrapper() {
        return (CollectionEventWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        // CollectionEventWrapper shipment = getWrapper();
        // Assert.isNotNull(shipment, "shipment is null");
        // String label = shipment.getFormattedDateReceived();
        // if (shipment.getWaybill() != null) {
        // label += " (" + shipment.getWaybill() + ")";
        // }
        // return label;
        return null;
    }

    @Override
    public String getTooltipText() {
        CollectionEventWrapper shipment = getWrapper();
        ClinicWrapper clinic = shipment.getClinic();
        if (clinic != null) {
            return clinic.getName() + " - " + getTooltipText("Shipment");
        }
        return getTooltipText("Shipment");
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Shipment");
        addViewMenu(menu, "Shipment");
        addDeleteMenu(menu, "Shipment");
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return "Are you sure you want to delete this shipment?";
    }

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        return findChildFromClass(searchedObject, ProcessingEventWrapper.class);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new CollectionEventAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof ProcessingEventWrapper);
        return new CollectionEventAdapter(this, (CollectionEventWrapper) child);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        // getWrapper().reload();
        // return getWrapper().getSpecimenCollection();
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return getWrapperChildren().size();
    }

    @Override
    public String getEntryFormId() {
        return ShipmentEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return ShipmentViewForm.ID;
    }

    @Override
    public boolean isDeletable() {
        return internalIsDeletable();
    }

}
