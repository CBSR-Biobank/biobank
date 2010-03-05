package edu.ualberta.med.biobank.common.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class DebugUtil {

    public static List<SampleWrapper> getRandomSamplesAlreadyLinked(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from " + Sample.class.getName()
            + " as s where s.patientVisit.patient.study.site.id = ?", Arrays
            .asList(new Object[] { siteId }));
        List<Sample> samples = appService.query(criteria);
        List<SampleWrapper> list = new ArrayList<SampleWrapper>();
        int i = 0;
        for (Sample sample : samples) {
            // return a list of 10 maximum
            if (i == 10) {
                return list;
            }
            list.add(new SampleWrapper(appService, sample));
            i++;
        }
        return list;
    }

    public static List<SampleWrapper> getRandomSamplesAlreadyAssigned(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from " + Sample.class.getName()
            + " as s where s in (select sp.sample from "
            + SamplePosition.class.getName()
            + " as sp) and s.patientVisit.patient.study.site.id = ?", Arrays
            .asList(new Object[] { siteId }));
        List<Sample> samples = appService.query(criteria);
        List<SampleWrapper> list = new ArrayList<SampleWrapper>();
        int i = 0;
        for (Sample sample : samples) {
            // return a list of 10 maximum
            if (i == 10) {
                return list;
            }
            list.add(new SampleWrapper(appService, sample));
            i++;
        }
        return list;
    }

    public static List<SampleWrapper> getRandomSamplesNotAssigned(
        WritableApplicationService appService, Integer siteId)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from " + Sample.class.getName()
            + " as s where s not in (select sp.sample from "
            + SamplePosition.class.getName()
            + " as sp) and s.patientVisit.patient.study.site.id = ?", Arrays
            .asList(new Object[] { siteId }));
        List<Sample> samples = appService.query(criteria);
        List<SampleWrapper> list = new ArrayList<SampleWrapper>();
        for (Sample sample : samples) {
            list.add(new SampleWrapper(appService, sample));
        }
        return list;
    }

}
