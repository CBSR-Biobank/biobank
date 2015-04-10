package edu.ualberta.med.biobank.common.action.collectionEvent;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventReadPermission;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;

public class CollectionEventGetSourceSpecimensAction implements Action<ListResult<Specimen>> {
    private static final long serialVersionUID = 1L;

    private final Integer ceventId;
    private final Integer peventId;
    private final Boolean notFlagged;

    public CollectionEventGetSourceSpecimensAction(CollectionEvent cevent, ProcessingEvent pevent) {
        this.ceventId = cevent.getId();
        this.peventId = pevent.getId();
        this.notFlagged = false;
    }

    public CollectionEventGetSourceSpecimensAction(CollectionEvent cevent, ProcessingEvent pevent,
        boolean notFlagged) {
        this.ceventId = cevent.getId();
        this.peventId = pevent.getId();
        this.notFlagged = notFlagged;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new CollectionEventReadPermission(ceventId).isAllowed(context);
    }

    @SuppressWarnings({ "nls", "unchecked" })
    @Override
    public ListResult<Specimen> run(ActionContext context) throws ActionException {
        Criteria criteria = context.getSession().createCriteria(Specimen.class, "spc")
            .createAlias("spc.collectionEvent", "cevent")
            .add(Restrictions.eq("cevent.id", ceventId))
            .createAlias("spc.processingEvent", "pevent")
            .add(Restrictions.eq("pevent.id", peventId));

        if (notFlagged) {
            criteria.add(Restrictions.ne("activityStatus", ActivityStatus.FLAGGED));
        }

        List<Specimen> specimens = criteria.list();

        // need to load other objects required by application
        for (Specimen spc : specimens) {
            for (SpecimenType spcType : spc.getSpecimenType().getChildSpecimenTypes()) {
                spcType.getName();
            }
            spc.getTopSpecimen().getOriginInfo().getCenter().getName();

            // study needs to be loaded when used by SpecimenLinkAndAssign
            spc.getCollectionEvent().getPatient().getStudy().getName();
        }

        return new ListResult<Specimen>(specimens);
    }

}
