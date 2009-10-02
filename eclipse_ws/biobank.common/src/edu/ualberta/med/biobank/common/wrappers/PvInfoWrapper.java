package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.PvInfo;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

// FIXME todo by delphine
public class PvInfoWrapper extends ModelWrapper<PvInfo> {

    public PvInfoWrapper(WritableApplicationService appService,
        PvInfo wrappedObject) {
        super(appService, wrappedObject);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return null;
    }

    @Override
    protected Class<PvInfo> getWrappedClass() {
        return PvInfo.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub
    }

}
