package edu.ualberta.med.biobank.common.action.study;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.check.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class StudyPreDeleteChecks {

    private final Study study;

    public StudyPreDeleteChecks(Study study) {
        this.study = study;
    }

    public void run(User user, Session session) {
        new CollectionIsEmptyCheck<Study>(
            Study.class, study, StudyPeer.PATIENT_COLLECTION,
            study.getNameShort(), null).run(user, session);
    }

}
