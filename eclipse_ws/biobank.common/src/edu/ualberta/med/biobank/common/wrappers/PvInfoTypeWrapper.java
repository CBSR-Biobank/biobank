package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.PvInfoType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

//FIXME delphine
public class PvInfoTypeWrapper extends ModelWrapper<PvInfoType> {

    public PvInfoTypeWrapper(WritableApplicationService appService,
        PvInfoType wrappedObject) {
        super(appService, wrappedObject);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void firePropertyChanges(PvInfoType oldWrappedObject,
        PvInfoType newWrappedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Class<PvInfoType> getWrappedClass() {
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
