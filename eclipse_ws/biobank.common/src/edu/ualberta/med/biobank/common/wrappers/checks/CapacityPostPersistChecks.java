package edu.ualberta.med.biobank.common.wrappers.checks;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.actions.LoadModelAction;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class CapacityPostPersistChecks extends LoadModelAction<Capacity> {
    private static final String ROW_COL_CAPACITY_INVALID_MSG = Messages
        .getString("CapacityPostPersistChecks.row.column.capacity.error.msg"); //$NON-NLS-1$

    private static final long serialVersionUID = 1L;

    public CapacityPostPersistChecks(ModelWrapper<Capacity> wrapper) {
        super(wrapper);
    }

    @Override
    public void doLoadModelAction(Session session, Capacity capacity)
        throws BiobankSessionException {
        if (capacity.getRowCapacity() <= 0 || capacity.getColCapacity() <= 0) {
            throw new BiobankSessionException(ROW_COL_CAPACITY_INVALID_MSG);
        }
    }
}
