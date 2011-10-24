package edu.ualberta.med.biobank.common.action.processingEvent;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.CollectionUtils;
import edu.ualberta.med.biobank.common.action.DiffUtils;
import edu.ualberta.med.biobank.common.action.check.UniquePreCheck;
import edu.ualberta.med.biobank.common.action.check.ValueProperty;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

public class ProcessingEventSaveAction implements Action<Integer> {

    private static final long serialVersionUID = 1L;

    private Integer peventId;

    private Integer centerId;

    private Date createdAt;

    private String worksheet;

    private Integer statusId;

    private String comments;

    private List<Integer> specimenIds;

    public ProcessingEventSaveAction(Integer peventId, Integer centerId,
        Date createdAt, String worksheet, Integer statusId, String comments,
        List<Integer> specimenIds) {
        this.peventId = peventId;
        this.centerId = centerId;
        this.createdAt = createdAt;
        this.worksheet = worksheet;
        this.statusId = statusId;
        this.comments = comments;
        this.specimenIds = specimenIds;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Integer run(User user, Session session) throws ActionException {
        ProcessingEvent peventToSave;
        if (peventId == null) {
            peventToSave = new ProcessingEvent();
        } else {
            peventToSave = ActionUtil.sessionGet(session,
                ProcessingEvent.class, peventId);
        }

        // FIXME Version check?
        // FIXME permission ?

        // check worksheet number unique. Can't set it as a database constraint
        // since imported pevent can have a null worksheet
        new UniquePreCheck<ProcessingEvent>(new ValueProperty<ProcessingEvent>(
            ProcessingEventPeer.ID, peventId), ProcessingEvent.class,
            Arrays.asList(new ValueProperty<ProcessingEvent>(
                ProcessingEventPeer.WORKSHEET, worksheet))).run(user, session);

        peventToSave.setActivityStatus(ActionUtil.sessionGet(session,
            ActivityStatus.class, statusId));
        peventToSave.setCenter(ActionUtil.sessionGet(session, Center.class,
            centerId));
        peventToSave.setComment(comments);
        peventToSave.setCreatedAt(createdAt);
        peventToSave.setWorksheet(worksheet);

        DiffUtils<Specimen> specUtil = new DiffUtils<Specimen>(
            CollectionUtils.getCollection(peventToSave,
                ProcessingEventPeer.SPECIMEN_COLLECTION));
        if (specimenIds != null)
            for (Integer spcId : specimenIds) {
                Specimen spc = ActionUtil.sessionGet(session, Specimen.class,
                    spcId);
                spc.setProcessingEvent(peventToSave);
                specUtil.add(spc);
            }
        for (Specimen spc : specUtil.pullRemoved()) {
            spc.setProcessingEvent(null);
        }

        session.saveOrUpdate(peventToSave);

        return peventToSave.getId();
    }
}
