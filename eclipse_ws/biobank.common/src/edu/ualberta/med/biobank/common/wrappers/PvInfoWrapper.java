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
    protected void firePropertyChanges(PvInfo oldWrappedObject,
        PvInfo newWrappedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Class<PvInfo> getWrappedClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean checkIntegrity() {
        return true;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub
    }

}
