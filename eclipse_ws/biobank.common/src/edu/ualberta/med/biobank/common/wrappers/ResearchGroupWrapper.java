package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.base.ResearchGroupBaseWrapper;
import edu.ualberta.med.biobank.model.ResearchGroup;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ResearchGroupWrapper extends ResearchGroupBaseWrapper {

    public ResearchGroupWrapper(WritableApplicationService appService,
        ResearchGroup rg) {
        super(appService, rg);
    }

    @Override
    public long getCollectionEventCount() throws ApplicationException,
        BiobankException {
        return -1;
    }

    @Override
    public long getCollectionEventCountForStudy(StudyWrapper study)
        throws ApplicationException, BiobankException {
        return -1;
    }

    @Override
    public long getPatientCountForStudy(StudyWrapper study)
        throws ApplicationException, BiobankException {
        return -1;
    }

    @Override
    public Long getPatientCount() throws Exception {
        return (long) -1;
    }

    // TODO: remove this override when all persist()-s are like this!
    @Override
    public void persist() throws Exception {
        WrapperTransaction.persist(this, appService);
    }

    @Override
    public void delete() throws Exception {
        WrapperTransaction.delete(this, appService);
    }
}