package edu.ualberta.med.biobank.action.processingEvent;

import java.util.Date;
import java.util.Set;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.comment.CommentUtil;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.permission.processingEvent.ProcessingEventCreatePermission;
import edu.ualberta.med.biobank.permission.processingEvent.ProcessingEventUpdatePermission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.ActivityStatus;

public class ProcessingEventSaveAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private Integer peventId;

    private Integer centerId;

    private Date createdAt;

    private String worksheet;

    private ActivityStatus activityStatus;

    private String commentText;

    private Set<Integer> addedSpecimenIds;

    private Set<Integer> removedSpecimenIds;

    public ProcessingEventSaveAction(Integer peventId, Integer centerId,
        Date createdAt, String worksheet, ActivityStatus activityStatus,
        String commentText, Set<Integer> addedSpecimenIds,
        Set<Integer> removedSpecimenIds) {
        this.peventId = peventId;
        this.centerId = centerId;
        this.createdAt = createdAt;
        this.worksheet = worksheet;
        this.activityStatus = activityStatus;
        this.commentText = commentText;
        this.addedSpecimenIds = addedSpecimenIds;
        this.removedSpecimenIds = removedSpecimenIds;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Permission permission;
        if (peventId == null) {
            permission =
                new ProcessingEventCreatePermission(centerId);
        } else {
            permission = new ProcessingEventUpdatePermission(peventId);
        }
        return permission.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        ProcessingEvent peventToSave;

        if (peventId == null) {
            peventToSave = new ProcessingEvent();
        } else {
            peventToSave = context.load(ProcessingEvent.class, peventId);
        }

        // FIXME Version check?

        peventToSave.setActivityStatus(activityStatus);
        peventToSave.setCenter(context.load(Center.class, centerId));
        setComments(context, peventToSave);
        peventToSave.setCreatedAt(createdAt);
        peventToSave.setWorksheet(worksheet);

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
