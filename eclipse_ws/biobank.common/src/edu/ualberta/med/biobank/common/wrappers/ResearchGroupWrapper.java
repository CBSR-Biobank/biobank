package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.base.ResearchGroupBaseWrapper;
import edu.ualberta.med.biobank.model.ResearchGroup;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

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

    public void reload() {
        // TODO Auto-generated method stub

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

    public Boolean getSendsShipments() {
        // TODO Auto-generated method stub
        return null;
    }

}