package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.PvSampleSource;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

//FIXME delphine
public class PvSampleSourceWrapper extends ModelWrapper<PvSampleSource> {

    public PvSampleSourceWrapper(WritableApplicationService appService,
        PvSampleSource wrappedObject) {
        super(appService, wrappedObject);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void firePropertyChanges(PvSampleSource oldWrappedObject,
        PvSampleSource newWrappedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Class<PvSampleSource> getWrappedClass() {
        return PvSampleSource.class;
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
