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
        HQLCriteria criteria = new HQLCriteria("from "
            + Aliquot.class.getName()
            + " as s where s.patientVisit.shipment.site.id = ?",
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
        HQLCriteria criteria = new HQLCriteria("from "
            + Aliquot.class.getName()
            + " as s where s in (select sp.aliquot from "
            + AliquotPosition.class.getName()
            + " as sp) and s.patientVisit.shipment.site.id = ?",
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

    public static List<AliquotWrapper> getRandomAliquotsNotAssigned(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(
            "select a from "
                + Aliquot.class.getName()
                + " as a left join a.aliquotPosition as ap where ap is null and a.patientVisit.shipment.site.id = ?",
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
        HQLCriteria criteria = new HQLCriteria(
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
