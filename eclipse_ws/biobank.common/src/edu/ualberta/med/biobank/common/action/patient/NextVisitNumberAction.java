package edu.ualberta.med.biobank.common.action.patient;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.User;

public class NextVisitNumberAction implements Action<Integer> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String NEXT_NUMBER_QRY = "select coalesce(max(ce.visitNumber),0) from "
        + CollectionEvent.class.getName() + " ce where ce.patient.id=?";

    private Integer patientId;

    public NextVisitNumberAction(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        Query query = session.createQuery(NEXT_NUMBER_QRY);
        query.setParameter(0, patientId);

        @SuppressWarnings("unchecked")
        List<Integer> rows = query.list();
        if (rows.size() == 0)
            return 1;

        return rows.get(0) + 1;
    }

}
