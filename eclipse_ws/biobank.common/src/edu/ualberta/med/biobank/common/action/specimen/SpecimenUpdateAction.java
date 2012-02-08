package edu.ualberta.med.biobank.common.action.specimen;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.comment.CommentUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;

public class SpecimenUpdateAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private Integer specimenId;
    private Integer specimenTypeId;
    private Integer collectionEventId;
    private ActivityStatus activityStatus;
    private String commentMessage;

    public void setSpecimenId(Integer specimenId) {
        this.specimenId = specimenId;
    }

    public void setSpecimenTypeId(Integer specimenTypeId) {
        this.specimenTypeId = specimenTypeId;
    }

    public void setCollectionEventId(Integer collectionEventId) {
        this.collectionEventId = collectionEventId;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public void setCommentMessage(String commentMessage) {
        this.commentMessage = commentMessage;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true; // TODO: inappropriate!
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Specimen specimen = context.load(Specimen.class, specimenId);

        SpecimenType specimenType =
            context.load(SpecimenType.class, specimenTypeId);
        specimen.setSpecimenType(specimenType);

        specimen.setActivityStatus(activityStatus);

        Comment comment = addComment(context, specimen);

        updateCollectionEvent(context, specimen, comment);

        return new EmptyResult();
    }

    private Comment addComment(ActionContext context, Specimen specimen) {
        Comment comment = CommentUtil.create(context.getUser(), commentMessage);
        if (comment != null) {
            context.getSession().save(comment);
            specimen.getCommentCollection().add(comment);
        }
        return comment;
    }

    private void updateCollectionEvent(ActionContext context,
        Specimen specimen, Comment comment) {
        if (!specimen.equals(specimen.getTopSpecimen())) return;

        CollectionEvent newCEvent = specimen.getCollectionEvent();
        CollectionEvent oldCEvent =
            context.load(CollectionEvent.class, collectionEventId);

        if (!oldCEvent.equals(newCEvent)) {
            specimen.setCollectionEvent(newCEvent);
            specimen.setOriginalCollectionEvent(newCEvent);

            updateChildSpecimensCEvent(specimen, newCEvent, comment);
        }
    }

    private void updateChildSpecimensCEvent(Specimen specimen,
        CollectionEvent cEvent, Comment comment) {
        for (Specimen childSpecimen : specimen.getChildSpecimenCollection()) {
            specimen.setCollectionEvent(cEvent);

            if (comment != null) {
                specimen.getCommentCollection().add(comment);
            }

            updateChildSpecimensCEvent(childSpecimen, cEvent, comment);
        }
    }
}
