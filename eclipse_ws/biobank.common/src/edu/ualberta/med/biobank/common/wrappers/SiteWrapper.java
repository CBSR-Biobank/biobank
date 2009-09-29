package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SiteWrapper extends ModelWrapper<Site> implements
    Comparable<SiteWrapper> {

    public SiteWrapper(WritableApplicationService appService, Site wrappedObject) {
        super(appService, wrappedObject);
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        wrappedObject.setName(name);
    }

    @Override
    public boolean checkIntegrity() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected DatabaseResult deleteChecks() throws ApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void firePropertyChanges(Site oldWrappedObject,
        Site newWrappedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Class<Site> getWrappedClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected DatabaseResult persistChecks() throws ApplicationException {
        // TODO Auto-generated method stub
        return null;
    }

    public int compareTo(SiteWrapper wrapper) {
        String myName = wrappedObject.getName();
        String wrapperName = wrapper.wrappedObject.getName();
        return ((myName.compareTo(wrapperName) > 0) ? 1 : (myName
            .equals(wrapperName) ? 0 : -1));
    }

    public Collection<Study> getStudyCollection() {
        return null;
    }

    public Collection<ClinicWrapper> getClinicWrapperCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    public AddressWrapper getAddressWrapper() {
        // TODO Auto-generated method stub
        return null;
    }

    private boolean checkSiteNameUnique() throws ApplicationException {
        HQLCriteria c = new HQLCriteria("from " + Site.class.getName()
            + " where name = ?", Arrays.asList(new Object[] { getName() }));

        List<Object> results = appService.query(c);
        if (results.size() == 0)
            return true;

        // BioBankPlugin.openAsyncError("Site Name Problem",
        // "A site with name \""
        // + siteWrapper.getName() + "\" already exists.");
        return false;
    }

}
