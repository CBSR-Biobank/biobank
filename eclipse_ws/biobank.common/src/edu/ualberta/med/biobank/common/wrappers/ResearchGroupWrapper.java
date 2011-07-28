package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.ResearchGroupPeer;
import edu.ualberta.med.biobank.common.wrappers.base.ResearchGroupBaseWrapper;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

@SuppressWarnings("unused")
public class ResearchGroupWrapper extends ResearchGroupBaseWrapper {

    public ResearchGroupWrapper(WritableApplicationService appService,
        ResearchGroup rg) {
        super(appService, rg);
    }

    public ResearchGroupWrapper(WritableApplicationService appService) {
        super(appService);
        // TODO Auto-generated constructor stub
    }

    private static final String ALL_RG_QRY = "from "
        + ResearchGroup.class.getName();

    public static List<ResearchGroupWrapper> getAllResearchGroups(
        WritableApplicationService appService) throws ApplicationException {
        List<ResearchGroupWrapper> wrappers = new ArrayList<ResearchGroupWrapper>();
        HQLCriteria c = new HQLCriteria(ALL_RG_QRY);
        List<ResearchGroup> ResearchGroups = appService.query(c);
        for (ResearchGroup researchGroup : ResearchGroups)
            wrappers.add(new ResearchGroupWrapper(appService, researchGroup));
        return wrappers;
    }

    private static final String RG_COUNT_QRY = "select count (*) from "
        + ResearchGroup.class.getName();

    public static long getCount(WritableApplicationService appService)
        throws BiobankException, ApplicationException {
        return getCountResult(appService, new HQLCriteria(RG_COUNT_QRY));
    }

    private static String AVAIL_STUDIES = "select s from "
        + Study.class.getName() + " s where s not in (select r."
        + ResearchGroupPeer.STUDY.getName() + " from "
        + ResearchGroup.class.getName() + " r)";

    public static List<StudyWrapper> getAvailStudies(
        WritableApplicationService appService) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(AVAIL_STUDIES);
        List<Study> studies = appService.query(criteria);
        List<StudyWrapper> wrappers = new ArrayList<StudyWrapper>();
        for (Study s : studies) {
            wrappers.add(new StudyWrapper(appService, s));
        }
        return wrappers;
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        checkNoDuplicates(Center.class, CenterPeer.NAME.getName(), getName(),
            "A center with name");
        checkNoDuplicates(Center.class, CenterPeer.NAME_SHORT.getName(),
            getNameShort(), "A center with short name");
    }

    @Override
    public long getCollectionEventCountForStudy(StudyWrapper study)
        throws ApplicationException, BiobankException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getPatientCountForStudy(StudyWrapper study)
        throws ApplicationException, BiobankException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Long getPatientCount() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}