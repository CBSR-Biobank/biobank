package edu.ualberta.med.biobank.common.action.processingEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.CollectionUtils;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.check.UniquePreCheck;
import edu.ualberta.med.biobank.common.action.check.ValueProperty;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.action.info.CommentInfo;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventCreatePermission;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventUpdatePermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;

public class ProcessingEventSaveAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private Integer peventId;

    private Integer centerId;

    private Date createdAt;

    private String worksheet;

    private Integer statusId;

    private Collection<CommentInfo> comments;

    private Set<Integer> specimenIds;

    public ProcessingEventSaveAction(Integer peventId, Integer centerId,
        Date createdAt, String worksheet, Integer statusId,
        Collection<CommentInfo> comments, Set<Integer> specimenIds) {
        this.peventId = peventId;
        this.centerId = centerId;
        this.createdAt = createdAt;
        this.worksheet = worksheet;
        this.statusId = statusId;
        this.comments = comments;
        this.specimenIds = specimenIds;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Permission permission;
        if (peventId == null) {
            permission = new ProcessingEventCreatePermission();
        } else {
            permission = new ProcessingEventUpdatePermission(peventId);
        }
        return permission.isAllowed(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IdResult run(ActionContext context) throws ActionException {
        if (specimenIds == null) {
            throw new NullPropertyException(ProcessingEvent.class,
                ProcessingEventPeer.SPECIMEN_COLLECTION);
        }

        ProcessingEvent peventToSave;

        if (peventId == null) {
            peventToSave = new ProcessingEvent();
        } else {
            peventToSave = context.load(ProcessingEvent.class, peventId);
        }

        // FIXME Version check?

        // check worksheet number unique. Can't set it as a database constraint
        // since imported pevent can have a null worksheet:
        new UniquePreCheck<ProcessingEvent>(ProcessingEvent.class, peventId,
            Arrays.asList(new ValueProperty<ProcessingEvent>(
                ProcessingEventPeer.WORKSHEET, worksheet))).run(context);

        peventToSave.setActivityStatus(context.load(ActivityStatus.class,
            statusId));
        peventToSave.setCenter(context.load(Center.class, centerId));
        setComments(context, peventToSave);
        peventToSave.setCreatedAt(createdAt);
        peventToSave.setWorksheet(worksheet);

        Map<Integer, Specimen> specimens =
            context.load(Specimen.class, specimenIds);
        SetDifference<Specimen> specimensDiff = new SetDifference<Specimen>(
            peventToSave.getSpecimenCollection(), specimens.values());
        peventToSave.setSpecimenCollection(specimensDiff.getNewSet());

        // set processing event on added specimens
        for (Specimen specimen : specimensDiff.getAddSet()) {
            specimen.setProcessingEvent(peventToSave);
        }

        // remove processing event on removed specimens
        for (Specimen specimen : specimensDiff.getRemoveSet()) {
            specimen.setProcessingEvent(null);
        }

        context.getSession().saveOrUpdate(peventToSave);
        return new IdResult(peventToSave.getId());
    }

    protected void setComments(ActionContext actionContext,
        ProcessingEvent peventToSave) {
        if (comments != null) {
            Collection<Comment> dbComments = CollectionUtils.getCollection(
                peventToSave, ProcessingEventPeer.COMMENT_COLLECTION);
            for (CommentInfo info : comments) {
                Comment commentModel = info.getCommentModel(actionContext);
                dbComments.add(commentModel);
                actionContext.getSession().saveOrUpdate(commentModel);
            }
        }
    }
}
