package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.PvInfoData;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

//FIXME todo by delphine
public class PvInfoDataWrapper extends ModelWrapper<PvInfoData> {

    public PvInfoDataWrapper(WritableApplicationService appService,
        PvInfoData wrappedObject) {
        super(appService, wrappedObject);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return null;
    }

    @Override
    protected Class<PvInfoData> getWrappedClass() {
        // TODO Auto-generated method stub
        return PvInfoData.class;
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
