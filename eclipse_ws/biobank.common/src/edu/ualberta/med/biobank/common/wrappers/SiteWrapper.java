package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SiteWrapper extends ModelWrapper<Site> {

    public SiteWrapper(WritableApplicationService appService, Site wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected void firePropertyChanges(Site oldValue, Site wrappedObject2) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void internalReload() throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    protected DatabaseResult persistChecks() throws ApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

}
