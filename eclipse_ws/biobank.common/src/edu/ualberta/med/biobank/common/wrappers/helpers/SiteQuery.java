package edu.ualberta.med.biobank.common.wrappers.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.ContactPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteQuery {

    // due to bug in Hibernate when using elements in query must also use a left
    // join
    @SuppressWarnings("nls")
    private static final String STUDIES_NON_ASSOC_BASE_QRY = "select s from "
        + Study.class.getName() + " s left join s."
        + StudyPeer.SITE_COLLECTION.getName() + " where ";

    @SuppressWarnings("nls")
    public static List<StudyWrapper> getStudiesNotAssoc(SiteWrapper site)
        throws ApplicationException {
        List<StudyWrapper> studyWrappers = new ArrayList<StudyWrapper>();
        StringBuilder qry = new StringBuilder(STUDIES_NON_ASSOC_BASE_QRY)
            .append(site.getId()).append(" not in elements(s.")
            .append(StudyPeer.SITE_COLLECTION.getName()).append(")");
        HQLCriteria c = new HQLCriteria(qry.toString());
        List<Study> results = site.getAppService().query(c);
        for (Study res : results) {
            studyWrappers.add(new StudyWrapper(site.getAppService(), res));
        }
        return studyWrappers;
    }

    @SuppressWarnings("nls")
    private static final String SITES_QRY = "from " + Site.class.getName();

    /**
     * If "id" is null, then all sites are returned. If not, then only sites
     * with that id are returned.
     */
    public static List<SiteWrapper> getSites(
        WritableApplicationService appService, Integer id) throws Exception {
        StringBuilder qry = new StringBuilder(SITES_QRY);
        List<Object> qryParms = new ArrayList<Object>();

        if (id != null) {
            qry.append(" where id = ?"); //$NON-NLS-1$
            qryParms.add(id);
        }

        HQLCriteria criteria = new HQLCriteria(qry.toString(), qryParms);
        List<Site> sites = appService.query(criteria);
        List<SiteWrapper> wrappers = new ArrayList<SiteWrapper>();
        for (Site s : sites) {
            wrappers.add(new SiteWrapper(appService, s));
        }
        return wrappers;
    }

    @SuppressWarnings("nls")
    private static final String TOP_CONTAINERS_QRY = "from "
        + Container.class.getName()
        + " where "
        + Property.concatNames(ContainerPeer.SITE, SitePeer.ID)
        + "=? and "
        + Property.concatNames(ContainerPeer.CONTAINER_TYPE,
            ContainerTypePeer.TOP_LEVEL) + "=true";

    public static List<ContainerWrapper> getTopContainerCollection(
        SiteWrapper site) throws Exception {
        HQLCriteria criteria = new HQLCriteria(TOP_CONTAINERS_QRY,
            Arrays.asList(new Object[] { site.getId() }));
        List<ContainerWrapper> result = new ArrayList<ContainerWrapper>();
        List<Container> rawList = site.getAppService().query(criteria);
        for (Container c : rawList) {
            result.add(new ContainerWrapper(site.getAppService(), c));
        }
        return result;
    }

    /**
     * get all site existing
     */
    public static List<SiteWrapper> getSites(
        WritableApplicationService appService) throws Exception {
        return getSites(appService, null);
    }

    @SuppressWarnings("nls")
    private static final String WORKING_CLINIC_COLLECTION_SIZE = "select distinct contact."
        + ContactPeer.CLINIC.getName()
        + " from "
        + Site.class.getName()
        + " as site "
        + "inner join site."
        + SitePeer.STUDY_COLLECTION.getName()
        + " as study "
        + "inner join study."
        + StudyPeer.CONTACT_COLLECTION.getName()
        + " as contact where site." + SitePeer.ID.getName() + "=?";

    /**
     * Use an HQL query to quickly get the size of the collection.
     * 
     * @return The number of clinics associated to this repository stie.
     * @throws ApplicationException
     */
    public static int getWorkingClinicCollectionSize(SiteWrapper site)
        throws ApplicationException {
        HQLCriteria c = new HQLCriteria(WORKING_CLINIC_COLLECTION_SIZE,
            Arrays.asList(new Object[] { site.getId() }));
        List<Clinic> clinics = site.getAppService().query(c);
        return clinics.size();
    }

    @SuppressWarnings("nls")
    private static final String PATIENT_COUNT_QRY = "select count(distinct cevent."
        + CollectionEventPeer.PATIENT.getName()
        + ") from "
        + Specimen.class.getName()
        + " as spcs join spcs."
        + SpecimenPeer.COLLECTION_EVENT.getName()
        + " as cevent where spcs."
        + Property.concatNames(SpecimenPeer.CURRENT_CENTER, CenterPeer.ID)
        + "=?";

    public static Long getPatientCount(SiteWrapper site) throws Exception {
        HQLCriteria criteria = new HQLCriteria(PATIENT_COUNT_QRY,
            Arrays.asList(new Object[] { site.getId() }));
        return ModelWrapper.getCountResult(site.getAppService(), criteria);
    }

    @SuppressWarnings("nls")
    public static final String PATIENT_COUNT_FOR_STUDY_QRY = "select count(distinct patient) from "
        + Specimen.class.getName()
        + " as specimens join specimens."
        + Property.concatNames(SpecimenPeer.COLLECTION_EVENT,
            CollectionEventPeer.PATIENT)
        + " as patient where specimens."
        + Property.concatNames(SpecimenPeer.CURRENT_CENTER, CenterPeer.ID)
        + "=? and patient."
        + Property.concatNames(PatientPeer.STUDY, StudyPeer.ID) + "=?";

    public static long getPatientCountForStudy(SiteWrapper site,
        StudyWrapper study) throws ApplicationException, BiobankException {
        HQLCriteria c = new HQLCriteria(PATIENT_COUNT_FOR_STUDY_QRY,
            Arrays.asList(new Object[] { site.getId(), study.getId() }));
        return ModelWrapper.getCountResult(site.getAppService(), c);
    }
}
