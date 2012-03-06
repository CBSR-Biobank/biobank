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
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return findChildFromClass(searchedClass, objectId,
            OriginInfoWrapper.class);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        return false;
    }

}
