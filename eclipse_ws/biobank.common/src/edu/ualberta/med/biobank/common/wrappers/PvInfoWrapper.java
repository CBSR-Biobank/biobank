package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.model.PvInfo;
import gov.nih.nci.system.applicationservice.ApplicationException;
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
    protected DatabaseResult persistChecks() throws ApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean checkIntegrity() {
        return true;
    }

}
