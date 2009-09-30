package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

// FIXME Delphine
public class PvInfoPossibleWrapper extends ModelWrapper<PvInfoPossible> {

    public PvInfoPossibleWrapper(WritableApplicationService appService,
        PvInfoPossible wrappedObject) {
        super(appService, wrappedObject);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void firePropertyChanges(PvInfoPossible oldWrappedObject,
        PvInfoPossible newWrappedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Class<PvInfoPossible> getWrappedClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean checkIntegrity() {
        return true;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        // TODO Auto-generated method stub
    }

}
