package edu.ualberta.med.biobank.treeview.shipment;

import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;

public class ClinicWithShipmentAdapter extends ClinicAdapter {

    public ClinicWithShipmentAdapter(AdapterBase parent,
        ClinicWrapper clinicWrapper) {
        super(parent, clinicWrapper);
    }

    @Override
    public List<AbstractAdapterBase> search(Object searchedObject) {
        return findChildFromClass(searchedObject, OriginInfoWrapper.class);
    }
}
