package edu.ualberta.med.biobank.common.wrappers;

import java.util.List;

import edu.ualberta.med.biobank.model.SpecimenLink;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

//FIXME check implementation
public class SpecimenLinkWrapper extends ModelWrapper<SpecimenLink> {

    public SpecimenLinkWrapper(WritableApplicationService appService) {
        super(appService);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<SpecimenLink> getWrappedClass() {
        // TODO Auto-generated method stub
        return null;
    }

}
