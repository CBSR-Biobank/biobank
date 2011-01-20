package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Researcher;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ResearchGroupWrapper extends ModelWrapper<ResearchGroup> {

    private AddressWrapper address;

    public ResearchGroupWrapper(WritableApplicationService appService,
        ResearchGroup rg) {
        super(appService, rg);
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        wrappedObject.setName(name);
    }

    public String getNameShort() {
        return wrappedObject.getNameShort();
    }

    public void setNameShort(String nameShort) {
        wrappedObject.setNameShort(nameShort);
    }

    @SuppressWarnings("unchecked")
    public List<ResearcherWrapper> getResearcherCollection(boolean sort) {
        List<ResearcherWrapper> researcherCollection = (List<ResearcherWrapper>) propertiesMap
            .get("researcherCollection");
        if (researcherCollection == null) {
            researcherCollection = new ArrayList<ResearcherWrapper>();
            Collection<Researcher> children = wrappedObject
                .getResearcherCollection();
            if (children != null) {
                for (Researcher type : children) {
                    researcherCollection.add(new ResearcherWrapper(appService,
                        type));
                }
                propertiesMap.put("researcherCollection", researcherCollection);
            }
        }
        if ((researcherCollection != null) && sort)
            Collections.sort(researcherCollection);
        return researcherCollection;
    }

    public void setResearcherCollection(
        Collection<Researcher> allResearcherObjects,
        List<ResearcherWrapper> allResearcherWrappers) {
        Collection<Researcher> oldResearchers = wrappedObject
            .getResearcherCollection();
        wrappedObject.setResearcherCollection(allResearcherObjects);
        propertyChangeSupport.firePropertyChange("researcherCollection",
            oldResearchers, allResearcherObjects);
        propertiesMap.put("researcherCollection", allResearcherWrappers);
    }

    public Study getStudy() {
        return wrappedObject.getStudy();
    }

    public void setStudy(Study study) {
        wrappedObject.setStudy(study);
    }

    public AddressWrapper getAddress() {
        if (address == null) {
            Address a = wrappedObject.getAddress();
            if (a == null)
                return null;
            address = new AddressWrapper(appService, a);
        }
        return address;
    }

    public void setAddress(Address address) {
        if (address == null)
            this.address = null;
        else
            this.address = new AddressWrapper(appService, address);
        Address oldAddress = wrappedObject.getAddress();
        wrappedObject.setAddress(address);
        propertyChangeSupport
            .firePropertyChange("address", oldAddress, address);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name", "nameShort", "address", "study" };
    }

    @Override
    public Class<ResearchGroup> getWrappedClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void deleteChecks() throws Exception {
        // TODO Auto-generated method stub

    }

}