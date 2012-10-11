package edu.ualberta.med.biobank.common.action.processingEvent;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.CountResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class ProcessingEventGetCountByWorksheetAction implements
    Action<CountResult> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory
        .getLogger(ProcessingEventGetCountByWorksheetAction.class.getName());

    @SuppressWarnings("nls")
    private static final String PROCESSING_EVENT_COUNT_HQL =
        "SELECT COUNT(*)"
            + " FROM " + ProcessingEvent.class.getName() + " pe"
            + " WHERE pe.worksheet = ?";

    private final String worksheet;

    @SuppressWarnings("nls")
    public ProcessingEventGetCountByWorksheetAction(String worksheet) {
        log.debug("worksheet={}", worksheet);
        if (worksheet == null) {
            throw new IllegalArgumentException();
        }
        this.worksheet = worksheet;
    }

    @SuppressWarnings("nls")
    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        boolean result =
            PermissionEnum.BATCH_OPERATIONS.isAllowed(context.getUser());
        log.debug("isAllowed: worksheet={} allowed={}", worksheet, result);
        return result;
    }

    @Override
    public CountResult run(ActionContext context)
        throws ActionException {
        log.debug("run: worksheet={}", worksheet);

        Query query =
            context.getSession().createQuery(PROCESSING_EVENT_COUNT_HQL);
        query.setParameter(0, worksheet);
        return new CountResult((Long) (query.list().get(0)));
    }

}
