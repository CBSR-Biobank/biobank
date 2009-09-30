package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.PvInfoData;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

//FIXME todo by delphine
public class PvInfoDataWrapper extends ModelWrapper<PvInfoData> {

    public PvInfoDataWrapper(WritableApplicationService appService,
        PvInfoData wrappedObject) {
        super(appService, wrappedObject);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void firePropertyChanges(PvInfoData oldWrappedObject,
        PvInfoData newWrappedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Class<PvInfoData> getWrappedClass() {
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
