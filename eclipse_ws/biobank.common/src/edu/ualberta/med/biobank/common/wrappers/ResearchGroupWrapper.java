package edu.ualberta.med.biobank.common.wrappers;

import java.util.Collection;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.base.ResearchGroupBaseWrapper;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ResearchGroupWrapper extends ResearchGroupBaseWrapper {

    public ResearchGroupWrapper(WritableApplicationService appService,
        ResearchGroup rg) {
        super(appService, rg);
    }

    public ResearchGroupWrapper(WritableApplicationService appService) {
        super(appService);
        // TODO Auto-generated constructor stub
    }

    public static Collection<? extends ModelWrapper<?>> getAllResearchGroups(
        BiobankApplicationService appService) {
        // TODO Auto-generated method stub
        return null;
    }

    public static int getCount(BiobankApplicationService appService) {
        // TODO Auto-generated method stub
        return 0;
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