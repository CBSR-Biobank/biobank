package edu.ualberta.med.biobank.common.wrappers.internal;

import edu.ualberta.med.biobank.common.wrappers.base.SpecimenAttrBaseWrapper;
import edu.ualberta.med.biobank.model.SpecimenAttr;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SpecimenAttrWrapper extends SpecimenAttrBaseWrapper {

    public SpecimenAttrWrapper(WritableApplicationService appService,
        SpecimenAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    public SpecimenAttrWrapper(WritableApplicationService appService) {
        super(appService);
    }

}
