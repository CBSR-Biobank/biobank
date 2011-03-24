package edu.ualberta.med.biobank.common.wrappers.internal;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CapacityBaseWrapper;
import edu.ualberta.med.biobank.model.Capacity;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class CapacityWrapper extends CapacityBaseWrapper {

    public CapacityWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public CapacityWrapper(WritableApplicationService appService,
        Capacity wrappedObject) {
        super(appService, wrappedObject);
    }

    public void setRow(Integer rowCapacity) {
        setRowCapacity(rowCapacity);
    }

    public void setCol(Integer colCapacity) {
        setColCapacity(colCapacity);
    }

    @Override
    public int compareTo(ModelWrapper<Capacity> cap) {
        if (cap instanceof CapacityWrapper) {
            Integer rowCapacity1 = wrappedObject.getRowCapacity();
            Integer rowCapacity2 = cap.getWrappedObject().getRowCapacity();
            int compare = rowCapacity1.compareTo(rowCapacity2);
            if (compare == 0) {
                Integer colCapacity1 = wrappedObject.getColCapacity();
                Integer colCapacity2 = cap.getWrappedObject().getColCapacity();

                return ((colCapacity1.compareTo(colCapacity2) > 0) ? 1
                    : (colCapacity1.equals(colCapacity2) ? 0 : -1));

            }
            return (compare > 0) ? 1 : -1;
        }
        return 0;
    }

}
