package edu.ualberta.med.biobank.common.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class DebugUtil {

    public static List<AliquotWrapper> getRandomAliquotsAlreadyLinked(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria =
            new HQLCriteria(
                "from "
                    + Aliquot.class.getName()
                    + " as s where s.patientVisit.clinicPatient.clinic.site.id = ?",
                Arrays.asList(new Object[] { siteId }));
        List<Aliquot> aliquots = appService.query(criteria);
        List<AliquotWrapper> list = new ArrayList<AliquotWrapper>();
        int i = 0;
        for (Aliquot aliquot : aliquots) {
            // return a list of 10 maximum
            if (i == 10) {
                return list;
            }
            list.add(new AliquotWrapper(appService, aliquot));
            i++;
        }
        return list;
    }

    public static List<AliquotWrapper> getRandomAliquotsAlreadyAssigned(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        return getRandomAliquotsAlreadyAssigned(appService, siteId, null);
    }

    public static List<AliquotWrapper> getRandomAliquotsAlreadyAssigned(
        WritableApplicationService appService, Integer siteId, Integer studyId)
        throws ApplicationException {
        List<Object> params = new ArrayList<Object>();
        params.add(siteId);
        String studyString = "";
        if (studyString != null) {
            studyString =
                " and a.patientVisit.clinicPatient.patient.study.id = ?";
            params.add(studyId);
        }

        HQLCriteria criteria =
            new HQLCriteria("from " + Aliquot.class.getName()
                + " as a where a in (select sp.aliquot from "
                + AliquotPosition.class.getName()
                + " as sp) and a.aliquotPosition.container.site.id = ?"
                + studyString, params);
        List<Aliquot> idList = appService.query(criteria);
        List<AliquotWrapper> list = new ArrayList<AliquotWrapper>();
        int i = 0;
        for (Aliquot aliquot : idList) {
            // return a list of 10 maximum
            if (i == 10) {
                return list;
            }
            list.add(new AliquotWrapper(appService, aliquot));
            i++;
        }
        return list;
    }

    public static List<AliquotWrapper> getRandomAliquotsNotAssignedNoDispatch(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria =
            new HQLCriteria(
                "select a from "
                    + Aliquot.class.getName()
                    + " as a left join a.aliquotPosition as ap where ap is null"
                    + " and a.patientVisit.clinicPatient.clinic.site.id = ?"
                    + " and a.activityStatus.name != 'Dispatched'",
                Arrays.asList(new Object[] { siteId }));
        List<Aliquot> aliquots = appService.query(criteria);
        List<AliquotWrapper> list = new ArrayList<AliquotWrapper>();
        for (int i = 0; i < aliquots.size() && i < 100; i++) {
            Aliquot aliquot = aliquots.get(i);
            list.add(new AliquotWrapper(appService, aliquot));
        }
        return list;
    }

    public static List<AliquotWrapper> getRandomAliquotsDispatched(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria =
            new HQLCriteria(
                "from "
                    + Aliquot.class.getName()
                    + " as s where s.patientVisit.shipment.site.id = ? and a.activityStatus.name='Dispatched'",
                Arrays.asList(new Object[] { siteId }));
        List<Aliquot> aliquots = appService.query(criteria);
        List<AliquotWrapper> list = new ArrayList<AliquotWrapper>();
        int i = 0;
        for (Aliquot aliquot : aliquots) {
            // return a list of 10 maximum
            if (i == 10) {
                return list;
            }
            list.add(new AliquotWrapper(appService, aliquot));
            i++;
        }
        return list;
    }

}
