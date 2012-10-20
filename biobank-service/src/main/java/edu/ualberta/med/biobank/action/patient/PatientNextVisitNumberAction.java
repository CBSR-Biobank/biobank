package edu.ualberta.med.biobank.action.patient;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.model.study.CollectionEvent;

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
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @Override
    public PatientNextVisitNumberResult run(ActionContext context)
        throws ActionException {
        Query query = context.getSession().createQuery(NEXT_NUMBER_QRY);
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
