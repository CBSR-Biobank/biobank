package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Researcher;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ResearcherWrapper extends ModelWrapper<Researcher> {

    public ResearcherWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ResearcherWrapper(WritableApplicationService appService, Researcher r) {
        super(appService, r);
    }

    @SuppressWarnings("unchecked")
    public List<ResearchGroupWrapper> getResearchGroupCollection(boolean sort) {
        List<ResearchGroupWrapper> researchGroupCollection = (List<ResearchGroupWrapper>) propertiesMap
            .get("researchGroupCollection");
        if (researchGroupCollection == null) {
            researchGroupCollection = new ArrayList<ResearchGroupWrapper>();
            Collection<ResearchGroup> children = wrappedObject
                .getResearchGroupCollection();
            if (children != null) {
                for (ResearchGroup type : children) {
                    researchGroupCollection.add(new ResearchGroupWrapper(
                        appService, type));
                }
                propertiesMap.put("researchGroupCollection",
                    researchGroupCollection);
            }
        }
        if ((researchGroupCollection != null) && sort)
            Collections.sort(researchGroupCollection);
        return researchGroupCollection;
    }

    public void setResearchGroupCollection(
        Collection<ResearchGroup> allResearchGroupObjects,
        List<ResearchGroupWrapper> allResearchGroupWrappers) {
        Collection<ResearchGroup> oldResearchGroups = wrappedObject
            .getResearchGroupCollection();
        wrappedObject.setResearchGroupCollection(allResearchGroupObjects);
        propertyChangeSupport.firePropertyChange("researchGroupCollection",
            oldResearchGroups, allResearchGroupObjects);
        propertiesMap.put("researchGroupCollection", allResearchGroupWrappers);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Researcher> getWrappedClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void deleteChecks() throws Exception {
        // TODO Auto-generated method stub

    }

}