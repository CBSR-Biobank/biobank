package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.ShipmentEntryForm;
import edu.ualberta.med.biobank.forms.ShipmentViewForm;

public class ShipmentAdapter extends AdapterBase {

    public ShipmentAdapter(AdapterBase parent, ShipmentWrapper shipment) {
        super(parent, shipment);
        setHasChildren(true);
    }

    public ShipmentWrapper getWrapper() {
        return (ShipmentWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        ShipmentWrapper shipment = getWrapper();
        Assert.isNotNull(shipment, "shipment is null");
        return shipment.getWaybill() + " - "
            + shipment.getFormattedDateShipped();
    }

    @Override
    public String getTooltipText() {
        ShipmentWrapper shipment = getWrapper();
        ClinicWrapper clinic = shipment.getClinic();
        if (clinic != null) {
            SiteWrapper site = clinic.getSite();
            return site.getNameShort() + " - " + clinic.getName() + " - "
                + getTooltipText("Shipment");
        }
        return getTooltipText("Shipment");
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Shipment");
        addViewMenu(menu, "Shipment");
        addDeleteMenu(menu, "Shipment",
            "Are you sure you want to delete this shipment?");
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new PatientVisitAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof PatientVisitWrapper);
        return new PatientVisitAdapter(this, (PatientVisitWrapper) child);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        getWrapper().reload();
        return getWrapper().getPatientVisitCollection();
    }

    @Override
    public String getEntryFormId() {
        return ShipmentEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return ShipmentViewForm.ID;
    }

}
