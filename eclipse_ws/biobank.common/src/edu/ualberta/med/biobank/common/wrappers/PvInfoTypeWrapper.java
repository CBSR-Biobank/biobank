package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.PvInfoType;
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
        return PvInfoType.class;
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
