package edu.ualberta.med.biobank.treeview.shipment;

import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;

public class ClinicWithShipmentAdapter extends ClinicAdapter {

    public ClinicWithShipmentAdapter(AdapterBase parent,
        ClinicWrapper clinicWrapper) {
        super(parent, clinicWrapper);
    }

    @Override
    public AdapterBase search(Object searchedObject) {
        if (searchedObject instanceof ShipmentWrapper) {
            return getChild((ModelWrapper<?>) searchedObject, true);
        }
        return searchChildren(searchedObject);
    }
}
