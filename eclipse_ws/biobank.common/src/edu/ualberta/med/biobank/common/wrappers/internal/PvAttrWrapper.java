package edu.ualberta.med.biobank.common.wrappers.internal;

import edu.ualberta.med.biobank.common.wrappers.base.PvAttrBaseWrapper;
import edu.ualberta.med.biobank.model.PvAttr;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PvAttrWrapper extends PvAttrBaseWrapper {

    public PvAttrWrapper(WritableApplicationService appService,
        PvAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    public PvAttrWrapper(WritableApplicationService appService) {
        super(appService);
    }

    }

