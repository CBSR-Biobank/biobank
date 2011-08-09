package edu.ualberta.med.biobank.common.wrappers.checks;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class CapacityPostPersistChecks extends LoadCheck<Capacity> {
    private static final String ROW_COL_CAPACITY_INVALID_MSG = "Row capacity and column capacity must be greater than or equal to zero.";

    private static final long serialVersionUID = 1L;

    public CapacityPostPersistChecks(ModelWrapper<Capacity> wrapper) {
        super(wrapper);
    }

    @Override
    public void doCheck(Session session, Capacity capacity)
        throws BiobankSessionException {
        if (capacity.getRowCapacity() <= 0 || capacity.getColCapacity() <= 0) {
            throw new BiobankSessionException(ROW_COL_CAPACITY_INVALID_MSG);
        }
    }
}
