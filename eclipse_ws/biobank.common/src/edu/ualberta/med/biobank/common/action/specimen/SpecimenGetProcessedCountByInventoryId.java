package edu.ualberta.med.biobank.common.action.specimen;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.CountResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenGetProcessedCountByInventoryId implements
    Action<CountResult> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory
        .getLogger(SpecimenGetProcessedCountByInventoryId.class.getName());

    @SuppressWarnings("nls")
    private static final String SPECIMEN_PROCESSED_COUNT_HQL =
        "SELECT COUNT(*)"
            + " FROM " + Specimen.class.getName() + " spec"
            + " WHERE spec.inventoryId = ? AND spec.processingEvent IS NOT NULL";

    private final String inventoryId;

    @SuppressWarnings("nls")
    public SpecimenGetProcessedCountByInventoryId(String inventoryId) {
        log.debug("inventoryId={}", inventoryId);
        if (inventoryId == null) {
            throw new IllegalArgumentException();
        }
        this.inventoryId = inventoryId;
    }

    @SuppressWarnings("nls")
    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        boolean result =
            PermissionEnum.BATCH_OPERATIONS.isAllowed(context.getUser());
        log.debug("isAllowed: inventoryId={} allowed={}", inventoryId, result);
        return result;
    }

    @Override
    public CountResult run(ActionContext context)
        throws ActionException {
        log.debug("run: inventoryId={}", inventoryId);

        Query query =
            context.getSession().createQuery(SPECIMEN_PROCESSED_COUNT_HQL);
        query.setParameter(0, inventoryId);
        return new CountResult((Long) (query.list().get(0)));
    }

}
