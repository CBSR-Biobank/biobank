package edu.ualberta.med.biobank.common.action.cevent;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventSaveAction implements Action<CollectionEventInfo> {

    private static final long serialVersionUID = 1L;

    private Integer ceventId;
    private Integer patientId;
    private Integer visitNumber;
    private Integer statusId;
    private String comments;

    // FIXME pvdata
    // FIXME source specimens

    public CollectionEventSaveAction(Integer ceventId, Integer patientId,
        Integer visitNumber, Integer statusId, String comments) {
        this.ceventId = ceventId;
        this.patientId = patientId;
        this.visitNumber = visitNumber;
        this.statusId = statusId;
        this.comments = comments;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public CollectionEventInfo doAction(Session session) throws ActionException {
        CollectionEvent ceventToSave;
        if (ceventId == null) {
            ceventToSave = new CollectionEvent();
        } else {
            // retrieve original cevent
            // FIXME enough info retrieved from this get method?
            CollectionEventInfo ceventinfo = new GetCollectionEventInfoAction(
                ceventId).doAction(session);
            ceventToSave = ceventinfo.cevent;
        }
        ceventToSave.setPatient((Patient) session
            .load(Patient.class, patientId));
        ceventToSave.setVisitNumber(visitNumber);
        ceventToSave.setActivityStatus((ActivityStatus) session.load(
            ActivityStatus.class, statusId));
        ceventToSave.setComment(comments);

        session.saveOrUpdate(ceventToSave);

        return new GetCollectionEventInfoAction(ceventToSave.getId())
            .doAction(session);
    }
}
