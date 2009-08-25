package edu.ualberta.med.biobank.common.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteUtils {

    private static final Logger logger = Logger.getLogger(SiteUtils.class
        .getName());

    public static Study getStudyInSite(WritableApplicationService appService,
        String nameShort, Site site) {
        HQLCriteria criteria = new HQLCriteria("from " + Study.class.getName()
            + " where nameShort = ? and site = ?", Arrays.asList(new Object[] {
            nameShort, site }));
        List<Study> studies;
        try {
            studies = appService.query(criteria);
            if (studies.size() == 1) {
                return studies.get(0);
            }
        } catch (ApplicationException e) {
            logger.error("Problem while queriyng a study", e);
        }
        return null;
    }

    public static List<Sample> getSamplesInSite(
        WritableApplicationService appService, String inventoryId, Site site) {
        HQLCriteria criteria = new HQLCriteria("from " + Sample.class.getName()
            + " where inventoryId = ? and patientVisit.patient.study.site = ?",
            Arrays.asList(new Object[] { inventoryId, site }));
        try {
            return appService.query(criteria);
        } catch (ApplicationException e) {
            logger.error("Problem while queriyng a sample", e);
        }
        return null;
    }

    public static Patient getPatientInSite(
        WritableApplicationService appService, String patientNumber, Site site) {
        HQLCriteria criteria = new HQLCriteria("from "
            + Patient.class.getName() + " where study.site = ? and number = ?",
            Arrays.asList(new Object[] { site, patientNumber }));
        List<Patient> patients;
        try {
            patients = appService.query(criteria);
            if (patients.size() == 1) {
                return patients.get(0);
            }
        } catch (ApplicationException e) {
            logger.error("Problem while queriyng a patient", e);
        }
        return null;
    }

    public static List<Container> getContainersInSite(
        WritableApplicationService appService, Site site, String label)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName() + " where site = ? and label = ?",
            Arrays.asList(new Object[] { site, label }));
        return appService.query(criteria);
    }

    public static Container getContainerWithTypeInSite(
        WritableApplicationService appService, Site site, String label,
        String startWithTypeName) throws ApplicationException {
        List<Container> containers = getContainersInSite(appService, site,
            label);
        if (containers.size() == 1) {
            return containers.get(0);
        } else {
            // this is start of the container type name
            if (startWithTypeName != null) {
                HQLCriteria criteria = new HQLCriteria("from "
                    + ContainerType.class.getName() + " where name like '"
                    + startWithTypeName + "%'");
                List<ContainerType> cTypes = appService.query(criteria);
                if (cTypes.size() > 0) {
                    for (Container c : containers) {
                        if (c.getContainerType().getId().equals(
                            cTypes.get(0).getId())) {
                            return c;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Collection<ContainerType> getTopContainerTypesInSite(
        WritableApplicationService appService, Site site) {
        HQLCriteria criteria = new HQLCriteria("from "
            + ContainerType.class.getName()
            + " where site = ? and topLevel=true", Arrays
            .asList(new Object[] { site }));
        try {
            return appService.query(criteria);
        } catch (ApplicationException e) {
            logger.error("Problem while queriyng a containers", e);
        }
        return Collections.emptyList();
    }
}
