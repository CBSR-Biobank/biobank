package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.SpecimenLinkBaseWrapper;
import edu.ualberta.med.biobank.model.SpecimenLink;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SpecimenLinkWrapper extends SpecimenLinkBaseWrapper {

    public SpecimenLinkWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public SpecimenLinkWrapper(WritableApplicationService appService,
        SpecimenLink wrappedObject) {
        super(appService, wrappedObject);
    }
}
