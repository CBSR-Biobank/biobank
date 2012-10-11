package edu.ualberta.med.biobank.common.action.processingEvent;

import java.util.Date;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.comment.CommentUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventCreatePermission;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventUpdatePermission;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.Person;

public class ProcessingEventSaveAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private ProcessingEvent pevent;

    private Center center;

    private Date createdAt;

    private String worksheet;

    private ActivityStatus activityStatus;

    private String commentText;

    private Set<Integer> addedSpecimenIds;

    private Set<Integer> removedSpecimenIds;

    private String technician;

    public ProcessingEventSaveAction(ProcessingEvent pevent, Center center,
        Date createdAt, String worksheet, ActivityStatus activityStatus,
        String commentText, Set<Integer> addedSpecimenIds,
        Set<Integer> removedSpecimenIds) {
        this.pevent = pevent;
        this.center = center;
        this.createdAt = createdAt;
        this.worksheet = worksheet;
        this.activityStatus = activityStatus;
        this.commentText = commentText;
        this.addedSpecimenIds = addedSpecimenIds;
        this.removedSpecimenIds = removedSpecimenIds;
        this.technician = null;
    }

    public ProcessingEventSaveAction(ProcessingEvent pevent, Center center,
        Date createdAt, String worksheet, ActivityStatus activityStatus,
        String commentText, Set<Integer> addedSpecimenIds,
        Set<Integer> removedSpecimenIds, String technician) {
        this.pevent = pevent;
        this.center = center;
        this.createdAt = createdAt;
        this.worksheet = worksheet;
        this.activityStatus = activityStatus;
        this.commentText = commentText;
        this.addedSpecimenIds = addedSpecimenIds;
        this.removedSpecimenIds = removedSpecimenIds;
        this.technician = technician;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Permission permission;
        if (pevent == null) {
            permission =
                new ProcessingEventCreatePermission(center);
        } else {
            permission = new ProcessingEventUpdatePermission(pevent);
        }
        return permission.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        ProcessingEvent peventToSave;

        if (pevent == null) {
            peventToSave = new ProcessingEvent();
        } else {
            peventToSave = context.load(ProcessingEvent.class, pevent.getId());
        }

        peventToSave.setActivityStatus(activityStatus);
        peventToSave.setCenter(context.load(Center.class, center.getId()));
        setComments(context, peventToSave);
        peventToSave.setCreatedAt(createdAt);
        peventToSave.setWorksheet(worksheet);
        Person technicianPerson = new Person();
        technicianPerson.setName(technician);
        peventToSave.setProcessedBy(technicianPerson);

        // set processing event on added specimens
        for (Integer specimen : addedSpecimenIds) {
            Specimen spec = context.load(Specimen.class, specimen);
            spec.setProcessingEvent(peventToSave);
            spec.setActivityStatus(ActivityStatus.CLOSED);
            context.getSession().saveOrUpdate(spec);
        }

        for (Integer specimen : removedSpecimenIds) {
            Specimen spec = context.load(Specimen.class, specimen);
            spec.setProcessingEvent(null);
            spec.setActivityStatus(ActivityStatus.ACTIVE);
            context.getSession().saveOrUpdate(spec);
        }

        context.getSession().saveOrUpdate(peventToSave);
        return new IdResult(peventToSave.getId());
    }

    protected void setComments(ActionContext context,
        ProcessingEvent peventToSave) {
        Comment comment = CommentUtil.create(context.getUser(), commentText);
        if (comment != null) {
            context.getSession().save(comment);
            peventToSave.getComments().add(comment);
        }
    }
}
