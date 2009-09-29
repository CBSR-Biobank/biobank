package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.model.PvSampleSource;
import gov.nih.nci.system.applicationservice.ApplicationException;
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

    @Override
    protected DatabaseResult deleteChecks() throws ApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

}
