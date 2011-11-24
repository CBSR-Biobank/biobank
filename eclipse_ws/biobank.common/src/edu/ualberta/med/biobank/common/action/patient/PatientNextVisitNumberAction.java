package edu.ualberta.med.biobank.common.action.patient;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.User;

public class PatientNextVisitNumberAction implements
    Action<PatientNextVisitNumberResult> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String NEXT_NUMBER_QRY =
        "select coalesce(max(ce.visitNumber),0) from "
            + CollectionEvent.class.getName() + " ce where ce.patient.id=?";

    private Integer patientId;

    public PatientNextVisitNumberAction(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true;
    }

    @Override
    public PatientNextVisitNumberResult run(User user, Session session)
        throws ActionException {
        Query query = session.createQuery(NEXT_NUMBER_QRY);
        query.setParameter(0, patientId);

        @SuppressWarnings("unchecked")
        List<Integer> rows = query.list();

        Integer nextVisitNumber = null;
        if (rows.size() == 0) {
            nextVisitNumber = 1;
        } else {
            nextVisitNumber = rows.get(0) + 1;
        }

        return new PatientNextVisitNumberResult(nextVisitNumber);
    }

}
