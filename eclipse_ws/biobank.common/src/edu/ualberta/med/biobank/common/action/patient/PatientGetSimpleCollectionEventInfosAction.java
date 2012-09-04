package edu.ualberta.med.biobank.common.action.patient;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.MapResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.patient.PatientGetSimpleCollectionEventInfosAction.SimpleCEventInfo;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;

public class PatientGetSimpleCollectionEventInfosAction implements
    Action<MapResult<Integer, SimpleCEventInfo>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String CEVENT_INFO_QRY =
        "SELECT cevent.id, COUNT(DISTINCT sourcesSpecs), MIN(sourcesSpecs.createdAt)"
            + " FROM " + CollectionEvent.class.getName() + " cevent"
            + " LEFT JOIN cevent.originalSpecimens sourcesSpecs"
            + " WHERE cevent.id=?"
            + " GROUP BY cevent.id";

    private final Integer patientId;

    public static class SimpleCEventInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;
        public CollectionEvent cevent;
        public Long sourceSpecimenCount;
        public Date minSourceSpecimenDate;
    }

    public PatientGetSimpleCollectionEventInfosAction(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @Override
    public MapResult<Integer, SimpleCEventInfo> run(ActionContext context)
        throws ActionException {
        HashMap<Integer, SimpleCEventInfo> ceventInfos =
            new HashMap<Integer, SimpleCEventInfo>();

        @SuppressWarnings("nls")
        Criteria criteria = context.getSession()
            .createCriteria(Patient.class, "p")
            .add(Restrictions.eq("id", patientId));

        Patient patient = (Patient) criteria.uniqueResult();

        for (CollectionEvent cevent : patient.getCollectionEvents()) {
            SimpleCEventInfo ceventInfo = new SimpleCEventInfo();
            ceventInfo.cevent = cevent;
            ceventInfos.put(cevent.getId(), ceventInfo);
        }

        for (SimpleCEventInfo ceventInfo : ceventInfos.values()) {
            Query query = context.getSession().createQuery(CEVENT_INFO_QRY);
            query.setParameter(0, ceventInfo.cevent.getId());

            Object[] row = (Object[]) query.uniqueResult();

            if (row == null) {
                throw new NullPointerException(
                    "collection event not found in query result"); //$NON-NLS-1$
            }

            ceventInfo.sourceSpecimenCount = (Long) row[1];
            ceventInfo.minSourceSpecimenDate = (Date) row[2];
        }

        return new MapResult<Integer, SimpleCEventInfo>(ceventInfos);
    }
}
